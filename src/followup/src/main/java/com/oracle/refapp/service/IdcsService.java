/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.exceptions.ProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
@Slf4j
public class IdcsService {

  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private final String idcs;
  private final String clientId;
  private final String clientSecret;
  private final String apiGw;

  @Inject
  public IdcsService(
    @ConfigProperty(name = "service.idcs") String idcs,
    @ConfigProperty(name = "service.client-id") String clientId,
    @ConfigProperty(name = "service.client-secret") String clientSecret,
    @ConfigProperty(name = "service.apigw.url") String apiGw
  ) {
    this.idcs = idcs;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.apiGw = apiGw;
  }

  //Call IDCS to get authToken for service to service call
  @Traced
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public String getAuthToken() {
    if (log.isDebugEnabled()) {
      log.debug(
        "Calling IDCS {} with client-id {} , client-secret {} and scope {}",
        idcs,
        clientId,
        clientSecret,
        apiGw
      );
    }

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost request = new HttpPost(idcs + "/oauth2/v1/token");
      request.addHeader("content-type", "application/x-www-form-urlencoded");

      String auth = clientId + ":" + clientSecret;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
      String authHeader = "Basic " + encodedAuth;
      request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

      List<NameValuePair> formParams = new ArrayList<>();
      formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
      formParams.add(new BasicNameValuePair("scope", apiGw + "/service"));
      UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(formParams);
      request.setEntity(requestEntity);

      try (CloseableHttpResponse response = httpClient.execute(request)) {
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
          Map<String, Object> responseDetails = JSON_MAPPER.readValue(
            EntityUtils.toString(responseEntity),
            new TypeReference<>() {}
          );
          String token = responseDetails.get("access_token").toString();
          if (log.isDebugEnabled()) log.debug("Got authToken for service-to-service call {}", token);
          return token;
        } else {
          throw new ProcessingException("Error when retrieving auth token, missing response from IDCS");
        }
      }
    } catch (IOException | ParseException e) {
      throw new ProcessingException("Error when retrieving auth token", e);
    }
  }
}
