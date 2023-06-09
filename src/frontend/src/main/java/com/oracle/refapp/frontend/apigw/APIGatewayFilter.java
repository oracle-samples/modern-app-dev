/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.apigw;

import com.oracle.refapp.frontend.auth.CustomIdTokenLoginHandler;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.errors.ObtainingAuthorizationErrorCode;
import org.reactivestreams.Publisher;

@Filter({ "/v1/providers/**", "/v1/patients/**", "/v1/appointments/**", "/v1/encounters/**" })
@Requires(notEnv = Environment.TEST)
public class APIGatewayFilter implements HttpClientFilter {

  private final CustomIdTokenLoginHandler customIdTokenLoginHandler;

  public APIGatewayFilter(CustomIdTokenLoginHandler customIdTokenLoginHandler) {
    this.customIdTokenLoginHandler = customIdTokenLoginHandler;
  }

  @Override
  public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
    String accessToken = String.valueOf(
      request
        .getAttribute("accessToken")
        .orElseThrow(() ->
          new OauthErrorResponseException(
            ObtainingAuthorizationErrorCode.SERVER_ERROR,
            "Cannot obtain an access token",
            null
          )
        )
    );
    return chain.proceed(request.bearerAuth(accessToken));
  }
}
