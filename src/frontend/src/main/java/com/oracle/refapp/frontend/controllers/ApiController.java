/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.controllers;

import com.oracle.refapp.frontend.service.ApiService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.CookieValue;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import java.util.Map;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Controller("/api")
@ExecuteOn(TaskExecutors.BLOCKING)
public class ApiController {

  private final ApiService apiService;

  public ApiController(ApiService apiService) {
    this.apiService = apiService;
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get(uri = "/{path:.*}")
  public Mono<Object> genericGet(
    @CookieValue(value = "ACCESS_TOKEN") String accessToken,
    String path,
    HttpRequest<?> incomingRequest
  ) {
    return apiService.genericGet(accessToken, path, incomingRequest);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Put(uri = "/{path:.*}", consumes = MediaType.APPLICATION_JSON)
  public Mono<Object> genericPut(
    @Body Object requestBody,
    @CookieValue(value = "ACCESS_TOKEN") String accessToken,
    String path
  ) {
    return apiService.genericPut(requestBody, accessToken, path);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Post(uri = "/{path:.*}", consumes = MediaType.APPLICATION_JSON)
  public Mono<Object> genericPost(
    @Body Object requestBody,
    @CookieValue(value = "ACCESS_TOKEN") String accessToken,
    String path,
    HttpRequest<?> incomingRequest
  ) {
    return apiService.genericPost(requestBody, accessToken, incomingRequest);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Delete(uri = "/{path:.*}", consumes = MediaType.APPLICATION_JSON)
  public Mono<HttpStatus> genericDelete(@CookieValue(value = "ACCESS_TOKEN") String accessToken, String path) {
    return apiService.genericDelete(accessToken, path);
  }

  @Secured(SecurityRule.IS_AUTHENTICATED)
  @Get(uri = "/userInformation")
  public Publisher<String> user(
    @CookieValue(value = "ACCESS_TOKEN") String accessToken,
    @Nullable Authentication authentication
  ) {
    return apiService.user(authentication, accessToken);
  }

  @Secured(SecurityRule.IS_ANONYMOUS)
  @Get(uri = "/apmInformation")
  public Publisher<Map<String, String>> apm() {
    return apiService.apm();
  }
}
