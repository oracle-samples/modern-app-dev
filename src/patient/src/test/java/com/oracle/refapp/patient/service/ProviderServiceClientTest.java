/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.oracle.refapp.patient.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.patient.models.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProviderServiceClientTest {

  private static final String TEST_PROVIDER_URL = "test/provider";
  private static final Integer PROVIDER_ID = 2;
  private static final String ACCESS_TOKEN = "testToken";

  private final CloseableHttpClient mockedHttpClient = mock(CloseableHttpClient.class);
  private final CloseableHttpResponse mockedHttpResponse = mock(CloseableHttpResponse.class);
  private final HttpEntity mockedHttpEntity = mock(HttpEntity.class);
  private final StatusLine mockedStatusLine = mock(StatusLine.class);
  private final ProviderServiceClient providerServiceClient = new ProviderServiceClient();

  @BeforeEach
  public void setup() throws IllegalAccessException, NoSuchFieldException, IOException {
    InputStream is = null;
    try {
      Field providerServiceUrlField = ProviderServiceClient.class.getDeclaredField("providerServiceUrl");
      providerServiceUrlField.setAccessible(true);
      providerServiceUrlField.set(providerServiceClient, TEST_PROVIDER_URL);
      Field httpClientField = ProviderServiceClient.class.getDeclaredField("httpClient");
      httpClientField.setAccessible(true);
      httpClientField.set(providerServiceClient, mockedHttpClient);
      when(mockedHttpClient.execute(any())).thenReturn(mockedHttpResponse);
      when(mockedHttpResponse.getEntity()).thenReturn(mockedHttpEntity);
      when(mockedHttpResponse.getStatusLine()).thenReturn(mockedStatusLine);
      when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
      String providerJson = "{\"email\": \"provider@uho.com\",\"firstName\": \"provider\"}";
      is = new ByteArrayInputStream(providerJson.getBytes());
      when(mockedHttpEntity.getContent()).thenReturn(is);
    } finally {
      if (is != null) {
        is.close();
      }
    }
  }

  @Test
  public void testGetProviderSuccess() throws IOException, NoSuchProviderFoundException {
    when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    Provider provider = providerServiceClient.getProvider(PROVIDER_ID, ACCESS_TOKEN);
    assertEquals("provider", provider.getFirstName());
  }

  @Test
  public void testGetProviderFailed() {
    when(mockedStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    assertThrows(
      NoSuchProviderFoundException.class,
      () -> providerServiceClient.getProvider(PROVIDER_ID, ACCESS_TOKEN)
    );
  }
}
