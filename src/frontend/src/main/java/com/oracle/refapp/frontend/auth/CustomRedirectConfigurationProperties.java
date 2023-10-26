/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.auth;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.security.config.SecurityConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ConfigurationProperties(CustomRedirectConfigurationProperties.PREFIX)
@Requires(notEnv = Environment.TEST)
@Introspected
public class CustomRedirectConfigurationProperties {

  public static final String PREFIX = SecurityConfigurationProperties.PREFIX + ".redirect";

  public static final String DEFAULT_LOGIN_SUCCESS = "/";

  @NonNull
  @NotBlank
  @ReflectiveAccess
  private String providerLoginSuccess = DEFAULT_LOGIN_SUCCESS;

  @NonNull
  @NotBlank
  @ReflectiveAccess
  private String patientLoginSuccess = DEFAULT_LOGIN_SUCCESS;
}
