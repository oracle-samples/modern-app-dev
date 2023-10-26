/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient;

import com.oracle.refapp.patient.domain.entity.PatientEntity;
import com.oracle.refapp.patient.models.Gender;
import com.oracle.refapp.patient.models.Provider;
import java.util.Date;

public class TestUtils {

  public static final PatientEntity TEST_PATIENT_ENTITY = buildPatientEntity();
  public static final Provider TEST_PROVIDER = buildProvider();
  public static final Integer TEST_PATIENT_ID = 1;
  public static final String TEST_PATIENT_USERNAME = "john_doe";
  public static final Integer TEST_PAGE = 0;
  public static final Integer TEST_LIMIT = 10;

  public static PatientEntity buildPatientEntity() {
    PatientEntity patientEntity = new PatientEntity();
    patientEntity.setId(TEST_PATIENT_ID);
    patientEntity.setUsername(TEST_PATIENT_USERNAME);
    patientEntity.setName("john");
    patientEntity.setPhone("9999123456");
    patientEntity.setEmail("jndoe@uho.com");
    patientEntity.setGender(Gender.MALE);
    patientEntity.setZip("600033");
    patientEntity.setCity("SomeCity");
    patientEntity.setCountry("SomeCountry");
    patientEntity.setPrimaryCareProviderId(2);
    patientEntity.setDob(new Date());
    return patientEntity;
  }

  public static Provider buildProvider() {
    Provider provider = new Provider();
    provider.setFirstName("TEST_PROVIDER");
    return provider;
  }
}
