/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClient.class);

  private final HttpClient patientClient;
  private final HttpClient providerClient;
  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public ServiceClient(
    @Client(id = "patient") HttpClient patientClient,
    @Client(id = "provider") HttpClient providerClient
  ) {
    this.patientClient = patientClient;
    this.providerClient = providerClient;
  }

  public Map<String, Object> getPatientDetails(Integer patientId, String accessToken)
    throws IOException, NoSuchPatientFoundException {
    String patientUrl = "/v1/patients/" + patientId;
    LOGGER.warn("Calling patient service for {}", patientUrl);
    String responseString = getDetails(patientClient, patientUrl, accessToken);
    LOGGER.warn("patient entity {}", responseString);
    if (responseString.contains("NotFound")) {
      throw new NoSuchPatientFoundException("No patient found with id=" + patientId);
    }
    return mapper.readValue(responseString, new TypeReference<>() {});
  }

  public Map<String, Object> getProviderDetails(Integer providerId, String accessToken)
    throws IOException, NoSuchProviderFoundException {
    LOGGER.info("Calling provider service");
    String providerUrl = "/v1/providers/" + providerId;
    String responseString = getDetails(providerClient, providerUrl, accessToken);
    LOGGER.info("Provider entity: {}", responseString);
    if (responseString.contains("NotFound")) throw new NoSuchProviderFoundException(
      "No such provider found with id=" + providerId
    );
    return mapper.readValue(responseString, new TypeReference<>() {});
  }

  private String getDetails(HttpClient client, String url, String accessToken) {
    LOGGER.warn("Request to {}", url);
    HttpRequest<String> request = HttpRequest
      .<String>GET(url)
      .header("Authorization", accessToken)
      .accept(MediaType.APPLICATION_JSON_TYPE);
    HttpResponse<String> response = client.toBlocking().exchange(request, String.class);
    LOGGER.warn("Response received: {} - {} - {}", response.getStatus(), response.code(), response.body());
    return response.body();
  }
}
