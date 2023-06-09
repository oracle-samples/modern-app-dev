/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.patient.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.patient.models.Provider;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.ReflectiveAccess;
import java.io.IOException;
import javax.inject.Singleton;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProviderServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProviderServiceClient.class);
  private final ObjectMapper mapper = new ObjectMapper();
  private final CloseableHttpClient httpClient = HttpClients.createDefault();

  @ReflectiveAccess
  @Value("${service.provider}")
  private String providerServiceUrl;

  public ProviderServiceClient() {
    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public Provider getProvider(Integer providerId, String accessToken) throws IOException, NoSuchProviderFoundException {
    LOGGER.debug("Calling provider service using url {}", providerServiceUrl);
    HttpUriRequest request = RequestBuilder
      .get(providerServiceUrl + "/v1/providers/" + providerId)
      .addHeader("Authorization", accessToken)
      .build();
    int statusCode;
    HttpEntity entity;
    try {
      CloseableHttpResponse response = httpClient.execute(request);
      entity = response.getEntity();
      statusCode = response.getStatusLine().getStatusCode();
    } catch (Exception e) {
      LOGGER.error("Provider Service Call Failed.", e);
      return null;
    }
    String responseString = EntityUtils.toString(entity);
    LOGGER.debug("Provider entity retrieved {}", responseString);
    if (statusCode != 200) {
      throw new NoSuchProviderFoundException("No such Provider found with id: " + providerId);
    }
    return mapper.readValue(responseString, Provider.class);
  }
}
