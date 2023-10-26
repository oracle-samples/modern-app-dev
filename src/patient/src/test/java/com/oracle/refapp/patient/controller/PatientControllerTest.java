/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.controller;

import static com.oracle.refapp.patient.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.patient.TestUtils.TEST_PAGE;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_ENTITY;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_ID;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_USERNAME;
import static com.oracle.refapp.patient.TestUtils.buildPatientEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oracle.refapp.patient.domain.entity.PatientEntity;
import com.oracle.refapp.patient.domain.repository.PatientRepository;
import com.oracle.refapp.patient.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.patient.exceptions.UniqueUserNameViolationException;
import com.oracle.refapp.patient.models.CreatePatientDetailsRequest;
import com.oracle.refapp.patient.models.Gender;
import com.oracle.refapp.patient.models.Patient;
import com.oracle.refapp.patient.models.PatientCollection;
import com.oracle.refapp.patient.models.PatientSummary;
import com.oracle.refapp.patient.models.UpdatePatientDetailsRequest;
import com.oracle.refapp.patient.service.PatientService;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MicronautTest
class PatientControllerTest {

  @Inject
  @Client("/")
  HttpClient client;

  @Singleton
  @Replaces(PatientRepository.class)
  PatientRepository patientRepository() {
    return mock(PatientRepository.class);
  }

  @MockBean(PatientService.class)
  PatientService mockedPatientService() {
    return mock(PatientService.class);
  }

  @Inject
  private PatientService patientService;

  @Test
  @DisplayName("test Get Patient endpoint with id")
  void testGetPatientWithId() throws NoSuchPatientFoundException, IOException {
    Patient patient = new Patient();
    patient.setId(1);
    when(patientService.getPatient(TEST_PATIENT_ID, "2")).thenReturn(patient);
    HttpRequest<Object> request = HttpRequest.GET("/v1/patients/1").header("Authorization", "2");
    HttpResponse<Patient> response = client.toBlocking().exchange(request, Patient.class);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(patient.getId(), response.getBody().get().getId());
  }

  @Test
  @DisplayName("test Get Patient endpoint when patient id does not match")
  void testGetPatientExceptionWithId() throws NoSuchPatientFoundException, IOException {
    Patient patient = new Patient();
    patient.setId(1);
    when(patientService.getPatient(TEST_PATIENT_ID, "2")).thenThrow(NoSuchPatientFoundException.class);
    HttpRequest<Object> request = HttpRequest.GET("/v1/patients/1").header("Authorization", "2");
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Patient.class));
  }

  @Test
  @DisplayName("test Get Patient endpoint with username")
  void testGetPatientWithUsername() throws NoSuchPatientFoundException {
    when(patientService.getPatientByUsername(TEST_PATIENT_USERNAME)).thenReturn(TEST_PATIENT_ENTITY);
    HttpRequest<Object> request = HttpRequest.GET("/v1/patients/username/john_doe");
    HttpResponse<Patient> response = client.toBlocking().exchange(request, Patient.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Get Patient endpoint when patient username does not match")
  void testGetPatientExceptionWithUsername() throws NoSuchPatientFoundException {
    when(patientService.getPatientByUsername(TEST_PATIENT_USERNAME)).thenThrow(NoSuchPatientFoundException.class);
    HttpRequest<Object> request = HttpRequest.GET("/v1/patients/username/john_doe");
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Patient.class));
  }

  @Test
  @DisplayName("test update patient endpoint")
  void testUpdatePatient() throws NoSuchPatientFoundException {
    PatientEntity patientEntity = buildPatientEntity();
    patientEntity.setId(null);
    patientEntity.setUsername(null);
    patientEntity.setPrimaryCareProviderId(null);
    UpdatePatientDetailsRequest updatePatientDetailsRequest = new UpdatePatientDetailsRequest();
    updatePatientDetailsRequest.setName(TEST_PATIENT_ENTITY.getName());
    updatePatientDetailsRequest.setPhone(TEST_PATIENT_ENTITY.getPhone());
    updatePatientDetailsRequest.setEmail(TEST_PATIENT_ENTITY.getEmail());
    updatePatientDetailsRequest.setGender(Gender.valueOf(TEST_PATIENT_ENTITY.getGender().getValue()));
    updatePatientDetailsRequest.setZip(TEST_PATIENT_ENTITY.getZip());
    updatePatientDetailsRequest.setCity(TEST_PATIENT_ENTITY.getCity());
    updatePatientDetailsRequest.setCountry(TEST_PATIENT_ENTITY.getCountry());
    updatePatientDetailsRequest.setDateOfBirth(
      TEST_PATIENT_ENTITY.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    );
    when(patientService.updatePatient(eq(TEST_PATIENT_ID), any())).thenReturn(patientEntity);
    HttpRequest<Object> request = HttpRequest.PUT("/v1/patients/1", updatePatientDetailsRequest);
    HttpResponse<Patient> response = client.toBlocking().exchange(request, Patient.class);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  @DisplayName("test Update Patient endpoint when patient does not exist")
  void testUpdatePatientException() throws NoSuchPatientFoundException {
    PatientEntity patientEntity = buildPatientEntity();
    patientEntity.setId(null);
    UpdatePatientDetailsRequest updatePatientDetailsRequest = new UpdatePatientDetailsRequest();
    updatePatientDetailsRequest.setName(TEST_PATIENT_ENTITY.getName());
    updatePatientDetailsRequest.setPhone(TEST_PATIENT_ENTITY.getPhone());
    updatePatientDetailsRequest.setEmail(TEST_PATIENT_ENTITY.getEmail());
    updatePatientDetailsRequest.setGender(Gender.valueOf(TEST_PATIENT_ENTITY.getGender().getValue()));
    updatePatientDetailsRequest.setZip(TEST_PATIENT_ENTITY.getZip());
    updatePatientDetailsRequest.setCity(TEST_PATIENT_ENTITY.getCity());
    updatePatientDetailsRequest.setCountry(TEST_PATIENT_ENTITY.getCountry());
    doThrow(NoSuchPatientFoundException.class).when(patientService).updatePatient(TEST_PATIENT_ID, patientEntity);
    HttpRequest<Object> request = HttpRequest.PUT("/v1/patients/1", updatePatientDetailsRequest);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Patient.class));
  }

  @Test
  @DisplayName("test authorizeDeviceToCollectData")
  void testAuthorizeDeviceToCollectData() {
    doNothing().when(patientService).authorizeDeviceToCollectData(TEST_PATIENT_ID);
    HttpRequest<Object> request = HttpRequest.POST("/v1/patients/1/actions/authorizeDevice", null);
    HttpResponse<Object> response = client.toBlocking().exchange(request, Object.class);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  @DisplayName("test Create Patient endpoint")
  void testCreatePatient() throws UniqueUserNameViolationException {
    CreatePatientDetailsRequest createPatientDetailsRequest = new CreatePatientDetailsRequest();
    createPatientDetailsRequest.setName(TEST_PATIENT_ENTITY.getName());
    createPatientDetailsRequest.setUsername(TEST_PATIENT_ENTITY.getUsername());
    createPatientDetailsRequest.setPhone(TEST_PATIENT_ENTITY.getPhone());
    createPatientDetailsRequest.setDateOfBirth(
      TEST_PATIENT_ENTITY.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    );
    createPatientDetailsRequest.setEmail(TEST_PATIENT_ENTITY.getEmail());
    createPatientDetailsRequest.setGender(Gender.valueOf(TEST_PATIENT_ENTITY.getGender().getValue()));
    createPatientDetailsRequest.setZip(TEST_PATIENT_ENTITY.getZip());
    createPatientDetailsRequest.setCity(TEST_PATIENT_ENTITY.getCity());
    createPatientDetailsRequest.setCountry(TEST_PATIENT_ENTITY.getCountry());
    PatientEntity testEntity = buildPatientEntity();
    testEntity.setId(null);
    testEntity.setPrimaryCareProviderId(null);
    when(patientService.createPatient(any())).thenReturn(testEntity);
    HttpRequest<Object> request = HttpRequest.POST("/v1/patients/", createPatientDetailsRequest);
    HttpResponse<Patient> response = client.toBlocking().exchange(request, Patient.class);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  @DisplayName("test Create Patient endpoint exception")
  void testCreatePatientException() throws UniqueUserNameViolationException {
    CreatePatientDetailsRequest createPatientDetailsRequest = new CreatePatientDetailsRequest();
    when(patientService.createPatient(any())).thenThrow(new UniqueUserNameViolationException("testMessage"));
    HttpRequest<Object> request = HttpRequest.POST("/v1/patients/", createPatientDetailsRequest);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Patient.class));
  }

  @Test
  @DisplayName("test Delete Patient endpoint")
  void testDeletePatient() throws NoSuchPatientFoundException {
    doNothing().when(patientService).deletePatient(TEST_PATIENT_ID);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/patients/1");
    HttpResponse<Patient> response = client.toBlocking().exchange(request, Patient.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Delete Patient Exception endpoint")
  void testDeletePatientException() throws NoSuchPatientFoundException {
    doThrow(NoSuchPatientFoundException.class).when(patientService).deletePatient(TEST_PATIENT_ID);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/patients/1");
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Patient.class));
  }

  @Test
  @DisplayName("test List Patient endpoint")
  void testListPatient() {
    List<PatientSummary> patientSummaryList = new ArrayList<>();
    PatientSummary summary = new PatientSummary();
    summary.setPhone(TEST_PATIENT_ENTITY.getPhone());
    summary.setName(TEST_PATIENT_ENTITY.getName());
    summary.setUsername(TEST_PATIENT_ENTITY.getUsername());
    summary.setGender(Gender.valueOf(TEST_PATIENT_ENTITY.getGender().getValue()));
    summary.setId(TEST_PATIENT_ENTITY.getId());
    patientSummaryList.add(summary);
    PatientCollection testPatientCollection = new PatientCollection().items(patientSummaryList);

    when(patientService.listPatients(TEST_LIMIT, TEST_PAGE)).thenReturn(testPatientCollection);
    URI uri = UriBuilder
      .of("/v1/patients/actions/search")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    HttpResponse<PatientCollection> response = client.toBlocking().exchange(request, PatientCollection.class);
    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(1, response.getBody().get().getItems().size());
  }
}
