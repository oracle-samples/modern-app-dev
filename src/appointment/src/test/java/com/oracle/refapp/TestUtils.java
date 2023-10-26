/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.model.Appointment;
import com.oracle.refapp.model.Status;
import java.time.ZonedDateTime;
import java.util.Map;

public class TestUtils {

  public static final String TEST_START_TIME = "2021-11-28T09:30Z";
  public static final String TEST_END_TIME = "2021-11-28T10:00Z";
  public static final ZonedDateTime TEST_ZONED_START_TIME = ZonedDateTime.parse(TEST_START_TIME);
  public static final ZonedDateTime TEST_ZONED_END_TIME = ZonedDateTime.parse(TEST_END_TIME);
  public static final Integer TEST_APPOINTMENT_ID = 1;
  public static final Integer TEST_PATIENT_ID = 2;
  public static final Integer TEST_PROVIDER_ID = 3;
  public static final Map<String, String> TEST_PRESCRIPTION_MAP = Map.of(
    "weight",
    "50kg",
    "height",
    "168cm",
    "symptoms",
    "cough, cold etc."
  );
  public static final Integer TEST_LIMIT = 1;
  public static final Integer TEST_PAGE = 2;
  public static final String TEST_TOKEN = "testToken";
  public static final AppointmentEntity TEST_APPOINTMENT_ENTITY = buildAppointmentEntity();
  public static final String TEST_PATIENT_JSON = "{\"email\": \"patient@uho.com\",\"name\": \"patient\"}";
  public static final String TEST_PROVIDER_JSON = "{\"email\": \"provider@uho.com\",\"firstName\": \"provider\"}";

  public static AppointmentEntity buildAppointmentEntity() {
    try {
      AppointmentEntity appointment = new AppointmentEntity();
      appointment.setId(1);
      appointment.setStatus(com.oracle.refapp.constants.Status.valueOf(Status.CONFIRMED.name()));
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
      String testPrescription = null;
      testPrescription = new ObjectMapper().writeValueAsString(testPrescriptionMap);
      appointment.setPreVisitData(testPrescription);
      appointment.setStartTime(TEST_ZONED_START_TIME);
      appointment.setEndTime(TEST_ZONED_END_TIME);
      return appointment;
    } catch (JsonProcessingException ignored) {}

    return null;
  }

  public static Appointment buildAppointment() {
    Appointment appointment = new Appointment(TEST_PATIENT_ID, TEST_PROVIDER_ID);
    appointment.setId(TEST_APPOINTMENT_ID);
    appointment.setStatus(Status.CONFIRMED);
    appointment.setPreVisitData(TEST_PRESCRIPTION_MAP);
    appointment.setStartTime(TEST_ZONED_START_TIME);
    appointment.setEndTime(TEST_ZONED_END_TIME);
    return appointment;
  }
}
