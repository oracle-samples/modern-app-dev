/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.refapp.clients.provider.ApiClient;
import com.oracle.refapp.clients.provider.ApiException;
import com.oracle.refapp.clients.provider.api.ProviderApi;
import com.oracle.refapp.clients.provider.model.Provider;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
@Slf4j
public class ProviderService {

  private final String providerServiceUrl;

  @Inject
  public ProviderService(@ConfigProperty(name = "service.provider") String providerServiceUrl) {
    this.providerServiceUrl = providerServiceUrl;
  }

  @Traced
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public Provider getProviderDetails(Integer providerId, String accessToken) throws NoSuchProviderFoundException {
    if (log.isDebugEnabled()) log.debug("Making call to provider service {}", providerServiceUrl);
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(providerServiceUrl);
    apiClient.setBearerToken(accessToken);
    ProviderApi providerApi = new ProviderApi(apiClient);
    try {
      Provider provider = providerApi.getProvider(providerId);
      if (provider != null) {
        if (log.isDebugEnabled()) log.debug("provider entity {}", provider);
        return provider;
      }

      log.error("No such Provider found with id {} ", providerId);
      throw new NoSuchProviderFoundException("No such Provider found with id: " + providerId);
    } catch (ApiException apiException) {
      log.error("Got exception calling provider service.", apiException);
      NoSuchProviderFoundException exception = new NoSuchProviderFoundException(
        "Got exception calling provider service with for provider with id: " + providerId
      );
      exception.addSuppressed(apiException);
      throw exception;
    }
  }
}
