/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import com.oracle.refapp.clients.provider.model.Provider;
import com.oracle.refapp.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.service.ProviderService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class MockProviderService extends ProviderService {

  public MockProviderService() {
    super(null);
  }

  @Override
  public Provider getProviderDetails(Integer providerId, String accessToken) throws NoSuchProviderFoundException {
    var p = new Provider();
    p.setId(providerId);
    p.setFirstName("UHO");
    p.setLastName("Provider");
    return p;
  }
}
