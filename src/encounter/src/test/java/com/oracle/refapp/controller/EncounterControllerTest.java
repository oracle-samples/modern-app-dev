/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.controller;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.oracle.refapp.connections.JSONConnection;
import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.model.CodeCollection;
import com.oracle.refapp.model.CodeSummary;
import com.oracle.refapp.model.Encounter;
import com.oracle.refapp.model.EncounterCollection;
import com.oracle.refapp.search.SearchCriteria;
import com.oracle.refapp.service.EncounterService;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@MicronautTest(environments = "test")
class EncounterControllerTest {

  ArgumentCaptor<SearchCriteria> searchCriteriaCaptor = ArgumentCaptor.forClass(SearchCriteria.class);

  @Inject
  @Client("/")
  private HttpClient client;

  @Inject
  private EncounterService encounterService;

  @Singleton
  @Replaces(JSONConnection.class)
  JSONConnection mockedJSONConnection() {
    return mock(JSONConnection.class);
  }

  @MockBean(EncounterService.class)
  EncounterService mockedEncounterService() {
    return mock(EncounterService.class);
  }

  @Inject
  ObjectStorageOperations<?, ?, ?> objectStorageOperations;

  /*
   * Mock ObjectStorageOperations with Mockito (https://site.mockito.org/)
   * The @MockBean annotation indicates the method returns a mock bean of ObjectStorageOperations.
   * The ObjectStorageOperations mock is injected into the test with @Inject above.
   */
  @MockBean(ObjectStorageOperations.class)
  ObjectStorageOperations<?, ?, ?> objectStorageOperations() {
    return mock(ObjectStorageOperations.class);
  }

  @Test
  @DisplayName("test list codes endpoint")
  void testListCodes() {
    List<CodeSummary> codeSummaries = List.of(buildCodeSummary());
    CodeCollection codeCollection = new CodeCollection().items(codeSummaries);
    when(encounterService.listCodes(TEST_CODE_TYPE, TEST_LIMIT, TEST_PAGE)).thenReturn(codeCollection);
    HttpRequest<Object> request = HttpRequest.GET(
      UriBuilder
        .of("/v1/encounters/codes")
        .queryParam("type", TEST_CODE_TYPE)
        .queryParam("limit", TEST_LIMIT)
        .queryParam("page", TEST_PAGE)
        .build()
    );
    HttpResponse<CodeCollection> response = client.toBlocking().exchange(request, CodeCollection.class);
    assertEquals(HttpStatus.OK, response.status());
    assertTrue(response.getBody().isPresent());
    assertIterableEquals(codeSummaries, response.getBody().get().getItems());
  }

  @Test
  void testListCodesException() {
    when(encounterService.listCodes(TEST_CODE_TYPE, TEST_LIMIT, TEST_PAGE)).thenThrow(RuntimeException.class);
    HttpRequest<Object> request = HttpRequest.GET(
      UriBuilder
        .of("/v1/encounters/codes")
        .queryParam("type", TEST_CODE_TYPE)
        .queryParam("limit", TEST_LIMIT)
        .queryParam("page", TEST_PAGE)
        .build()
    );
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, CodeCollection.class));
  }

  @Test
  void testCreateEncounter() throws EncounterServiceException {
    when(encounterService.createEncounter(TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER);
    HttpRequest<Encounter> request = HttpRequest.POST("/v1/encounters/", TEST_ENCOUNTER);
    HttpResponse<Encounter> response = client.toBlocking().exchange(request, Encounter.class);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void testCreateEncounterException() throws EncounterServiceException {
    when(encounterService.createEncounter(TEST_ENCOUNTER)).thenThrow(EncounterServiceException.class);
    HttpRequest<Encounter> request = HttpRequest.POST("/v1/encounters/", TEST_ENCOUNTER);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Encounter.class));
  }

  @Test
  void testGetEncounter() throws EncounterServiceException {
    String encounterId = TEST_ENCOUNTER.getEncounterId();
    when(encounterService.getEncounter(encounterId)).thenReturn(TEST_ENCOUNTER);

    HttpRequest<Object> request = HttpRequest.GET("/v1/encounters/" + encounterId);
    HttpResponse<Encounter> response = client.toBlocking().exchange(request, Encounter.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  void testGetEncounterException() throws EncounterServiceException {
    when(encounterService.getEncounter(TEST_ENCOUNTER.getEncounterId())).thenThrow(EncounterServiceException.class);
    HttpRequest<Object> request = HttpRequest.GET("/v1/encounters/" + TEST_ENCOUNTER.getEncounterId());
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Encounter.class));
  }

  @Test
  void testDeleteEncounter() throws EncounterServiceException {
    String encounterId = "31e59371-98ba-4ab4-854e-5e016965c3df";
    when(encounterService.deleteEncounter(encounterId)).thenReturn(1);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/encounters/" + encounterId);
    HttpResponse<Encounter> response = client.toBlocking().exchange(request, Encounter.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  void testDeleteEncounterException() throws EncounterServiceException {
    String encounterId = "31e59371-98ba-4ab4-854e-5e016965c3df";
    when(encounterService.deleteEncounter(encounterId)).thenThrow(EncounterServiceException.class);
    HttpRequest<Object> request = HttpRequest.DELETE("/v1/encounters/" + encounterId);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Void.class));
  }

  @Test
  void testUpdateEncounter() throws EncounterServiceException {
    when(encounterService.updateEncounter(TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER);
    HttpRequest<Object> request = HttpRequest.PUT("/v1/encounters/" + TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER);
    HttpResponse<Encounter> response = client.toBlocking().exchange(request, Encounter.class);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void testUpdateEncounterException() throws EncounterServiceException {
    when(encounterService.updateEncounter(TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER))
      .thenThrow(EncounterServiceException.class);
    HttpRequest<Object> request = HttpRequest.PUT("/v1/encounters/" + TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, Encounter.class));
  }

  @Test
  void testSearchEncounter() throws EncounterServiceException {
    HttpRequest<Object> request = HttpRequest.GET(
      "/v1/encounters/actions/search?patientId=" +
      TEST_PATIENT_ID +
      "&providerId=" +
      TEST_PROVIDER_ID +
      "&appointmentId=" +
      TEST_APPOINTMENT_ID +
      "&limit=" +
      TEST_LIMIT +
      "&page=" +
      TEST_PAGE
    );
    EncounterCollection encounterCollection = new EncounterCollection();
    when(encounterService.listEncounters(any(SearchCriteria.class))).thenReturn(encounterCollection);
    HttpResponse<EncounterCollection> response = client.toBlocking().exchange(request, EncounterCollection.class);
    assertEquals(HttpStatus.OK, response.status());
    verify(encounterService, times(1)).listEncounters(searchCriteriaCaptor.capture());
    assertEquals(TEST_PATIENT_ID, searchCriteriaCaptor.getValue().getPatientId());
    assertEquals(TEST_PROVIDER_ID, searchCriteriaCaptor.getValue().getProviderId());
    assertEquals(TEST_APPOINTMENT_ID, searchCriteriaCaptor.getValue().getAppointmentId());
    assertEquals(TEST_PAGE, searchCriteriaCaptor.getValue().getPage());
    assertEquals(TEST_LIMIT, searchCriteriaCaptor.getValue().getLimit());
    assertEquals(encounterCollection, response.body());
  }

  @Test
  void testSearchEncounterException() throws EncounterServiceException {
    HttpRequest<Object> request = HttpRequest.GET(
      "/v1/encounters/actions/search?patientId=" +
      TEST_PATIENT_ID +
      "&providerId=" +
      TEST_PROVIDER_ID +
      "&appointmentId=" +
      TEST_APPOINTMENT_ID +
      "&limit=" +
      TEST_LIMIT +
      "&page=" +
      TEST_PAGE
    );
    when(encounterService.listEncounters(any(SearchCriteria.class))).thenThrow(EncounterServiceException.class);
    assertThrows(
      HttpClientResponseException.class,
      () -> client.toBlocking().exchange(request, EncounterCollection.class)
    );
  }

  private CodeSummary buildCodeSummary() {
    CodeSummary codeSummary = new CodeSummary();
    codeSummary.setCode(TEST_CODE);
    codeSummary.setText(TEST_TEXT);
    return codeSummary;
  }
}
