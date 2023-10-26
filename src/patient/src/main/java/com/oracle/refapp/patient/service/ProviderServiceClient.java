/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import com.oracle.refapp.patient.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.patient.models.Provider;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProviderServiceClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProviderServiceClient.class);

  @ReflectiveAccess
  @Value("${service.provider}")
  private String providerServiceUrl;

  private final HttpClient providerClient;

  public ProviderServiceClient(@Client(id = "provider") HttpClient providerClient) {
    this.providerClient = providerClient;
  }

  public Provider getProvider(Integer providerId, String accessToken) throws NoSuchProviderFoundException {
    LOGGER.debug("Calling provider service using url {}", providerServiceUrl);
    String providerUrl = "/v1/providers/" + providerId;
    HttpRequest<Provider> request = HttpRequest.<Provider>GET(providerUrl).header("Authorization", accessToken);
    HttpResponse<Provider> response = providerClient.toBlocking().exchange(request);
    if (response.getStatus() != HttpStatus.OK) {
      throw new NoSuchProviderFoundException("No such Provider found with id: " + providerId);
    }
    return response.body();
  }
}
