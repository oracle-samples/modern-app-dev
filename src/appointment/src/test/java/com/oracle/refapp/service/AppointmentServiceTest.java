/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oracle.refapp.constants.Status;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.exceptions.*;
import com.oracle.refapp.model.AppointmentMessage;
import com.oracle.refapp.search.AppointmentSearcherFactory;
import io.micronaut.data.exceptions.DataAccessException;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppointmentServiceTest {

  private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
  private final AppointmentMessageProducer appointmentMessageProducer = mock(AppointmentMessageProducer.class);
  private final AppointmentSearcherFactory appointmentSearcherFactory = mock(AppointmentSearcherFactory.class);
  private final ServiceClient serviceClient = mock(ServiceClient.class);
  private final AppointmentService appointmentService = new AppointmentService(
    appointmentRepository,
    appointmentMessageProducer,
    appointmentSearcherFactory,
    serviceClient
  );

  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Test
  @DisplayName("test create appointment")
  void testCreateAppointment()
    throws IOException, NoSuchPatientFoundException, NoSuchProviderFoundException, MultipleAppointmentNotAllowedException {
    AppointmentEntity appointment = buildAppointmentEntity();
    when(serviceClient.getPatientDetails(TEST_PATIENT_ID, TEST_TOKEN))
      .thenReturn(mapper.readValue(TEST_PATIENT_JSON, new TypeReference<>() {}));
    when(serviceClient.getProviderDetails(TEST_PROVIDER_ID, TEST_TOKEN))
      .thenReturn(mapper.readValue(TEST_PROVIDER_JSON, new TypeReference<>() {}));
    when(appointmentRepository.save(appointment)).thenReturn(appointment);
    AppointmentMessage appointmentMessage = new AppointmentMessage(
      appointment.getStatus(),
      appointment.getStartTime(),
      appointment.getEndTime(),
      "patient@uho.com",
      "provider@uho.com",
      "provider",
      "patient"
    );
    String message = mapper.writeValueAsString(appointmentMessage);
    doNothing().when(appointmentMessageProducer).sendMessage(appointment.getProviderId().toString(), message);
    appointmentService.createAppointment(appointment, TEST_TOKEN);
    verify(appointmentRepository, times(1)).save(appointment);
    verify(appointmentMessageProducer, times(1)).sendMessage(appointment.getProviderId().toString(), message);
  }

  @Test
  @DisplayName("test create appointment exceptions")
  void testCreateAppointmentExceptions_Multipleappointments() {
    when(appointmentRepository.save(TEST_APPOINTMENT_ENTITY))
      .thenThrow(new DataAccessException("Error executing SQL UPDATE: ORA-00001: Unique Constraint Violated."));
    assertThrows(
      MultipleAppointmentNotAllowedException.class,
      () -> appointmentService.createAppointment(TEST_APPOINTMENT_ENTITY, TEST_TOKEN)
    );
  }

  @Test
  @DisplayName("test create appointment")
  void testCreateAppointmentException_NoSuchPatient()
    throws IOException, NoSuchPatientFoundException, NoSuchProviderFoundException {
    when(serviceClient.getPatientDetails(TEST_PATIENT_ID, "test")).thenThrow(NoSuchPatientFoundException.class);
    when(serviceClient.getProviderDetails(TEST_PROVIDER_ID, "test"))
      .thenReturn(mapper.readValue(TEST_PROVIDER_JSON, new TypeReference<>() {}));
    when(appointmentRepository.save(TEST_APPOINTMENT_ENTITY)).thenReturn(TEST_APPOINTMENT_ENTITY);
    assertThrows(
      NoSuchPatientFoundException.class,
      () -> appointmentService.createAppointment(TEST_APPOINTMENT_ENTITY, "test")
    );
    verify(appointmentRepository, times(1)).save(TEST_APPOINTMENT_ENTITY);
    verifyNoInteractions(appointmentMessageProducer);
  }

  @Test
  @DisplayName("test create appointment")
  void testCreateAppointmentException_NoSuchProvider()
    throws IOException, NoSuchPatientFoundException, NoSuchProviderFoundException {
    when(serviceClient.getPatientDetails(TEST_PATIENT_ID, "test"))
      .thenReturn(mapper.readValue(TEST_PATIENT_JSON, new TypeReference<>() {}));
    when(serviceClient.getProviderDetails(TEST_PROVIDER_ID, "test")).thenThrow(NoSuchProviderFoundException.class);
    when(appointmentRepository.save(TEST_APPOINTMENT_ENTITY)).thenReturn(TEST_APPOINTMENT_ENTITY);
    assertThrows(
      NoSuchProviderFoundException.class,
      () -> appointmentService.createAppointment(TEST_APPOINTMENT_ENTITY, "test")
    );
    verify(appointmentRepository, times(1)).save(TEST_APPOINTMENT_ENTITY);
    verifyNoInteractions(appointmentMessageProducer);
  }

  @Test
  @DisplayName("test cancel appointment")
  void testCancelAppointment()
    throws IOException, NoSuchAppointmentFoundException, AppointmentUpdateFailedException, NoSuchPatientFoundException, NoSuchProviderFoundException {
    AppointmentEntity appointment = buildAppointmentEntity();
    appointment.setUniqueString("3" + "2021-11-28T09:30Z[UTC]" + "2021-11-28T10:00Z[UTC]" + "confirmed");
    when(serviceClient.getPatientDetails(TEST_PATIENT_ID, "test"))
      .thenReturn(mapper.readValue(TEST_PATIENT_JSON, new TypeReference<>() {}));
    when(serviceClient.getProviderDetails(TEST_PROVIDER_ID, "test"))
      .thenReturn(mapper.readValue(TEST_PROVIDER_JSON, new TypeReference<>() {}));
    when(appointmentRepository.findById(TEST_APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
    AppointmentMessage appointmentMessage = new AppointmentMessage(
      appointment.getStatus(),
      appointment.getStartTime(),
      appointment.getEndTime(),
      "patient@uho.com",
      "provider@uho.com",
      "provider",
      "patient"
    );
    String message = mapper.writeValueAsString(appointmentMessage);
    doNothing().when(appointmentMessageProducer).sendMessage(appointment.getProviderId().toString(), message);
    appointmentService.updateAppointment(1, Status.CANCELLED, "test");
    verify(appointmentRepository, times(1)).update(appointment);
  }

  @Test
  @DisplayName("test GET appointment service exception")
  void testGetAppointmentException() {
    assertThrows(NoSuchAppointmentFoundException.class, () -> appointmentService.getAppointment(2));
  }

  @Test
  @DisplayName("test cancel appointment exception")
  void testCancelAppointmentException() {
    when(appointmentRepository.findById(1)).thenReturn(Optional.empty());
    assertThrows(
      NoSuchAppointmentFoundException.class,
      () -> appointmentService.updateAppointment(2, Status.CANCELLED, null)
    );
  }

  @Test
  @DisplayName("test get appointment service")
  void testGetAppointment() throws NoSuchAppointmentFoundException {
    when(appointmentRepository.findById(TEST_APPOINTMENT_ID))
      .thenReturn(java.util.Optional.of(TEST_APPOINTMENT_ENTITY));
    AppointmentEntity result = appointmentService.getAppointment(TEST_APPOINTMENT_ID);
    assertEquals(result.getId(), TEST_APPOINTMENT_ENTITY.getId());
  }

  @Test
  @DisplayName("test delete appointment exception")
  void testDeleteAppointmentException() {
    when(appointmentRepository.findById(TEST_APPOINTMENT_ID)).thenReturn(Optional.empty());
    assertThrows(
      NoSuchAppointmentFoundException.class,
      () -> appointmentService.deleteAppointment(TEST_APPOINTMENT_ID)
    );
  }

  @Test
  @DisplayName("test delete appointment service")
  void testDeleteAppointment() throws NoSuchAppointmentFoundException {
    when(appointmentRepository.findById(TEST_APPOINTMENT_ID))
      .thenReturn(java.util.Optional.of(TEST_APPOINTMENT_ENTITY));
    appointmentService.deleteAppointment(TEST_APPOINTMENT_ID);
    verify(appointmentRepository, times(1)).delete(TEST_APPOINTMENT_ENTITY);
  }
}
