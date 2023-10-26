/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.service;

import static io.micronaut.http.HttpHeaders.ACCEPT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.frontend.auth.CustomIdTokenLoginHandler;
import com.oracle.refapp.frontend.models.Role;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Singleton;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Singleton
public class ApiService {

  private static final Logger LOG = LoggerFactory.getLogger(ApiService.class);

  private final HttpClient httpClient;
  private final ObjectMapper objectMapper;

  @Value("${apigw.url}")
  @ReflectiveAccess
  private String apigwUrl;

  @Value("${micronaut.security.oauth2.clients.patient.client-id}")
  @ReflectiveAccess
  private String patientClientId;

  @Value("${micronaut.security.oauth2.clients.provider.client-id}")
  @ReflectiveAccess
  private String providerClientId;

  @Value("${otel.exporter.zipkin.url}")
  private String apmDataUploadEndpoint;

  @Value("${otel.exporter.zipkin.path}")
  private String apmDataUploadPath;

  public ApiService(HttpClient httpClient) {
    this.httpClient = httpClient;
    this.objectMapper = new ObjectMapper();
  }

  public Mono<Object> genericGet(String accessToken, String path, HttpRequest<?> incomingRequest) {
    UriBuilder builder = UriBuilder.of(apigwUrl).path(path);
    incomingRequest.getParameters().forEach((key, value) -> builder.queryParam(key, String.join(",", value)));
    HttpRequest<?> request = HttpRequest
      .GET(builder.build())
      .header(ACCEPT, "application/json")
      .bearerAuth(accessToken);
    return Mono.from(httpClient.retrieve(request, Object.class));
  }

  public Mono<Object> genericPut(Object requestBody, String accessToken, String path) {
    UriBuilder builder = UriBuilder.of(apigwUrl).path(path);
    HttpRequest<?> request = HttpRequest
      .PUT(builder.build(), requestBody)
      .header(ACCEPT, "application/json")
      .bearerAuth(accessToken);
    return Mono.from(httpClient.retrieve(request, Object.class));
  }

  public Mono<Object> genericPost(Object requestBody, String accessToken, HttpRequest<?> incomingRequest) {
    UriBuilder builder = UriBuilder.of(apigwUrl).path(incomingRequest.getPath().replace("/home/api/", ""));
    HttpRequest<?> request = HttpRequest
      .POST(builder.build(), requestBody)
      .header(ACCEPT, "application/json")
      .bearerAuth(accessToken);
    return Mono.from(httpClient.retrieve(request, Object.class));
  }

  public Mono<HttpStatus> genericDelete(String accessToken, String path) {
    UriBuilder builder = UriBuilder.of(apigwUrl).path(path);
    HttpRequest<?> request = HttpRequest
      .DELETE(builder.build(), null)
      .header(ACCEPT, "application/json")
      .bearerAuth(accessToken);
    return Mono.from(httpClient.retrieve(request, HttpStatus.class));
  }

  public Mono<Map<String, String>> apm() {
    Map<String, String> apmMap = new HashMap<>();
    apmMap.put("serviceName", "uho-frontend");
    apmMap.put("webApplication", "uho-frontend-web");
    apmMap.put("ociDataUploadEndpoint", this.apmDataUploadEndpoint);
    apmMap.put("publicDataKey", this.apmDataUploadPath.split("dataKey=")[1]);
    return Mono.just(apmMap);
  }

  public Publisher<String> user(Authentication authentication, String accessToken) {
    Role role;
    String homePath;
    String clientId = authentication.getAttributes().get("client_id").toString();
    if (Objects.equals(clientId, patientClientId)) {
      role = Role.PATIENT;
      homePath = "/v1/patients/username/";
    } else if (Objects.equals(clientId, providerClientId)) {
      role = Role.PROVIDER;
      homePath = "/v1/providers/username/";
    } else {
      throw new IllegalStateException("Cannot retrieve role");
    }
    LOG.info("Retrieving user information for: {}", authentication.getName());
    UriBuilder builder = UriBuilder.of(apigwUrl).path(homePath + authentication.getName());
    URI uri = builder.build();
    HttpRequest<?> request = HttpRequest.GET(uri).header(ACCEPT, "application/json").bearerAuth(accessToken);
    Publisher<String> publisher = Mono.from(httpClient.retrieve(request, String.class));

    return Publishers.map(
      publisher,
      string -> {
        Map<String, Object> map;
        try {
          map = objectMapper.readValue(string, Map.class);
          map.put("role", role);
          return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
          return "";
        }
      }
    );
  }
}
