/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.refapp.clients.patient.ApiClient;
import com.oracle.refapp.clients.patient.ApiException;
import com.oracle.refapp.clients.patient.api.PatientApi;
import com.oracle.refapp.clients.patient.model.Patient;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
@Slf4j
public class PatientService {

  private final String patientServiceUrl;

  @Inject
  public PatientService(@ConfigProperty(name = "service.patient") String patientServiceUrl) {
    this.patientServiceUrl = patientServiceUrl;
  }

  @Traced
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public Patient getPatientDetails(Integer patientId, String accessToken) throws NoSuchPatientFoundException {
    if (log.isDebugEnabled()) log.debug("Making call to patient service {}", patientServiceUrl);
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(patientServiceUrl);
    apiClient.setBearerToken(accessToken);
    PatientApi patientApi = new PatientApi(apiClient);
    Patient patient;
    try {
      patient = patientApi.getPatient(patientId, accessToken);
    } catch (ApiException apiException) {
      log.error("Got exception calling patient service.", apiException);
      NoSuchPatientFoundException exception = new NoSuchPatientFoundException(
        "Got exception calling patient service with for patient with id: " + patientId
      );
      exception.addSuppressed(apiException);
      throw exception;
    }
    if (patient == null) {
      log.error("No such Patient found with id {} ", patientId);
      throw new NoSuchPatientFoundException("No such Patient found with id: " + patientId);
    }
    if (log.isDebugEnabled()) log.debug("patient entity {}", patient);
    return patient;
  }
}
