/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.mock.MockConnector;
import com.oracle.refapp.mock.MockEncounterService;
import com.oracle.refapp.mock.MockIdcsService;
import com.oracle.refapp.mock.MockPatientService;
import com.oracle.refapp.mock.MockProviderService;
import com.oracle.refapp.mock.MockTelemetryService;
import io.helidon.microprofile.tests.junit5.AddBean;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@HelidonTest
@AddBean(MockConnector.class)
@AddBean(MockEncounterService.class)
@AddBean(MockIdcsService.class)
@AddBean(MockPatientService.class)
@AddBean(MockProviderService.class)
@AddBean(MockTelemetryService.class)
@Configuration(profile = "test")
class FeedbackServiceTest {

  @Inject
  @MockConnector.Channel("from-stream")
  SubmissionPublisher<Message> fromStreamEmitter;

  @Inject
  @MockConnector.Channel("to-stream")
  List<Message> toStreamList;

  @Test
  void testProcessMessageFromEncounterStream() throws InterruptedException, IOException {
    CountDownLatch latch = new CountDownLatch(1);
    String msg =
      "{\n" +
      "  \"eventType\": \"com.oraclecloud.objectstorage.createobject\",\n" +
      "  \"cloudEventsVersion\": \"0.1\",\n" +
      "  \"eventTypeVersion\": \"2.0\",\n" +
      "  \"source\": \"ObjectStorage\",\n" +
      "  \"eventTime\": \"2022-02-02T14:33:42Z\",\n" +
      "  \"contentType\": \"application/json\",\n" +
      "  \"data\": {\n" +
      "    \"compartmentId\": \"ocid1.compartment.oc1..test\",\n" +
      "    \"compartmentName\": \"cmpt-test\",\n" +
      "    \"resourceName\": \"0f96e39a-0d12-4339-ae5a-163f8b823a82_1.pdf\",\n" +
      "    \"resourceId\": \"/n/ns/b/UHO-bucket-test/o/t10E1234_1.pdf\",\n" +
      "    \"availabilityDomain\": \"SYD-AD-1\",\n" +
      "    \"additionalDetails\": {\n" +
      "      \"bucketName\": \"HO-bucket-test\",\n" +
      "      \"versionId\": \"670c7cf5-3362-4978-b2d4-4180138dd390\",\n" +
      "      \"archivalState\": \"Available\",\n" +
      "      \"namespace\": \"frvznqwcwyvv\",\n" +
      "      \"bucketId\": \"ocid1.bucket.oc1.ap-sydney-1.test\",\n" +
      "      \"eTag\": \"4609f631-8759-47ce-8def-14cfbec57a50\"\n" +
      "    }\n" +
      "  }\n" +
      "}";
    fromStreamEmitter.submit(
      Message.of(
        msg,
        () -> {
          latch.countDown();
          return CompletableFuture.completedStage(null);
        }
      )
    );
    Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    Assertions.assertEquals(1, toStreamList.size());
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> feedbackMessageMap = objectMapper.readValue(
      toStreamList.get(0).getPayload().toString(),
      new TypeReference<>() {}
    );
    Assertions.assertTrue(feedbackMessageMap.containsKey("messageType"));
    Assertions.assertTrue(feedbackMessageMap.containsKey("messageType"));
    Assertions.assertEquals("feedback", feedbackMessageMap.get("messageType").toString());
    Assertions.assertEquals("patient@uho.com", feedbackMessageMap.get("patientEmail").toString());
    Assertions.assertEquals("UHO Patient", feedbackMessageMap.get("patientName").toString());
    Assertions.assertEquals("UHO Provider", feedbackMessageMap.get("providerName").toString());
  }
}
