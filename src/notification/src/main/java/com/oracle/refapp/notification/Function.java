/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.notification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.objectstorage.ObjectStorageEntry;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.oraclecloud.function.OciFunction;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Singleton
public class Function extends OciFunction {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject
  EmailDeliveryService emailDeliveryService;

  @Inject
  ObjectStorageOperations<?, ?, ?> objectStorage;

  @Value("${idcs}")
  private String idcsSecret;

  @Inject
  @Client(id = "idcs")
  private HttpClient idcsClient;

  @Inject
  @Client(id = "apigw")
  private HttpClient apigwClient;

  @ReflectiveAccess
  public String handleRequest(Map<String, String> request) throws Exception {
    System.out.println("Incoming request");
    System.out.println(objectMapper.writeValueAsString(request));

    String encodedMapString = request.get("value");
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
    return "Success!";
  }

  private void feedbackFollowupEmailFlow(Map<String, Object> input) {
    System.out.println("feedbackFollowupEmailFlow entered");
    String emailSubject = input.get("emailSubject").toString();
    String emailBody = input.get("emailBody").toString();
    String patientEmail = input.get("patientEmail").toString();
    emailDeliveryService.sendFeedbackFollowupEmail(emailSubject, emailBody, patientEmail);
  }

  private void appointmentEmailFlow(Map<String, Object> input) {
    System.out.println("appointmentEmailFlow entered");
    String status = (String) input.get("status");
    String startTime = (String) input.get("startTime");
    String endTime = (String) input.get("endTime");
    String patientEmail = (String) input.get("patientEmail");
    String providerEmail = (String) input.get("providerEmail");
    String patientName = (String) input.get("patientName");
    String providerName = (String) input.get("providerName");
    emailDeliveryService.sendAppointmentBookedEmail(
      patientEmail,
      providerEmail,
      patientName,
      providerName,
      startTime,
      endTime,
      status
    );
  }

  private void encounterEmailFlow(Map<String, Object> input) throws Exception {
    System.out.println("encounterEmailFlow entered");

    @SuppressWarnings("unchecked")
    Map<String, Object> dataField = (Map<String, Object>) input.get("data");
    String pdfName = dataField.get("resourceName").toString();
    String patientId = pdfName.split("_")[1].split("\\.")[0];

    System.out.println("pdfName :" + pdfName + ", patientId :" + patientId);

    Optional<ObjectStorageEntry<?>> optionalEntry = objectStorage.retrieve(pdfName);
    if (optionalEntry.isEmpty()) {
      throw new RuntimeException("File not found: " + pdfName);
    }
    ObjectStorageEntry<?> entry = optionalEntry.get();

    System.out.println("Metadata : " + entry.getMetadata());
    Map<String, String> decodedSecret = objectMapper.readValue(idcsSecret, new TypeReference<>() {});
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

    emailDeliveryService.sendEncounterPdfEmail(patientName, patientEmail, pdfName, entry.getInputStream());
  }

  private String getAccessToken(String clientId, String clientSecret) {
    System.out.println("In getAccessToken");
    String apigwUrl = System.getenv().get("APIGW_URL");
    String base64encodedSecret = Base64
      .getEncoder()
      .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

    Map<String, String> data = Map.of("grant_type", "client_credentials", "scope", apigwUrl + "/service");

    HttpRequest<?> request = HttpRequest
      .POST("/oauth2/v1/token", data)
      .contentType(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
      .header("Authorization", "Basic " + base64encodedSecret);
    Map<String, String> response = idcsClient
      .toBlocking()
      .retrieve(request, Argument.mapOf(String.class, String.class));
    return response.get("access_token");
  }

  private Map<String, Object> getPatientDetails(String patientId, String accessToken) {
    System.out.println("In getPatientDetails");
    HttpRequest<?> request = HttpRequest.GET("/v1/patients/" + patientId).bearerAuth(accessToken);
    return apigwClient.toBlocking().retrieve(request, Argument.mapOf(String.class, Object.class));
  }
}
