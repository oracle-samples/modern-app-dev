/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oracle.refapp.config.HttpClientFactory;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClient.class);

  @ReflectiveAccess
  @Value("${service.patient}")
  private String patientServiceUrl;

  @ReflectiveAccess
  @Value("${service.provider}")
  private String providerServiceUrl;

  private final HttpClientFactory httpClientFactory;
  private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public ServiceClient(HttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }

  public Map<String, Object> getPatientDetails(Integer patientId, String accessToken)
    throws IOException, NoSuchPatientFoundException {
    LOGGER.debug("Calling patient service using url {}", patientServiceUrl);
    String patientUrl = patientServiceUrl + "/v1/patients/" + patientId;
    String responseString = getDetails(patientUrl, accessToken);
    LOGGER.debug("patient entity {}", responseString);
    if (responseString.contains("NotFound")) {
      throw new NoSuchPatientFoundException("No patient found with id=" + patientId);
    }
    return mapper.readValue(responseString, new TypeReference<>() {});
  }

  public Map<String, Object> getProviderDetails(Integer providerId, String accessToken)
    throws IOException, NoSuchProviderFoundException {
    LOGGER.debug("Calling provider service using url {}", providerServiceUrl);
    String providerUrl = providerServiceUrl + "/v1/providers/" + providerId;
    String responseString = getDetails(providerUrl, accessToken);
    LOGGER.debug("Provider entity: {}", responseString);
    if (responseString.contains("NotFound")) throw new NoSuchProviderFoundException(
      "No such provider found with id=" + providerId
    );
    return mapper.readValue(responseString, new TypeReference<>() {});
  }

  private String getDetails(String url, String accessToken) throws IOException {
    CloseableHttpClient httpClient = httpClientFactory.getHttpClient();
    HttpUriRequest request = RequestBuilder.get(url).addHeader("Authorization", accessToken).build();
    CloseableHttpResponse response = httpClient.execute(request);
    HttpEntity entity = response.getEntity();
    return EntityUtils.toString(entity);
  }
}
