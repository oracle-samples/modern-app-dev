/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.auth;

import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.refapp.frontend.telemetry.TelemetryClient;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.core.util.functional.ThrowingSupplier;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationMode;
import io.micronaut.security.config.RedirectConfigurationProperties;
import io.micronaut.security.config.RedirectService;
import io.micronaut.security.config.SecurityConfigurationProperties;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.errors.ObtainingAuthorizationErrorCode;
import io.micronaut.security.errors.PriorToLoginPersistence;
import io.micronaut.security.oauth2.endpoint.token.response.IdTokenLoginHandler;
import io.micronaut.security.token.cookie.AccessTokenCookieConfiguration;
import jakarta.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Requires(property = SecurityConfigurationProperties.PREFIX + ".authentication", value = "idtoken")
@Singleton
@Replaces(IdTokenLoginHandler.class)
@Requires(notEnv = Environment.TEST)
public class CustomIdTokenLoginHandler extends IdTokenLoginHandler {

  private static final String ACCESS_TOKEN = "accessToken";

  private static final Logger LOG = LoggerFactory.getLogger(CustomIdTokenLoginHandler.class);

  private final String patientLoginSuccess;

  private final String providerLoginSuccess;

  @Value("${micronaut.security.oauth2.clients.patient.client-id}")
  @ReflectiveAccess
  private String patientAppClientId;

  @Value("${micronaut.security.oauth2.clients.provider.client-id}")
  @ReflectiveAccess
  private String providerAppClientId;

  private final TelemetryClient telemetryClient;

  public CustomIdTokenLoginHandler(
    AccessTokenCookieConfiguration accessTokenCookieConfiguration,
    RedirectConfigurationProperties redirectConfiguration,
    CustomRedirectConfigurationProperties customRedirectConfigurationProperties,
    RedirectService redirectService,
    @Nullable PriorToLoginPersistence priorToLoginPersistence,
    TelemetryClient telemetryClient
  ) {
    super(accessTokenCookieConfiguration, redirectConfiguration, redirectService, priorToLoginPersistence);
    this.patientLoginSuccess = customRedirectConfigurationProperties.getPatientLoginSuccess();
    this.providerLoginSuccess = customRedirectConfigurationProperties.getProviderLoginSuccess();
    this.telemetryClient = telemetryClient;
  }

  @Override
  public List<Cookie> getCookies(Authentication authentication, HttpRequest<?> request) {
    List<Cookie> cookies = new ArrayList<>(1);
    String idToken = parseIdToken(authentication)
      .orElseThrow(() ->
        new OauthErrorResponseException(
          ObtainingAuthorizationErrorCode.SERVER_ERROR,
          "Cannot obtain an access token",
          null
        )
      );

    Cookie jwtCookie = Cookie.of(accessTokenCookieConfiguration.getCookieName(), idToken);
    jwtCookie.configure(accessTokenCookieConfiguration, request.isSecure());
    jwtCookie.maxAge(cookieExpiration(authentication, request));
    cookies.add(jwtCookie);

    String accessToken = parseAccessToken(authentication)
      .orElseThrow(() ->
        new OauthErrorResponseException(
          ObtainingAuthorizationErrorCode.SERVER_ERROR,
          "Cannot obtain an access token",
          null
        )
      );
    Cookie accessTokenCookie = Cookie.of("ACCESS_TOKEN", accessToken);
    accessTokenCookie.configure(accessTokenCookieConfiguration, request.isSecure());
    accessTokenCookie.maxAge(cookieExpiration(authentication, request));
    cookies.add(accessTokenCookie);
    return cookies;
  }

  @Override
  public MutableHttpResponse<?> loginSuccess(Authentication authentication, HttpRequest<?> request) {
    postMetricData();
    return applyCookies(createSuccessResponse(authentication, request), getCookies(authentication, request));
  }

  protected MutableHttpResponse<?> createSuccessResponse(Authentication authentication, HttpRequest<?> request) {
    try {
      MutableHttpResponse<?> response = HttpResponse.status(HttpStatus.SEE_OTHER);
      ThrowingSupplier<URI, URISyntaxException> uriSupplier = () -> new URI(getRedirectUrl(authentication));
      if (priorToLoginPersistence != null) {
        Optional<URI> originalUri = priorToLoginPersistence.getOriginalUri(request, response);
        if (originalUri.isPresent()) {
          uriSupplier = originalUri::get;
        }
      }
      response.getHeaders().location(uriSupplier.get());
      return response;
    } catch (URISyntaxException e) {
      return HttpResponse.serverError();
    }
  }

  protected Optional<String> parseAccessToken(Authentication authentication) {
    Map<String, Object> attributes = authentication.getAttributes();
    if (!attributes.containsKey(ACCESS_TOKEN)) {
      if (LOG.isWarnEnabled()) {
        LOG.warn(
          "{} should be present in user details attributes to use {}:{}",
          ACCESS_TOKEN,
          SecurityConfigurationProperties.PREFIX + ".authentication",
          AuthenticationMode.IDTOKEN
        );
      }
      return Optional.empty();
    }
    Object idTokenObjet = attributes.get(ACCESS_TOKEN);
    if (!(idTokenObjet instanceof String)) {
      if (LOG.isWarnEnabled()) {
        LOG.warn(
          "{} present in user details attributes should be of type String to use {}:{}",
          ACCESS_TOKEN,
          SecurityConfigurationProperties.PREFIX + ".authentication",
          AuthenticationMode.IDTOKEN
        );
      }
      return Optional.empty();
    }
    return Optional.of((String) idTokenObjet);
  }

  private String getRedirectUrl(Authentication authentication) {
    if (authentication.getAttributes().containsKey("client_id")) {
      String clientId = (String) authentication.getAttributes().get("client_id");
      if (clientId.equals(patientAppClientId)) {
        return this.patientLoginSuccess;
      } else if (clientId.equals(providerAppClientId)) {
        return this.providerLoginSuccess;
      }
    }
    return this.loginSuccess;
  }

  private void postMetricData() {
    List<Datapoint> datapoints = new ArrayList<>(
      List.of(Datapoint.builder().timestamp(new Date(System.currentTimeMillis())).value(1.0).count(1).build())
    );

    telemetryClient.postMetricData("user-login", datapoints);
  }
}
