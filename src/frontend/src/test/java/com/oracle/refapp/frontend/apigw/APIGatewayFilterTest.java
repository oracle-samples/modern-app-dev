/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.apigw;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.oracle.refapp.frontend.auth.CustomIdTokenLoginHandler;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;

@MicronautTest(startApplication = false)
public class APIGatewayFilterTest {

  private static CustomIdTokenLoginHandler customIdTokenLoginHandler;
  private static APIGatewayFilter apiGatewayFilter;
  private static ClientFilterChain chain;

  @BeforeAll
  public static void setup() {
    customIdTokenLoginHandler = mock(CustomIdTokenLoginHandler.class);
    apiGatewayFilter = new APIGatewayFilter(customIdTokenLoginHandler);
    chain = mock(ClientFilterChain.class);
  }

  @Value("${test-values.test-id-token}")
  String testIdToken;

  @Test
  void testDoFilter() {
    MutableHttpRequest<?> request = (MutableHttpRequest<?>) HttpRequest
      .GET("/provider/")
      .setAttribute("accessToken", testIdToken);
    Publisher<? extends HttpResponse<?>> actualResponse = apiGatewayFilter.doFilter(request, chain);
    verify(chain, times(1)).proceed(request.bearerAuth(testIdToken));
    verifyNoMoreInteractions(chain);
  }

  @Test
  void testDoFilterException() {
    MutableHttpRequest<?> request = HttpRequest.GET("/provider/");
    assertThrows(OauthErrorResponseException.class, () -> apiGatewayFilter.doFilter(request, chain));
  }
}
