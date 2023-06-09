/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import com.oracle.refapp.clients.encounter.model.Encounter;
import com.oracle.refapp.exceptions.NoSuchEncounterFoundException;
import com.oracle.refapp.service.EncounterService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class MockEncounterService extends EncounterService {

  public MockEncounterService() {
    super(null);
  }

  @Override
  public Encounter getEncounterDetails(String encounterId, String accessToken) throws NoSuchEncounterFoundException {
    var e = new Encounter();
    e.encounterId(encounterId);
    return e;
  }
}
