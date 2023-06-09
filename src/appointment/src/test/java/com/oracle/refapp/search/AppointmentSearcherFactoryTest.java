/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import static org.mockito.Mockito.mock;

import com.oracle.refapp.model.AppointmentSearchCriteria;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AppointmentSearcherFactoryTest {

  private final TimeRangeAppointmentSearcher timeRangeSearcher = mock(TimeRangeAppointmentSearcher.class);

  private final PatientAppointmentSearcher patientSearcher = mock(PatientAppointmentSearcher.class);

  private final ProviderAppointmentSearcher providerSearcher = mock(ProviderAppointmentSearcher.class);

  private final AppointmentSearcherFactory appointmentSearcherFactory = new AppointmentSearcherFactory(
    timeRangeSearcher,
    patientSearcher,
    providerSearcher
  );

  @Test
  @DisplayName("test factory returns testTimeRangeSearcher")
  public void testTimeRangeSearcherReturned() {
    AppointmentSearchCriteria searchCriteria = new AppointmentSearchCriteria();
    AppointmentSearcher searcher = appointmentSearcherFactory.getSearcher(searchCriteria);
    assert (searcher instanceof TimeRangeAppointmentSearcher);
  }

  @Test
  @DisplayName("test factory returns testPatientSearcher")
  public void testPatientSearcherReturned() {
    AppointmentSearchCriteria searchCriteria = new AppointmentSearchCriteria();
    searchCriteria.setPatientId(1);
    AppointmentSearcher searcher = appointmentSearcherFactory.getSearcher(searchCriteria);
    assert (searcher instanceof PatientAppointmentSearcher);
  }

  @Test
  @DisplayName("test factory returns testProviderSearcher")
  public void testProviderSearcherReturned() {
    AppointmentSearchCriteria searchCriteria = new AppointmentSearchCriteria();
    searchCriteria.setProviderId(2);
    AppointmentSearcher searcher = appointmentSearcherFactory.getSearcher(searchCriteria);
    assert (searcher instanceof ProviderAppointmentSearcher);
  }
}
