/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.CreateFeedbackDetailsRequest;
import com.oracle.refapp.provider.models.FeedbackCollection;
import com.oracle.refapp.provider.models.FeedbackSummary;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;

  private final ProviderMapper mapper;

  public FeedbackService(FeedbackRepository feedbackRepository, ProviderMapper mapper) {
    this.feedbackRepository = feedbackRepository;
    this.mapper = mapper;
  }

  public FeedbackEntity createFeedback(Integer providerId, CreateFeedbackDetailsRequest createFeedbackDetailsRequest) {
    FeedbackEntity feedbackEntity = new FeedbackEntity();
    feedbackEntity.setPatientId(createFeedbackDetailsRequest.getPatientId());
    feedbackEntity.setProviderId(providerId);
    feedbackEntity.setRating(createFeedbackDetailsRequest.getRating());
    feedbackEntity.setText(createFeedbackDetailsRequest.getText());
    return feedbackRepository.save(feedbackEntity);
  }

  public FeedbackCollection search(Integer providerId, Integer patientId, Integer rating, Integer limit, Integer page) {
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    Page<FeedbackEntity> feedbackEntityPage = feedbackRepository.filterByQueries(
      providerId,
      patientId,
      rating,
      Pageable.from(page, limit, Sort.of(order))
    );
    List<FeedbackEntity> feedbackEntities = feedbackEntityPage.getContent();
    List<FeedbackSummary> feedbackSummaries = mapper.mapFeedbackEntityListToFeedbackSummaryList(feedbackEntities);
    Integer nextPage = (feedbackEntityPage.getTotalPages() > feedbackEntityPage.nextPageable().getNumber())
      ? feedbackEntityPage.nextPageable().getNumber()
      : null;
    return new FeedbackCollection().items(feedbackSummaries).nextPage(nextPage);
  }
}
