/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oracle.refapp.config.HttpClientFactory;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@MicronautTest
@Property(name = "service.patient", value = "servicePatient")
@Property(name = "service.provider", value = "serviceProvider")
public class ServiceClientTest {

  private static final CloseableHttpResponse patientHttpResponse = mock(CloseableHttpResponse.class);
  private static final CloseableHttpResponse patientHttpErrorResponse = mock(CloseableHttpResponse.class);
  private static final CloseableHttpResponse providerHttpResponse = mock(CloseableHttpResponse.class);
  private static final CloseableHttpResponse providerHttpErrorResponse = mock(CloseableHttpResponse.class);
  private final CloseableHttpClient client = mock(CloseableHttpClient.class);

  @Inject
  private HttpClientFactory httpClientFactory;

  @MockBean(HttpClientFactory.class)
  HttpClientFactory mockedHttpClientFactory() {
    return mock(HttpClientFactory.class);
  }

  @Inject
  private ServiceClient serviceClient;

  @BeforeEach
  public void init() throws IOException {
    InputStream is = null;
    try {
      when(httpClientFactory.getHttpClient()).thenReturn(client);
      HttpEntity patientEntity = mock(HttpEntity.class);
      String patientJson = "{\"email\": \"patient@uho.com\",\"name\": \"patient\"}";
      is = new ByteArrayInputStream(patientJson.getBytes());
      when(patientEntity.getContent()).thenReturn(is);
      when(patientEntity.toString()).thenReturn(patientJson);
      when(patientHttpResponse.getEntity()).thenReturn(patientEntity);
      HttpEntity providerEntity = mock(HttpEntity.class);
      String providerJson = "{\"email\": \"provider@uho.com\",\"firstName\": \"provider\"}";
      is = new ByteArrayInputStream(providerJson.getBytes());
      when(providerEntity.getContent()).thenReturn(is);
      when(providerEntity.toString()).thenReturn(providerJson);
      when(providerHttpResponse.getEntity()).thenReturn(providerEntity);

      HttpEntity providerErrorEntity = mock(HttpEntity.class);
      is = new ByteArrayInputStream("NotFound".getBytes());
      when(providerErrorEntity.getContent()).thenReturn(is);
      when(providerErrorEntity.toString()).thenReturn("NotFound");
      when(providerHttpErrorResponse.getEntity()).thenReturn(providerErrorEntity);

      HttpEntity patientErrorEntity = mock(HttpEntity.class);
      is = new ByteArrayInputStream("NotFound".getBytes());
      when(patientErrorEntity.getContent()).thenReturn(is);
      when(patientErrorEntity.toString()).thenReturn("NotFound");
      when(patientHttpErrorResponse.getEntity()).thenReturn(patientErrorEntity);
    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

  @Test
  public void testGetProviderDetails() throws IOException, NoSuchProviderFoundException {
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) ->
          req != null && req.getURI().toString().equals("serviceProvider/v1/providers/3")
        )
      )
    )
      .thenReturn(providerHttpResponse);
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) -> !req.getURI().toString().equals("serviceProvider/v1/providers/3"))
      )
    )
      .thenReturn(patientHttpResponse);
    Map<String, Object> output = serviceClient.getProviderDetails(TEST_PROVIDER_ID, TEST_TOKEN);
    assertEquals("provider@uho.com", output.get("email"));
    assertEquals("provider", output.get("firstName"));
  }

  @Test
  public void testGetPatientDetails() throws IOException, NoSuchProviderFoundException {
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) ->
          req != null && req.getURI().toString().equals("serviceProvider/v1/providers/3")
        )
      )
    )
      .thenReturn(providerHttpResponse);
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) -> !req.getURI().toString().equals("serviceProvider/v1/providers/3"))
      )
    )
      .thenReturn(patientHttpResponse);
    Map<String, Object> output = serviceClient.getProviderDetails(TEST_PATIENT_ID, TEST_TOKEN);
    assertEquals("patient@uho.com", output.get("email"));
    assertEquals("patient", output.get("name"));
  }

  @Test
  public void testGetProviderDetailsException() throws IOException {
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) ->
          req != null && req.getURI().toString().equals("serviceProvider/v1/providers/3")
        )
      )
    )
      .thenReturn(providerHttpErrorResponse);
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) -> !req.getURI().toString().equals("serviceProvider/v1/providers/3"))
      )
    )
      .thenReturn(patientHttpResponse);
    assertThrows(
      NoSuchProviderFoundException.class,
      () -> serviceClient.getProviderDetails(TEST_PROVIDER_ID, TEST_TOKEN)
    );
  }

  @Test
  public void testGetPatientDetailsException() throws IOException {
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) ->
          req != null && req.getURI().toString().equals("serviceProvider/v1/providers/3")
        )
      )
    )
      .thenReturn(providerHttpResponse);
    when(
      client.execute(
        Mockito.argThat((HttpUriRequest req) -> !req.getURI().toString().equals("serviceProvider/v1/providers/3"))
      )
    )
      .thenReturn(patientHttpErrorResponse);
    assertThrows(NoSuchPatientFoundException.class, () -> serviceClient.getPatientDetails(TEST_PATIENT_ID, TEST_TOKEN));
  }
}
