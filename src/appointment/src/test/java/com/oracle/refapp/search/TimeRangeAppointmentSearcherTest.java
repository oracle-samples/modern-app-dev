/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import static com.oracle.refapp.TestUtils.TEST_ZONED_END_TIME;
import static com.oracle.refapp.TestUtils.TEST_ZONED_START_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.constants.Status;
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
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TimeRangeAppointmentSearcherTest {

  private static Pageable PAGEABLE;
  private static Page<AppointmentEntity> PAGE;

  private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
  private final AppointmentMapper appointmentMapper = new AppointmentMapperImpl();
  private final TimeRangeAppointmentSearcher searcher = new TimeRangeAppointmentSearcher(
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
  @DisplayName(("StartTime Greater Than Equals And EndTime Less Than Equals"))
  void testStartTimeGreaterThanEqualsAndEndTimeLessThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setStartTime(TEST_ZONED_START_TIME);
    testSearchCriteria.setEndTime(TEST_ZONED_END_TIME);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(
      appointmentRepository.findByStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        testSearchCriteria.getStartTime(),
        testSearchCriteria.getEndTime(),
        PAGEABLE
      )
    )
      .thenReturn(PAGE);
    AppointmentCollection resultSearchByStartTimeEndTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByStartTimeEndTime.getItems().size());
  }

  @Test
  @DisplayName(("StartTime Greater Than Equals"))
  void testStartTimeGreaterThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setStartTime(TEST_ZONED_START_TIME);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(appointmentRepository.findByStartTimeGreaterThanEquals(testSearchCriteria.getStartTime(), PAGEABLE))
      .thenReturn(PAGE);
    AppointmentCollection resultSearchByStartTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByStartTime.getItems().size());
  }

  @Test
  @DisplayName(("StartTime Greater Than Equals"))
  void testEndTimeLessThanEquals() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setEndTime(TEST_ZONED_END_TIME);
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(appointmentRepository.findByEndTimeLessThanEquals(testSearchCriteria.getEndTime(), PAGEABLE)).thenReturn(PAGE);
    AppointmentCollection resultSearchByEndTime = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchByEndTime.getItems().size());
  }

  @Test
  @DisplayName(("All Appointments"))
  void testAll() {
    AppointmentSearchCriteria testSearchCriteria = new AppointmentSearchCriteria();
    testSearchCriteria.setPage(0);
    testSearchCriteria.setLimit(2);
    when(appointmentRepository.findAll(PAGEABLE)).thenReturn(PAGE);
    AppointmentCollection resultSearchAll = searcher.search(testSearchCriteria);
    assertEquals(1, resultSearchAll.getItems().size());
  }

  private static AppointmentEntity buildAppointmentEntity() throws JsonProcessingException {
    AppointmentEntity appointment = new AppointmentEntity();
    appointment.setId(1);
    appointment.setStatus(Status.CONFIRMED);
    appointment.setPatientId(2);
    appointment.setProviderId(3);
    Map<String, String> testPrescriptionMap = Map.of(
      "weight",
      "50kg",
      "height",
      "168cm",
      "symptoms",
      "cough, cold etc."
    );
    String testPrescription = new ObjectMapper().writeValueAsString(testPrescriptionMap);
    appointment.setPreVisitData(testPrescription);
    appointment.setStartTime(TEST_ZONED_START_TIME);
    appointment.setEndTime(TEST_ZONED_END_TIME);
    return appointment;
  }
}
