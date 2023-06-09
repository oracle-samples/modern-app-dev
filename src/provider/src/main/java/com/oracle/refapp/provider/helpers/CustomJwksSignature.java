/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.helpers;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.jwks.JwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwksCache;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignature;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Replaces(JwksSignature.class)
@Singleton
public class CustomJwksSignature implements JwksCache, SignatureConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(CustomJwksSignature.class);
  private final JwkValidator jwkValidator;
  private final CustomJwksSignatureConfiguration jwksSignatureConfiguration;
  private volatile Instant jwkSetCachedAt;
  private volatile JWKSet jwkSet;

  @Inject
  public CustomJwksSignature(CustomJwksSignatureConfiguration jwksSignatureConfiguration, JwkValidator jwkValidator)
    throws ParseException {
    this.jwksSignatureConfiguration = jwksSignatureConfiguration;
    this.jwkValidator = jwkValidator;
    this.jwkSet = JWKSet.parse(this.jwksSignatureConfiguration.getKeys());
  }

  @Override
  public String supportedAlgorithmsMessage() {
    String message =
      this.getJsonWebKeys()
        .stream()
        .map(JWK::getAlgorithm)
        .map(Algorithm::getName)
        .reduce((a, b) -> a + ", " + b)
        .map(s -> "Only the " + s)
        .orElse("No");
    return message + " algorithms are supported";
  }

  @Override
  public boolean supports(JWSAlgorithm algorithm) {
    Stream var10000 = this.getJsonWebKeys().stream().map(JWK::getAlgorithm);
    algorithm.getClass();
    return var10000.anyMatch(algorithm::equals);
  }

  @Override
  public boolean verify(SignedJWT jwt) throws JOSEException {
    List<JWK> matches = this.matches(jwt, (JWKSet) this.getJWKSet().orElse(null));
    if (LOG.isDebugEnabled()) {
      LOG.debug("Found {} matching JWKs", matches.size());
    }

    return matches != null && !matches.isEmpty() ? this.verify(matches, jwt) : false;
  }

  @Override
  public boolean isPresent() {
    return this.jwkSet != null;
  }

  @Override
  public boolean isExpired() {
    Instant cachedAt = this.jwkSetCachedAt;
    return cachedAt != null && Instant.now().isAfter(cachedAt);
  }

  @Override
  public void clear() {
    this.jwkSet = null;
    this.jwkSetCachedAt = null;
  }

  @Override
  public Optional<List<String>> getKeyIds() {
    JWKSet jwkSet = this.jwkSet;
    if (jwkSet != null) {
      List<String> keyIds = new ArrayList(jwkSet.getKeys().size());
      Iterator var3 = jwkSet.getKeys().iterator();

      while (var3.hasNext()) {
        JWK key = (JWK) var3.next();
        String keyId = key.getKeyID();
        if (keyId != null) {
          keyIds.add(keyId);
        }
      }

      if (keyIds.isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(keyIds);
      }
    } else {
      return Optional.empty();
    }
  }

  protected List<JWK> matches(SignedJWT jwt, @Nullable JWKSet jwkSet) {
    List<JWK> matches = Collections.emptyList();
    if (jwkSet != null) {
      JWKMatcher.Builder builder = new JWKMatcher.Builder();
      if (this.jwksSignatureConfiguration.getKeyType() != null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Key Type: {}", this.jwksSignatureConfiguration.getKeyType());
        }

        builder = builder.keyType(this.jwksSignatureConfiguration.getKeyType());
      }

      String keyId = jwt.getHeader().getKeyID();
      if (LOG.isDebugEnabled()) {
        LOG.debug("JWT Key ID: {}", keyId);
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug("JWK Set Key IDs: {}", String.join(",", (Iterable) this.getKeyIds().orElse(Collections.emptyList())));
      }

      if (keyId != null) {
        builder = builder.keyID(keyId);
      }

      matches = (new JWKSelector(builder.build())).select(jwkSet);
    }

    return matches;
  }

  protected boolean verify(List<JWK> matches, SignedJWT jwt) {
    return matches.stream().anyMatch(jwk -> this.jwkValidator.validate(jwt, jwk));
  }

  private List<JWK> getJsonWebKeys() {
    return (List) this.getJWKSet().map(JWKSet::getKeys).orElse(Collections.emptyList());
  }

  private Optional<JWKSet> getJWKSet() {
    JWKSet jwkSet = this.jwkSet;
    return Optional.ofNullable(jwkSet);
  }
}
