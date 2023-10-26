/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.mapper;

import com.oracle.refapp.patient.domain.entity.PatientEntity;
import com.oracle.refapp.patient.models.CreatePatientDetailsRequest;
import com.oracle.refapp.patient.models.Patient;
import com.oracle.refapp.patient.models.UpdatePatientDetailsRequest;
import java.time.LocalDate;
import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jsr330")
public interface PatientMapper {
  default LocalDate fromDate(Date date) {
    if (date == null) {
      return null;
    }
    return new java.sql.Date(date.getTime()).toLocalDate();
  }

  @Mapping(source = "patientEntity.dob", target = "dateOfBirth")
  Patient mapDomainToApiModels(PatientEntity patientEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "createPatientDetailsRequest.dateOfBirth", target = "dob")
  PatientEntity mapApiToDomainModels(CreatePatientDetailsRequest createPatientDetailsRequest);

  @Mapping(target = "id", ignore = true)
  @Mapping(source = "updatePatientDetailsRequest.dateOfBirth", target = "dob")
  @Mapping(target = "username", ignore = true)
  PatientEntity mapApiToDomainModels(UpdatePatientDetailsRequest updatePatientDetailsRequest);
}
