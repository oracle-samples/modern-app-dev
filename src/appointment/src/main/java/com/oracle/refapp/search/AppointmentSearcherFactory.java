/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import com.oracle.refapp.model.AppointmentSearchCriteria;
import jakarta.inject.Singleton;

@Singleton
public class AppointmentSearcherFactory {

  private final AppointmentSearcher timeRangeSearcher;
  private final AppointmentSearcher patientSearcher;
  private final AppointmentSearcher providerSearcher;

  AppointmentSearcherFactory(
    TimeRangeAppointmentSearcher timeRangeSearcher,
    PatientAppointmentSearcher patientSearcher,
    ProviderAppointmentSearcher providerSearcher
  ) {
    this.timeRangeSearcher = timeRangeSearcher;
    this.patientSearcher = patientSearcher;
    this.providerSearcher = providerSearcher;
  }

  public AppointmentSearcher getSearcher(AppointmentSearchCriteria searchCriteria) {
    if (searchCriteria.getPatientId() == null && searchCriteria.getProviderId() == null) {
      return timeRangeSearcher;
    } else if (searchCriteria.getPatientId() != null) {
      return patientSearcher;
    } else if (searchCriteria.getProviderId() != null) {
      return providerSearcher;
    }
    return null;
  }
}
