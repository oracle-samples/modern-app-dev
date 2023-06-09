/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.refapp.clients.encounter.ApiClient;
import com.oracle.refapp.clients.encounter.ApiException;
import com.oracle.refapp.clients.encounter.api.EncounterApi;
import com.oracle.refapp.clients.encounter.model.Encounter;
import com.oracle.refapp.exceptions.NoSuchEncounterFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
@Slf4j
public class EncounterService {

  private final String encounterServiceUrl;

  @Inject
  public EncounterService(@ConfigProperty(name = "service.encounter") String encounterServiceUrl) {
    this.encounterServiceUrl = encounterServiceUrl;
  }

  @Traced
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public Encounter getEncounterDetails(String encounterId, String accessToken) throws NoSuchEncounterFoundException {
    if (log.isDebugEnabled()) log.debug("Making call to encounter service {}", encounterServiceUrl);
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(encounterServiceUrl);
    apiClient.setBearerToken(accessToken);
    EncounterApi encounterApi = new EncounterApi(apiClient);
    try {
      Encounter encounter = encounterApi.getEncounter(encounterId);
      if (encounter != null) {
        if (log.isDebugEnabled()) log.debug("encounter entity {}", encounter);
        return encounter;
      }
      log.error("No such Encounter found with id {}", encounterId);
      throw new NoSuchEncounterFoundException("No such Encounter found with id: " + encounterId);
    } catch (ApiException apiException) {
      log.error("Got exception calling encounter service.", apiException);
      NoSuchEncounterFoundException exception = new NoSuchEncounterFoundException(
        "Got exception calling encounter service with for encounter with id: " + encounterId
      );
      exception.addSuppressed(apiException);
      throw exception;
    }
  }
}
