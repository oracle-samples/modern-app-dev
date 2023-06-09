/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.service;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.frontend.model.Provider;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@MicronautTest
public class ApiServiceTest {

  @Inject
  HttpClient httpClient;

  @Inject
  ApiService apiService = new ApiService(httpClient);

  @MockBean(HttpClient.class)
  HttpClient mockedHttpClient() {
    return mock(HttpClient.class);
  }

  @Value("${apigw.url}")
  String testApigwUrl;

  @Value("${test-values.test-cookie}")
  String testCookie;

  @Value("${test-values.test-id-token}")
  String testIdToken;

  Integer testId = 1;
  String testGetPath = "v1/providers/" + testId;
  String testPutPath = "v1/providers/" + testId;
  String testPostPath = "v1/providers";
  String testDeletePath = "v1/providers/" + testId;
  String testNameProvider = "test_provider";
  String testNamePatient = "test_patient";

  @Value("${micronaut.security.oauth2.clients.provider.client-id}")
  String testProviderClientId;

  @Value("${micronaut.security.oauth2.clients.patient.client-id}")
  String testPatientClientId;

  String testQueryParamName = "test-query";
  String testQueryParamValue = "test-query-value";
  Provider testProvider = buildTestProvider();

  private Provider buildTestProvider() {
    Provider provider = new Provider();
    provider.setId(testId);
    provider.setName(testNameProvider);
    return provider;
  }

  @Test
  void testGenericGetWithoutQueryParams() {
    HttpRequest<Object> request = HttpRequest
      .GET(testApigwUrl + "/" + testGetPath)
      .header(ACCEPT, "application/json")
      .setAttribute("idToken", testIdToken);
    when(httpClient.retrieve(any(), eq(Object.class))).thenReturn(Mono.just(testProvider));
    Mono<Object> response = apiService.genericGet(testIdToken, testGetPath, request);
    Provider responseProvider = (Provider) response.block();
    assertNotNull(responseProvider);
    assertEquals(responseProvider.getName(), testNameProvider);
    verify(httpClient, times(1)).retrieve(any(), eq(Object.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testGenericGetWithQueryParams() {
    URI uri = UriBuilder
      .of(testApigwUrl + "/" + testGetPath)
      .queryParam(testQueryParamName, testQueryParamValue)
      .build();
    HttpRequest<Object> request = HttpRequest
      .GET(uri)
      .header(ACCEPT, "application/json")
      .setAttribute("idToken", testIdToken);
    when(httpClient.retrieve(any(), eq(Object.class))).thenReturn(Mono.just(testProvider));
    Mono<Object> response = apiService.genericGet(testIdToken, testGetPath, request);
    Provider responseProvider = (Provider) response.block();
    assertNotNull(responseProvider);
    assertEquals(responseProvider.getName(), testNameProvider);
    verify(httpClient, times(1)).retrieve(any(), eq(Object.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testGenericPut() {
    HttpRequest<Provider> request = HttpRequest
      .PUT(testApigwUrl + "/" + testPutPath, testProvider)
      .header(ACCEPT, "application/json")
      .setAttribute("idToken", testIdToken);

    when(httpClient.retrieve(any(), eq(Object.class))).thenReturn(Mono.just(testProvider));
    Mono<Object> response = apiService.genericPut(testProvider, testIdToken, testPutPath);
    Provider responseProvider = (Provider) response.block();
    assertNotNull(responseProvider);
    assertEquals(responseProvider.getName(), testNameProvider);
    verify(httpClient, times(1)).retrieve(any(), eq(Object.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testGenericPost() {
    HttpRequest<Provider> request = HttpRequest
      .POST(testApigwUrl + "/" + testPostPath, testProvider)
      .header(ACCEPT, "application/json")
      .setAttribute("idToken", testIdToken);
    when(httpClient.retrieve(any(request.getClass()), eq(Object.class))).thenReturn(Mono.just(testProvider));
    Mono<Object> response = apiService.genericPost(testProvider, testIdToken, request);
    Provider responseProvider = (Provider) response.block();
    assertNotNull(responseProvider);
    assertEquals(responseProvider.getName(), testNameProvider);
    verify(httpClient, times(1)).retrieve(any(request.getClass()), eq(Object.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testGenericDelete() {
    when(httpClient.retrieve(any(), eq(HttpStatus.class))).thenReturn(Mono.just(HttpStatus.NO_CONTENT));
    Mono<HttpStatus> response = apiService.genericDelete(testIdToken, testDeletePath);
    verify(httpClient, times(1)).retrieve(any(), eq(HttpStatus.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testUserPatient() {
    Publisher<String> publisher = Mono.just("{\"id\":" + testId + ",\"name\": \"" + testNamePatient + "\"}");
    when(httpClient.retrieve(any(), eq(String.class))).thenReturn(publisher);
    Authentication authentication = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Map.of("client_id", testPatientClientId);
      }

      @Override
      public String getName() {
        return "patient";
      }
    };
    Publisher<String> responsePublisher = apiService.user(authentication, testIdToken);
    assertNotNull(responsePublisher);
    String expectedOutput = "{\"id\":1,\"name\":\"test_patient\",\"role\":\"PATIENT\"}";
    assertEquals(expectedOutput, Mono.from(responsePublisher).block());
    verify(httpClient, times(1)).retrieve(any(), eq(String.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testUserProvider() {
    Publisher<String> publisher = Mono.just("{\"id\":" + testId + ",\"name\": \"" + testNameProvider + "\"}");
    when(httpClient.retrieve(any(), eq(String.class))).thenReturn(publisher);
    Authentication authentication = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Map.of("client_id", testProviderClientId);
      }

      @Override
      public String getName() {
        return "provider";
      }
    };
    Publisher<String> responsePublisher = apiService.user(authentication, testIdToken);
    assertNotNull(responsePublisher);
    String expectedOutput = "{\"id\":1,\"name\":\"test_provider\",\"role\":\"PROVIDER\"}";
    assertEquals(expectedOutput, Mono.from(responsePublisher).block());
    verify(httpClient, times(1)).retrieve(any(), eq(String.class));
    verifyNoMoreInteractions(httpClient);
  }

  @Test
  void testUserError() {
    Publisher<String> publisher = Mono.just("{\"id\":" + testId + ",\"name\": \"" + "test" + "\"}");
    when(httpClient.retrieve(any(), eq(String.class))).thenReturn(publisher);
    Authentication authentication = new Authentication() {
      @Override
      public Map<String, Object> getAttributes() {
        return Map.of("client_id", "test");
      }

      @Override
      public String getName() {
        return "provider";
      }
    };
    assertThrows(IllegalStateException.class, () -> apiService.user(authentication, testIdToken));
  }

  @Test
  void testGenericGetWithWhitespace() {
    String city = "San%20Francisco";
    HttpRequest<Object> request = HttpRequest
      .GET(testApigwUrl + "/v1/providers/actions/search?city=" + city)
      .header(ACCEPT, "application/json")
      .header(CONTENT_TYPE, "text/plain; charset=utf-8")
      .setAttribute("idToken", testIdToken);
    when(httpClient.retrieve(any(), eq(Object.class))).thenReturn(Mono.just(testProvider));
    Mono<Object> response = apiService.genericGet(testIdToken, testGetPath, request);
    Provider responseProvider = (Provider) response.block();
    assertNotNull(responseProvider);
    assertEquals(responseProvider.getName(), testNameProvider);
    verify(httpClient, times(1)).retrieve(any(), eq(Object.class));
    verifyNoMoreInteractions(httpClient);
  }
}
