/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient;

import com.oracle.bmc.auth.internal.GetResourcePrincipalSessionTokenRequest;
import com.oracle.bmc.auth.internal.JWK;
import com.oracle.bmc.auth.internal.X509FederationClient;
import com.oracle.bmc.http.internal.ResponseHelper;
import io.micronaut.runtime.Micronaut;
import io.micronaut.serde.annotation.SerdeImport;

@SerdeImport(GetResourcePrincipalSessionTokenRequest.class)
@SerdeImport(JWK.class)
@SerdeImport(ResponseHelper.ErrorCodeAndMessage.class)
@SerdeImport(X509FederationClient.SecurityToken.class)
@SerdeImport(X509FederationClient.X509FederationRequest.class)
public class Application {

  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
