/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oracle.refapp.frontend.telemetry.TelemetryClient;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.config.RedirectConfigurationProperties;
import io.micronaut.security.config.RefreshRedirectConfiguration;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.errors.PriorToLoginPersistence;
import io.micronaut.security.token.jwt.cookie.AccessTokenCookieConfiguration;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CustomIdTokenLoginHandlerTest {

  private static AccessTokenCookieConfiguration accessTokenCookieConfiguration;
  private static RedirectConfigurationProperties redirectConfigurationProperties;
  private static CustomRedirectConfigurationProperties customRedirectConfigurationProperties;
  private static PriorToLoginPersistence priorToLoginPersistence;
  private static TelemetryClient telemetryClient;
  private static CustomIdTokenLoginHandler customIdTokenLoginHandler;

  String testOpenIdToken = "testOpenIdToken";
  String testAccessToken = "testAccessToken";
  String testPrincipalName = "provider";
  String testCookieName = "Cookie";
  String testPatientAppClientId = "test-patient-id";
  String testProviderAppClientId = "test-provider-id";
  Authentication testAuthentication = new Authentication() {
    @Override
    public Map<String, Object> getAttributes() {
      return Map.of(
        "openIdToken",
        testOpenIdToken,
        "accessToken",
        testAccessToken,
        "client_id",
        testProviderAppClientId
      );
    }

    @Override
    public String getName() {
      return testPrincipalName;
    }
  };
  URI testUri = UriBuilder.of("/providers/1").scheme("https").build();
  HttpRequest<?> testRequest = HttpRequest.GET(testUri);

  @BeforeAll
  public static void setup() {
    accessTokenCookieConfiguration = mock(AccessTokenCookieConfiguration.class);
    redirectConfigurationProperties = mock(RedirectConfigurationProperties.class);
    customRedirectConfigurationProperties = mock(CustomRedirectConfigurationProperties.class);
    priorToLoginPersistence = mock(PriorToLoginPersistence.class);
    telemetryClient = mock(TelemetryClient.class);
    RefreshRedirectConfiguration redirectConfiguration = () -> "www.example.com";
    when(redirectConfigurationProperties.isEnabled()).thenReturn(true);
    when(redirectConfigurationProperties.getRefresh()).thenReturn(redirectConfiguration);
    when(redirectConfigurationProperties.getLoginSuccess()).thenReturn("");
    when(customRedirectConfigurationProperties.getPatientLoginSuccess()).thenReturn("/");
    when(customRedirectConfigurationProperties.getProviderLoginSuccess()).thenReturn("/");
    customIdTokenLoginHandler =
      new CustomIdTokenLoginHandler(
        accessTokenCookieConfiguration,
        redirectConfigurationProperties,
        customRedirectConfigurationProperties,
        priorToLoginPersistence,
        telemetryClient
      );
    when(accessTokenCookieConfiguration.getCookieName()).thenReturn("Cookie");
  }

  @Test
  void testGetCookies() {
    List<Cookie> actualResponse = customIdTokenLoginHandler.getCookies(testAuthentication, testRequest);
    assertNotNull(actualResponse);
    assertEquals(2, actualResponse.size());
    assertEquals(testCookieName, actualResponse.get(0).getName());
    assertEquals(testOpenIdToken, actualResponse.get(0).getValue());
    assertTrue(actualResponse.get(0).isSecure());
  }

  @Test
  void testGetCookiesException() {
    Authentication authWithoutOpenIdToken = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
      }

      @Override
      public String getName() {
        return null;
      }
    };
    assertThrows(
      OauthErrorResponseException.class,
      () -> customIdTokenLoginHandler.getCookies(authWithoutOpenIdToken, testRequest)
    );
    Authentication authWithoutAccessToken = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Map.of("openIdToken", testOpenIdToken);
      }

      @Override
      public String getName() {
        return null;
      }
    };
    assertThrows(
      OauthErrorResponseException.class,
      () -> customIdTokenLoginHandler.getCookies(authWithoutAccessToken, testRequest)
    );
  }

  @Test
  void testLoginSuccess() {
    when(priorToLoginPersistence.getOriginalUri(eq(testRequest), any(MutableHttpResponse.class)))
      .thenReturn(Optional.of(testUri));
    MutableHttpResponse<?> actualResponse = customIdTokenLoginHandler.loginSuccess(testAuthentication, testRequest);
    assertNotNull(actualResponse);
    verify(priorToLoginPersistence, times(1)).getOriginalUri(eq(testRequest), any(MutableHttpResponse.class));
  }

  @Test
  void testGetRedirectUrl() {
    CustomIdTokenLoginHandler customIdTokenLoginHandler = new CustomIdTokenLoginHandler(
      accessTokenCookieConfiguration,
      redirectConfigurationProperties,
      customRedirectConfigurationProperties,
      null,
      telemetryClient
    );
    MutableHttpResponse<?> response = customIdTokenLoginHandler.loginSuccess(testAuthentication, testRequest);
    assertNotNull(response);
  }

  @Test
  void testInvalidAccessToken() {
    Authentication authentication = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Map.of(
          "openIdToken",
          testOpenIdToken,
          "accessToken",
          new Object(),
          "client_id",
          testProviderAppClientId
        );
      }

      @Override
      public String getName() {
        return testPrincipalName;
      }
    };
    when(priorToLoginPersistence.getOriginalUri(eq(testRequest), any(MutableHttpResponse.class)))
      .thenReturn(Optional.of(testUri));
    assertThrows(
      OauthErrorResponseException.class,
      () -> customIdTokenLoginHandler.loginSuccess(authentication, testRequest)
    );
  }

  @Test
  void testCreateSuccessResponseError() {
    RedirectConfigurationProperties redirectConfigurationProperties = mock(RedirectConfigurationProperties.class);
    RefreshRedirectConfiguration redirectConfiguration = () -> "www.example.com";
    when(redirectConfigurationProperties.isEnabled()).thenReturn(true);
    when(redirectConfigurationProperties.getRefresh()).thenReturn(redirectConfiguration);
    when(redirectConfigurationProperties.isEnabled()).thenReturn(true);
    when(redirectConfigurationProperties.getLoginSuccess()).thenReturn("^+");
    CustomIdTokenLoginHandler customIdTokenLoginHandler = new CustomIdTokenLoginHandler(
      accessTokenCookieConfiguration,
      redirectConfigurationProperties,
      customRedirectConfigurationProperties,
      null,
      telemetryClient
    );
    MutableHttpResponse<?> response = customIdTokenLoginHandler.loginSuccess(testAuthentication, testRequest);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
  }
}
