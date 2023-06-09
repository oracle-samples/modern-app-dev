/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import com.oracle.refapp.service.IdcsService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class MockIdcsService extends IdcsService {

  public MockIdcsService() {
    super(null, null, null, null);
  }

  @Override
  public String getAuthToken() {
    return "mock-token";
  }
}
