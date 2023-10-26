/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mappers;

import static com.oracle.refapp.TestUtils.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.model.Appointment;
import com.oracle.refapp.model.AppointmentSummary;
import com.oracle.refapp.model.CreateAppointmentRequest;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppointmentMapperTest {

  private final AppointmentMapper appointmentMapper = new AppointmentMapperImpl();

  @Test
  @DisplayName("test appointment mapper - mapApiToDomainModels")
  void testMapperMapApiToDomainModels() {
    CreateAppointmentRequest request = new CreateAppointmentRequest(TEST_PATIENT_ID, TEST_PROVIDER_ID);
    request.setPreVisitData(TEST_PRESCRIPTION_MAP);
    request.setStartTime(TEST_ZONED_START_TIME);
    request.setEndTime(TEST_ZONED_END_TIME);
    AppointmentEntity result = appointmentMapper.mapApiToDomainModels(request);
    Assertions.assertEquals(result.getPatientId(), request.getPatientId());
    Assertions.assertEquals(result.getProviderId(), request.getProviderId());
    Assertions.assertEquals(TEST_ZONED_START_TIME, result.getStartTime());
    Assertions.assertEquals(TEST_ZONED_END_TIME, result.getEndTime());
  }

  @Test
  @DisplayName("test appointment mapper - mapApiToDomainModels - null check")
  void testMapperMapApiToDomainModelsNullCheck() {
    AppointmentEntity result = appointmentMapper.mapApiToDomainModels(null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Appointment Mapper - mapDomainToApiModels")
  void testMapperMapDomainToApiModels() {
    AppointmentEntity request = buildAppointmentEntity();
    Appointment result = appointmentMapper.mapDomainToApiModels(request);
    Assertions.assertEquals(result.getId(), request.getId());
    Assertions.assertEquals(result.getStatus().getValue(), request.getStatus().name());
    Assertions.assertEquals(result.getPatientId(), request.getPatientId());
    Assertions.assertEquals(result.getProviderId(), request.getProviderId());
    Assertions.assertEquals(result.getStartTime(), request.getStartTime());
    Assertions.assertEquals(result.getEndTime(), request.getEndTime());
  }

  @Test
  @DisplayName("test Appointment Mapper - mapDomainToApiModels - null check")
  void testMapperMapDomainToApiModelsNullCheck() {
    Appointment result = appointmentMapper.mapDomainToApiModels((AppointmentEntity) null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Appointment Mapper - mapDomainToApiModelsList")
  void testMapperMapDomainToApiModelsList() throws JsonProcessingException {
    AppointmentEntity appointment = buildAppointmentEntity();
    List<AppointmentEntity> request = new ArrayList<>();
    request.add(appointment);
    List<AppointmentSummary> result = appointmentMapper.mapDomainToApiModels(request);
    Assertions.assertEquals(result.size(), request.size());
  }

  @Test
  @DisplayName("test Appointment Mapper - mapDomainToApiModelsList - null check")
  void testMapperMapDomainToApiModelsListNullCheck() {
    List<AppointmentSummary> result = appointmentMapper.mapDomainToApiModels((List<AppointmentEntity>) null);
    Assertions.assertNull(result);
  }
}
