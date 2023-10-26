/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.controller;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.refapp.constants.Status;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.exceptions.*;
import com.oracle.refapp.mappers.AppointmentMapper;
import com.oracle.refapp.model.*;
import com.oracle.refapp.service.AppointmentService;
import com.oracle.refapp.telemetry.TelemetryClient;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@MicronautTest
class AppointmentControllerTest {

  @Inject
  @Client("/")
  private HttpClient client;

  @Inject
  private AppointmentService appointmentService;

  @Inject
  private AppointmentMapper mapper;

  @Inject
  private TelemetryClient telemetryClient;

  @Singleton
  @Replaces(AppointmentRepository.class)
  AppointmentRepository AppointmentRepository() {
    return mock(AppointmentRepository.class);
  }

  @MockBean(AppointmentService.class)
  AppointmentService mockedAppointmentService() {
    return mock(AppointmentService.class);
  }

  @MockBean(TelemetryClient.class)
  TelemetryClient mockedTelemetryClient() {
    return mock(TelemetryClient.class);
  }

  private ArgumentCaptor<AppointmentSearchCriteria> searchCriteriaArgumentCaptor = ArgumentCaptor.forClass(
    AppointmentSearchCriteria.class
  );

  private Appointment testAppointment = buildAppointment();

  @Test
  @DisplayName("Create Appointment")
  public void testCreateAppointment()
    throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException, MultipleAppointmentNotAllowedException {
    CreateAppointmentRequest appointmentRequest = new CreateAppointmentRequest(TEST_PATIENT_ID, TEST_PROVIDER_ID);
    appointmentRequest.setStartTime(TEST_ZONED_START_TIME);
    appointmentRequest.setEndTime(TEST_ZONED_END_TIME);
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenReturn(TEST_APPOINTMENT_ENTITY);
    MutableHttpRequest<CreateAppointmentRequest> request = HttpRequest
      .POST("/v1/appointments/", appointmentRequest)
      .header("Authorization", "test");
    HttpResponse<Appointment> response = client.toBlocking().exchange(request, Appointment.class);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(TEST_APPOINTMENT_ID, response.getBody().get().getId());
    verify(telemetryClient, times(1)).postMetricData(anyString(), anyList());
  }

  @Test
  @DisplayName("Create Appointment Failed")
  public void testCreateAppointmentExceptions()
    throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException, MultipleAppointmentNotAllowedException {
    CreateAppointmentRequest appointmentRequest = new CreateAppointmentRequest(TEST_PATIENT_ID, TEST_PROVIDER_ID);
    appointmentRequest.setStartTime(TEST_ZONED_START_TIME);
    appointmentRequest.setEndTime(TEST_ZONED_END_TIME);
    MutableHttpRequest<CreateAppointmentRequest> request = HttpRequest
      .POST("/v1/appointments/", appointmentRequest)
      .header("Authorization", "test");
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(NoSuchPatientFoundException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(NoSuchProviderFoundException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(MultipleAppointmentNotAllowedException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(DataAccessException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(IOException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.createAppointment(any(AppointmentEntity.class), any(String.class)))
      .thenThrow(JsonProcessingException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
  }

  @Test
  @DisplayName("Update Appointment")
  public void testUpdateAppointment()
    throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException, MultipleAppointmentNotAllowedException, AppointmentUpdateFailedException, NoSuchAppointmentFoundException {
    UpdateAppointmentRequest appointmentRequest = new UpdateAppointmentRequest();
    appointmentRequest.setStatus(com.oracle.refapp.model.Status.CANCELLED);
    when(appointmentService.updateAppointment(TEST_APPOINTMENT_ID, Status.CANCELLED, "test"))
      .thenReturn(TEST_APPOINTMENT_ENTITY);
    MutableHttpRequest<UpdateAppointmentRequest> request = HttpRequest
      .PUT("/v1/appointments/" + TEST_APPOINTMENT_ID, appointmentRequest)
      .header("Authorization", "test");
    HttpResponse<Appointment> response = client.toBlocking().exchange(request, Appointment.class);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(TEST_APPOINTMENT_ID, response.getBody().get().getId());
  }

  @Test
  @DisplayName("Update Appointment failures")
  public void testUpdateAppointmentExceptions()
    throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException, AppointmentUpdateFailedException, NoSuchAppointmentFoundException {
    UpdateAppointmentRequest appointmentRequest = new UpdateAppointmentRequest();
    appointmentRequest.setStatus(com.oracle.refapp.model.Status.CANCELLED);
    MutableHttpRequest<UpdateAppointmentRequest> request = HttpRequest
      .PUT("/v1/appointments/" + TEST_APPOINTMENT_ID, appointmentRequest)
      .header("Authorization", "test");
    when(appointmentService.updateAppointment(anyInt(), any(Status.class), anyString()))
      .thenThrow(AppointmentUpdateFailedException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.updateAppointment(anyInt(), any(Status.class), anyString()))
      .thenThrow(NoSuchPatientFoundException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.updateAppointment(anyInt(), any(Status.class), anyString()))
      .thenThrow(NoSuchProviderFoundException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.updateAppointment(anyInt(), any(Status.class), anyString())).thenThrow(IOException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
    when(appointmentService.updateAppointment(anyInt(), any(Status.class), anyString()))
      .thenThrow(NoSuchAppointmentFoundException.class);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Appointment.class));
  }

  @Test
  @DisplayName("test get Appointment endpoint")
  void testGetAppointment() throws NoSuchAppointmentFoundException {
    AppointmentEntity appointment = new AppointmentEntity();
    appointment.setId(TEST_APPOINTMENT_ID);
    appointment.setStatus(Status.CONFIRMED);
    appointment.setPatientId(TEST_PATIENT_ID);
    appointment.setProviderId(TEST_PROVIDER_ID);
    appointment.setStartTime(TEST_ZONED_START_TIME);
    appointment.setEndTime(TEST_ZONED_END_TIME);
    when(appointmentService.getAppointment(1)).thenReturn(appointment);
    HttpRequest<Object> request = HttpRequest.GET("v1/appointments/1");
    HttpResponse<AppointmentEntity> response = client.toBlocking().exchange(request, AppointmentEntity.class);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(Status.CONFIRMED, response.getBody().get().getStatus());
  }

  @Test
  @DisplayName("test get appointment when appointment does not exist ")
  void testGetAppointmentException() throws NoSuchAppointmentFoundException {
    HttpRequest<Object> request = HttpRequest.GET("v1/appointments/1");
    when(appointmentService.getAppointment(1)).thenThrow(NoSuchAppointmentFoundException.class);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    Assertions.assertThrows(
      HttpClientResponseException.class,
      () -> blockingHttpClient.exchange(request, AppointmentEntity.class)
    );
  }

  @Test
  @DisplayName("test patient search with patientId")
  void testAppointmentSearchWithValidParams() {
    AppointmentCollection appointmentCollection = new AppointmentCollection()
      .items(List.of(mapper.mapToSummary(testAppointment)));
    URI uri = UriBuilder
      .of("v1/appointments/actions/search/")
      .queryParam("patientId", TEST_PATIENT_ID)
      .queryParam("startTime", TEST_START_TIME)
      .queryParam("endTime", TEST_END_TIME)
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    when(appointmentService.searchAppointment(Mockito.any(AppointmentSearchCriteria.class)))
      .thenReturn(appointmentCollection);
    HttpRequest<Object> request = HttpRequest.GET(uri);
    HttpResponse<AppointmentCollection> response = client.toBlocking().exchange(request, AppointmentCollection.class);
    verify(appointmentService, times(1)).searchAppointment(searchCriteriaArgumentCaptor.capture());
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(TEST_PAGE, searchCriteriaArgumentCaptor.getValue().getPage());
    assertEquals(TEST_LIMIT, searchCriteriaArgumentCaptor.getValue().getLimit());
    assertEquals(TEST_ZONED_START_TIME, searchCriteriaArgumentCaptor.getValue().getStartTime());
    assertEquals(TEST_ZONED_END_TIME, searchCriteriaArgumentCaptor.getValue().getEndTime());
    assertEquals(TEST_PATIENT_ID, searchCriteriaArgumentCaptor.getValue().getPatientId());
    assertEquals(1, response.getBody().get().getItems().size());
  }

  @Test
  @DisplayName("test patient search with invalid parameters")
  void testAppointmentSearchWithInvalidValidParams_bothPatientIdAndProviderIdPassed() {
    URI uri = UriBuilder
      .of("v1/appointments/actions/search/")
      .queryParam("patientId", TEST_PATIENT_ID)
      .queryParam("startTime", TEST_START_TIME)
      .queryParam("endTime", TEST_END_TIME)
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .queryParam("providerId", TEST_PROVIDER_ID)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    assertThrows(
      HttpClientResponseException.class,
      () -> client.toBlocking().exchange(request, AppointmentCollection.class)
    );
    verifyNoInteractions(appointmentService);
  }

  @Test
  @DisplayName("test delete appointment endpoint")
  void testDeleteAppointment() throws NoSuchAppointmentFoundException {
    doNothing().when(appointmentService).deleteAppointment(TEST_APPOINTMENT_ID);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/appointments/1");
    HttpResponse<Appointment> response = client.toBlocking().exchange(request, Appointment.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test delete appointment exception")
  void testDeleteAppointmentException() throws NoSuchAppointmentFoundException {
    doThrow(NoSuchAppointmentFoundException.class).when(appointmentService).deleteAppointment(TEST_APPOINTMENT_ID);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/appointments/1");
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Appointment.class));
  }
}
