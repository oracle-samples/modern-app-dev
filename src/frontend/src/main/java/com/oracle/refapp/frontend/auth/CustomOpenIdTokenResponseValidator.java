/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.auth;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.oauth2.client.OpenIdProviderMetadata;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdClaims;
import io.micronaut.security.oauth2.endpoint.token.response.OpenIdTokenResponse;
import io.micronaut.security.oauth2.endpoint.token.response.validation.DefaultOpenIdTokenResponseValidator;
import io.micronaut.security.oauth2.endpoint.token.response.validation.NonceClaimValidator;
import io.micronaut.security.oauth2.endpoint.token.response.validation.OpenIdClaimsValidator;
import io.micronaut.security.oauth2.endpoint.token.response.validation.OpenIdTokenResponseValidator;
import io.micronaut.security.token.jwt.validator.GenericJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JwtValidator;
import jakarta.inject.Singleton;
import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Replaces(DefaultOpenIdTokenResponseValidator.class)
@Singleton
public class CustomOpenIdTokenResponseValidator implements OpenIdTokenResponseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(CustomOpenIdTokenResponseValidator.class);
  private final Collection<OpenIdClaimsValidator> openIdClaimsValidators;
  private final Collection<GenericJwtClaimsValidator> genericJwtClaimsValidators;
  private final NonceClaimValidator nonceClaimValidator;
  private final CustomJwksSignature jwksSignature;

  public CustomOpenIdTokenResponseValidator(
    Collection<OpenIdClaimsValidator> idTokenValidators,
    Collection<GenericJwtClaimsValidator> genericJwtClaimsValidators,
    @Nullable NonceClaimValidator nonceClaimValidator,
    CustomJwksSignature jwksSignature
  ) {
    this.openIdClaimsValidators = idTokenValidators;
    this.genericJwtClaimsValidators = genericJwtClaimsValidators;
    this.nonceClaimValidator = nonceClaimValidator;
    this.jwksSignature = jwksSignature;
  }

  public Optional<JWT> validate(
    OauthClientConfiguration clientConfiguration,
    OpenIdProviderMetadata openIdProviderMetadata,
    OpenIdTokenResponse openIdTokenResponse,
    @Nullable String nonce
  ) {
    Optional<JWT> jwt = this.parseJwtWithValidSignature(openIdTokenResponse);
    if (jwt.isPresent()) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("JWT signature validation succeeded. Validating claims...");
      }

      return this.validateClaims(clientConfiguration, openIdProviderMetadata, jwt.get(), nonce);
    } else {
      if (LOG.isErrorEnabled()) {
        LOG.error("JWT signature validation failed for provider [{}]", clientConfiguration.getName());
      }

      return Optional.empty();
    }
  }

  @NonNull
  protected Optional<JWT> validateClaims(
    @NonNull OauthClientConfiguration clientConfiguration,
    @NonNull OpenIdProviderMetadata openIdProviderMetadata,
    @NonNull JWT jwt,
    @Nullable String nonce
  ) {
    try {
      JWTClaimsSet claimsSet = jwt.getJWTClaimsSet();
      OpenIdClaims claims = new JWTOpenIdClaims(claimsSet);
      if (this.genericJwtClaimsValidators.stream().allMatch(validator -> validator.validate(claims, null))) {
        if (
          this.openIdClaimsValidators.stream()
            .allMatch(validator -> validator.validate(claims, clientConfiguration, openIdProviderMetadata))
        ) {
          if (this.nonceClaimValidator == null) {
            if (LOG.isTraceEnabled()) {
              LOG.trace(
                "Skipping nonce validation because no bean of type {} present. ",
                NonceClaimValidator.class.getSimpleName()
              );
            }

            return Optional.of(jwt);
          }

          if (this.nonceClaimValidator.validate(claims, clientConfiguration, openIdProviderMetadata, nonce)) {
            return Optional.of(jwt);
          }

          if (LOG.isErrorEnabled()) {
            LOG.error(
              "Nonce {} validation failed for claims {}",
              nonce,
              claims
                .getClaims()
                .keySet()
                .stream()
                .map(key -> {
                  return key + "=" + claims.getClaims().get(key);
                })
                .collect(Collectors.joining(", ", "{", "}"))
            );
          }
        } else if (LOG.isErrorEnabled()) {
          LOG.error("JWT OpenID specific claims validation failed for provider [{}]", clientConfiguration.getName());
        }
      } else if (LOG.isErrorEnabled()) {
        LOG.error("JWT generic claims validation failed for provider [{}]", clientConfiguration.getName());
      }
    } catch (ParseException var7) {
      if (LOG.isErrorEnabled()) {
        LOG.error("Failed to parse the JWT returned from provider [{}]", clientConfiguration.getName(), var7);
      }
    }

    return Optional.empty();
  }

  @NonNull
  protected Optional<JWT> parseJwtWithValidSignature(@NonNull OpenIdTokenResponse openIdTokenResponse) {
    return JwtValidator
      .builder()
      .withSignatures(this.jwksSignature)
      .build()
      .validate(openIdTokenResponse.getIdToken(), null);
  }
}
