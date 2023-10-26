/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import jakarta.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class SearchFilterBuilder {

  public String build(SearchCriteria searchCriteria) {
    ArrayList<String> filters = new ArrayList<>();
    if (searchCriteria.getPatientId() != null) {
      filters.add("\"patient_id\": " + searchCriteria.getPatientId());
    }
    if (searchCriteria.getProviderId() != null) {
      filters.add("\"provider_id\": " + searchCriteria.getProviderId());
    }
    if (searchCriteria.getAppointmentId() != null) {
      filters.add("\"appointment_id\": " + searchCriteria.getAppointmentId());
    }
    return "{ " + String.join(",", filters) + " }";
  }
}
