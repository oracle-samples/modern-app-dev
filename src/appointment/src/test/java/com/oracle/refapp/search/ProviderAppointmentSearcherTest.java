/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.mappers.AppointmentMapper;
import com.oracle.refapp.mappers.AppointmentMapperImpl;
import com.oracle.refapp.model.AppointmentCollection;
import com.oracle.refapp.model.AppointmentSearchCriteria;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProviderAppointmentSearcherTest {

  private static Pageable PAGEABLE;
  private static Page<AppointmentEntity> PAGE;

  private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
  private final AppointmentMapper appointmentMapper = new AppointmentMapperImpl();
  private final ProviderAppointmentSearcher searcher = new ProviderAppointmentSearcher(
    appointmentRepository,
    appointmentMapper
  );

  @BeforeAll
  public static void init() throws JsonProcessingException {
    Sort.Order order = new Sort.Order("startTime", Sort.Order.Direction.DESC, true);
    PAGEABLE = Pageable.from(0, 2, Sort.of(order));
    PAGE = Page.of(List.of(buildAppointmentEntity()), PAGEABLE, 1);
  }

  @Test
  @DisplayName("test search by provider and start time greater than equals and end time less than equals")
  void testSearchByStartTimeGreaterThanEqualsAndEndTimeLessThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setStartTime(TEST_ZONED_START_TIME);
    testSearchCriteria.setEndTime(TEST_ZONED_END_TIME);
    testSearchCriteria.setProviderId(TEST_PROVIDER_ID);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);

    when(
      appointmentRepository.findByProviderIdAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        TEST_PROVIDER_ID,
        TEST_ZONED_START_TIME,
        TEST_ZONED_END_TIME,
        PAGEABLE
      )
    )
      .thenReturn(PAGE);
    AppointmentCollection resultSearchByProviderStartTimeEndTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByProviderStartTimeEndTime.getItems().size());
  }

  @Test
  @DisplayName("test search by provider and start time greater than equals and end time not provided")
  void testSearchByStartTimeGreaterThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setStartTime(TEST_ZONED_START_TIME);
    testSearchCriteria.setProviderId(TEST_PROVIDER_ID);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(
      appointmentRepository.findByProviderIdAndStartTimeGreaterThanEquals(
        TEST_PROVIDER_ID,
        TEST_ZONED_START_TIME,
        PAGEABLE
      )
    )
      .thenReturn(PAGE);
    AppointmentCollection resultSearchByProviderStartTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByProviderStartTime.getItems().size());
  }

  @Test
  @DisplayName("test search by provider and end time less than equals and start time not provided")
  void testSearchByEndTimeLessThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setEndTime(TEST_ZONED_END_TIME);
    testSearchCriteria.setProviderId(TEST_PROVIDER_ID);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(
      appointmentRepository.findByProviderIdAndEndTimeLessThanEquals(TEST_PROVIDER_ID, TEST_ZONED_END_TIME, PAGEABLE)
    )
      .thenReturn(PAGE);
    AppointmentCollection resultSearchByProviderEndTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByProviderEndTime.getItems().size());
  }

  @Test
  @DisplayName("test search by provider only")
  void testSearchByOnlyProviderId() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setProviderId(TEST_PROVIDER_ID);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(appointmentRepository.findByProviderId(TEST_PROVIDER_ID, PAGEABLE)).thenReturn(PAGE);
    AppointmentCollection resultSearchByProvider = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByProvider.getItems().size());
  }
}
