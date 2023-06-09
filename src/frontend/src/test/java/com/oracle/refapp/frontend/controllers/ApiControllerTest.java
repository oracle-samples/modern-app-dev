/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.oracle.refapp.frontend.model.Provider;
import com.oracle.refapp.frontend.service.ApiService;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@MicronautTest
public class ApiControllerTest {

  @Inject
  @Client("/")
  HttpClient client;

  @Inject
  ApiService apiService;

  @MockBean(ApiService.class)
  ApiService mockedApiService() {
    return mock(ApiService.class);
  }

  @Value("${test-values.test-cookie}")
  String testCookie;

  @Value("${test-values.test-id-token}")
  String testIdToken;

  Integer testId = 1;
  String testGetPath = "v1/providers/" + testId;
  String testPutPath = "v1/providers/" + testId;
  String testPostPath = "v1/providers";
  String testDeletePath = "v1/providers/" + testId;
  String testUserInformationPath = "userInformation";
  String testName = "test_provider";
  Provider testProvider = buildTestProvider();

  private Provider buildTestProvider() {
    Provider provider = new Provider();
    provider.setId(testId);
    provider.setName(testName);
    return provider;
  }

  @Test
  void testGenericGet() {
    when(apiService.genericGet(eq(testIdToken), eq(testGetPath), any(HttpRequest.class)))
      .thenReturn(Mono.just(testProvider));
    HttpRequest<Object> request = HttpRequest
      .GET("/home/api/" + testGetPath)
      .cookie(Cookie.of("ACCESS_TOKEN", testIdToken))
      .header("Cookie", testCookie);
    HttpResponse<Provider> response = client.toBlocking().exchange(request, Provider.class);

    verify(apiService, times(1)).genericGet(eq(testIdToken), eq(testGetPath), any(HttpRequest.class));
    verifyNoMoreInteractions(apiService);
    assertEquals(response.getStatus(), HttpStatus.OK);
    assertEquals(response.getBody().get().getName(), testName);
  }

  @Test
  void testGenericPut() {
    when(apiService.genericPut(any(), eq(testIdToken), eq(testPutPath))).thenReturn(Mono.just(testProvider));
    HttpRequest<Provider> request = HttpRequest
      .PUT("/home/api/" + testPutPath, testProvider)
      .cookie(Cookie.of("ACCESS_TOKEN", testIdToken))
      .header("Cookie", testCookie);
    HttpResponse<Provider> response = client.toBlocking().exchange(request, Provider.class);

    verify(apiService, times(1)).genericPut(any(), eq(testIdToken), eq(testPutPath));
    verifyNoMoreInteractions(apiService);
    assertEquals(response.getStatus(), HttpStatus.OK);
    assertEquals(response.getBody().get().getName(), testName);
  }

  @Test
  void testGenericPost() {
    when(apiService.genericPost(any(), eq(testIdToken), any())).thenReturn(Mono.just(testProvider));
    HttpRequest<Provider> request = HttpRequest
      .POST("/home/api/" + testPostPath, testProvider)
      .cookie(Cookie.of("ACCESS_TOKEN", testIdToken))
      .header("Cookie", testCookie);
    HttpResponse<Provider> response = client.toBlocking().exchange(request, Provider.class);

    verify(apiService, times(1)).genericPost(any(), eq(testIdToken), any());
    verifyNoMoreInteractions(apiService);
    assertEquals(response.getStatus(), HttpStatus.OK);
    assertEquals(response.getBody().get().getName(), testName);
  }

  @Test
  void testGenericDelete() {
    when(apiService.genericDelete(testIdToken, testDeletePath)).thenReturn(Mono.just(HttpStatus.NO_CONTENT));
    HttpRequest<Object> request = HttpRequest
      .DELETE("/home/api/" + testDeletePath)
      .cookie(Cookie.of("ACCESS_TOKEN", testIdToken))
      .header("Cookie", testCookie);
    HttpResponse<HttpStatus> response = client.toBlocking().exchange(request, HttpStatus.class);

    verify(apiService, times(1)).genericDelete(testIdToken, testDeletePath);
    verifyNoMoreInteractions(apiService);
    assertEquals(response.getStatus(), HttpStatus.NO_CONTENT);
  }

  @Test
  void testUser() {
    Publisher<String> publisher = Mono.just("{\"id\":" + testId + ",\"name\": \"" + testName + "\"}");
    when(apiService.user(any(), eq(testIdToken))).thenReturn(publisher);
    HttpRequest<Object> request = HttpRequest
      .GET("/home/api/" + testUserInformationPath)
      .cookie(Cookie.of("ACCESS_TOKEN", testIdToken))
      .header("Cookie", testCookie);
    HttpResponse<Provider> response = client.toBlocking().exchange(request, Provider.class);
    assertEquals(response.getBody().get().getName(), testName);
    verify(apiService, times(1)).user(any(), eq(testIdToken));
    verifyNoMoreInteractions(apiService);
  }
}
