/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.auth;

import com.nimbusds.jose.jwk.KeyType;
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Singleton
@ConfigurationProperties("micronaut.security.token.jwt.signatures.static-jwks")
public class CustomJwksSignatureConfiguration {

  private KeyType keyType;
  private String keys;
}
