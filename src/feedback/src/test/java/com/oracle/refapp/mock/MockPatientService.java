/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import com.oracle.refapp.clients.patient.model.Patient;
import com.oracle.refapp.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.service.PatientService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class MockPatientService extends PatientService {

  public MockPatientService() {
    super(null);
  }

  @Override
  public Patient getPatientDetails(Integer patientId, String accessToken) throws NoSuchPatientFoundException {
    var p = new Patient();
    p.setId(patientId);
    p.setName("UHO Patient");
    p.setEmail("patient@uho.com");
    return p;
  }
}
