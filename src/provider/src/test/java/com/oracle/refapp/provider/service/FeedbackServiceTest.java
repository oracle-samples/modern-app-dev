/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import static com.oracle.refapp.provider.TestUtils.TEST_FEEDBACK_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_FEEDBACK_RATING;
import static com.oracle.refapp.provider.TestUtils.TEST_FEEDBACK_TEXT;
import static com.oracle.refapp.provider.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.provider.TestUtils.TEST_PAGE;
import static com.oracle.refapp.provider.TestUtils.TEST_PATIENT_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.mappers.ProviderMapperImpl;
import com.oracle.refapp.provider.models.CreateFeedbackDetailsRequest;
import com.oracle.refapp.provider.models.FeedbackCollection;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedbackServiceTest {

  private final FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);
  private final ProviderMapper mapper = new ProviderMapperImpl();
  private final FeedbackService feedbackService = new FeedbackService(feedbackRepository, mapper);

  @Test
  @DisplayName("test List feedbacks endpoint")
  void testListFeedbacks() {
    Page<FeedbackEntity> page = Page.of(List.of(TEST_FEEDBACK_ENTITY), Pageable.from(TEST_PAGE, TEST_LIMIT), 1);
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    Pageable pageable = Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order));
    when(feedbackRepository.filterByQueries(TEST_PROVIDER_ID, TEST_PATIENT_ID, TEST_FEEDBACK_RATING, pageable))
      .thenReturn(page);
    FeedbackCollection actualResponse = feedbackService.search(
      TEST_PROVIDER_ID,
      TEST_PATIENT_ID,
      TEST_FEEDBACK_RATING,
      TEST_LIMIT,
      TEST_PAGE
    );

    assertEquals(actualResponse.getItems().size(), page.getContent().size());
    verify(feedbackRepository, times(1))
      .filterByQueries(TEST_PROVIDER_ID, TEST_PATIENT_ID, TEST_FEEDBACK_RATING, pageable);
    verifyNoMoreInteractions(feedbackRepository);
  }

  @Test
  @DisplayName("test create feedback")
  void testCreateFeedback() {
    CreateFeedbackDetailsRequest createFeedbackDetailsRequest = new CreateFeedbackDetailsRequest()
      .patientId(TEST_PATIENT_ID)
      .text(TEST_FEEDBACK_TEXT)
      .rating(TEST_FEEDBACK_RATING);
    when(feedbackRepository.save(TEST_FEEDBACK_ENTITY)).thenReturn(TEST_FEEDBACK_ENTITY);
    FeedbackEntity actualResponse = feedbackService.createFeedback(TEST_PROVIDER_ID, createFeedbackDetailsRequest);
    assertEquals(actualResponse.getProviderId(), TEST_PROVIDER_ID);
    verify(feedbackRepository, times(1)).save(TEST_FEEDBACK_ENTITY);
    verifyNoMoreInteractions(feedbackRepository);
  }
}
