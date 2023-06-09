/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleByNameRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleByNameResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import okhttp3.*;

public class EmailFunction {

  private ObjectStorageClient objStoreClient = null;
  private SecretsClient secretsClient = null;
  private final ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider
    .builder()
    .build();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public EmailFunction() {
    try {
      objStoreClient = new ObjectStorageClient(provider);
      secretsClient = new SecretsClient(provider);
    } catch (Exception ex) {
      System.out.println("Failed to instantiate ObjectStorage/Secrets client - " + ex.getMessage());
    }
  }

  public String handleRequest(String request) throws JsonProcessingException {
    System.out.println("handleRequest begin...");
    if (objStoreClient == null || secretsClient == null) {
      System.out.println("There was a problem creating the ObjectStorage/Secrets Client object. Please check logs");
      return "Error!";
    }
    try {
      Map<String, String> streamMessageMap = objectMapper.readValue(request, new TypeReference<>() {});
      String encodedMapString = streamMessageMap.get("value");
      String decodedMapString = new String(Base64.getDecoder().decode(encodedMapString), StandardCharsets.UTF_8);
      Map<String, Object> input = objectMapper.readValue(decodedMapString, new TypeReference<>() {});

      System.out.println(input);
      if (input.containsKey("source") && input.containsKey("data")) {
        encounterEmailFlow(input);
      } else if (
        input.containsKey("messageType") &&
        (input.get("messageType").equals("feedback") || input.get("messageType").equals("followup"))
      ) {
        feedbackFollowupEmailFlow(input);
      } else {
        appointmentEmailFlow(input);
      }
    } catch (Exception e) {
      System.out.println("Error : " + e);
      return "Error!";
    }
    return "Success!";
  }

  private void encounterEmailFlow(Map<String, Object> input) throws Exception {
    System.out.println("encounterEmailFlow entered");
    String nameSpace = System.getenv().get("NAMESPACE");
    String bucketName = System.getenv().get("BUCKET_NAME");
    String vaultId = System.getenv().get("VAULT_ID");
    String secretName = System.getenv().get("SECRET_NAME");

    @SuppressWarnings("unchecked")
    Map<String, Object> dataField = (Map<String, Object>) input.get("data");
    String pdfName = dataField.get("resourceName").toString();
    String patientId = pdfName.split("_")[1].split("\\.")[0];

    System.out.println("pdfName :" + pdfName + ", patientId :" + patientId);

    GetObjectRequest getObjectRequest = GetObjectRequest
      .builder()
      .namespaceName(nameSpace)
      .bucketName(bucketName)
      .objectName(pdfName)
      .build();

    GetObjectResponse getObjectResponse = objStoreClient.getObject(getObjectRequest);
    InputStream pdfFileStream = getObjectResponse.getInputStream();

    GetSecretBundleByNameRequest getSecretBundleByNameRequest = GetSecretBundleByNameRequest
      .builder()
      .vaultId(vaultId)
      .secretName(secretName)
      .build();

    GetSecretBundleByNameResponse getSecretBundleByNameResponse = secretsClient.getSecretBundleByName(
      getSecretBundleByNameRequest
    );

    Base64SecretBundleContentDetails encodedSecret = (Base64SecretBundleContentDetails) getSecretBundleByNameResponse
      .getSecretBundle()
      .getSecretBundleContent();
    Map<String, String> decodedSecret = objectMapper.readValue(
      Base64.getDecoder().decode(encodedSecret.getContent()),
      new TypeReference<>() {}
    );
    String clientId = decodedSecret.get("service-app-client-id");
    String clientSecret = decodedSecret.get("service-app-client-secret");

    System.out.println(clientId + ":" + clientSecret);

    String accessToken = getAccessToken(clientId, clientSecret);

    System.out.println("accessToken : " + accessToken);

    Map<String, Object> patientDetails = getPatientDetails(patientId, accessToken);

    String patientName = (String) patientDetails.get("name");
    String patientEmail = (String) patientDetails.get("email");

    System.out.println("patientName : " + patientName);
    System.out.println("patientEmail : " + patientEmail);

    EmailDeliveryService.sendEncounterPdfEmail(patientName, patientEmail, pdfName, pdfFileStream);
  }

  private void feedbackFollowupEmailFlow(Map<String, Object> input) throws Exception {
    System.out.println("feedbackFollowupEmailFlow entered");
    String emailSubject = input.get("emailSubject").toString();
    String emailBody = input.get("emailBody").toString();
    String patientEmail = input.get("patientEmail").toString();
    EmailDeliveryService.sendFeedbackFollowupEmail(emailSubject, emailBody, patientEmail);
  }

  private void appointmentEmailFlow(Map<String, Object> input) throws Exception {
    System.out.println("appointmentEmailFlow entered");
    String status = (String) input.get("status");
    String startTime = (String) input.get("startTime");
    String endTime = (String) input.get("endTime");
    String patientEmail = (String) input.get("patientEmail");
    String providerEmail = (String) input.get("providerEmail");
    String patientName = (String) input.get("patientName");
    String providerName = (String) input.get("providerName");
    EmailDeliveryService.sendAppointmentBookedEmail(
      patientEmail,
      providerEmail,
      patientName,
      providerName,
      startTime,
      endTime,
      status
    );
  }

  private String getAccessToken(String clientId, String clientSecret) throws Exception {
    System.out.println("In getAccessToken");
    String idcsUrl = System.getenv().get("IDCS_URL");
    String apigwUrl = System.getenv().get("APIGW_URL");
    String base64encodedSecret = Base64
      .getEncoder()
      .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create("grant_type=client_credentials&scope=" + apigwUrl + "/service", mediaType);
    Request request = new Request.Builder()
      .url(idcsUrl + "/oauth2/v1/token")
      .method("POST", body)
      .addHeader("Authorization", "Basic " + base64encodedSecret)
      .addHeader("Content-Type", "application/x-www-form-urlencoded")
      .build();
    Response response = client.newCall(request).execute();
    if (response.isSuccessful()) {
      Map<String, String> responseBodyJson = objectMapper.readValue(
        Objects.requireNonNull(response.body()).string(),
        new TypeReference<>() {}
      );
      response.close();
      return responseBodyJson.get("access_token");
    } else {
      response.close();
      throw new Exception("Error getting accessToken :" + Objects.requireNonNull(response.body()).string());
    }
  }

  private Map<String, Object> getPatientDetails(String patientId, String accessToken) throws Exception {
    System.out.println("In getPatientDetails");
    String apigwUrl = System.getenv().get("APIGW_URL");
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    Request request = new Request.Builder()
      .url(apigwUrl + "/v1/patients/" + patientId)
      .method("GET", null)
      .addHeader("Authorization", "Bearer " + accessToken)
      .build();
    Response response = client.newCall(request).execute();
    if (response.isSuccessful()) {
      Map<String, Object> patientDetails = objectMapper.readValue(
        Objects.requireNonNull(response.body()).string(),
        new TypeReference<>() {}
      );
      response.close();
      return patientDetails;
    } else {
      response.close();
      throw new Exception("Error getting patientDetails :" + Objects.requireNonNull(response.body()).string());
    }
  }
}
