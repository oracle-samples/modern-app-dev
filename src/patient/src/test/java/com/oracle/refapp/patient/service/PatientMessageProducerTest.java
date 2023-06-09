/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.PutMessagesResult;
import com.oracle.bmc.streaming.model.PutMessagesResultEntry;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;
import io.micronaut.context.env.Environment;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class PatientMessageProducerTest {

  private PatientMessageProducer patientMessageProducer;

  private final Environment environment = mock(Environment.class);

  private StreamClient streamClient = mock(StreamClient.class);

  public PatientMessageProducerTest() throws IOException {
    when(environment.getActiveNames()).thenReturn((Collections.singleton("test")));
    this.patientMessageProducer = new PatientMessageProducer(environment, "", "");
  }

  @Test
  public void testSendMessage() throws NoSuchFieldException, IllegalAccessException {
    Field clientField = PatientMessageProducer.class.getDeclaredField("streamClient");
    clientField.setAccessible(true);

    PutMessagesResultEntry putMessagesResultEntry = PutMessagesResultEntry.builder().build();
    PutMessagesResult putMessagesResult = PutMessagesResult
      .builder()
      .entries(Collections.singletonList(putMessagesResultEntry))
      .build();
    PutMessagesResponse putMessagesResponse = PutMessagesResponse
      .builder()
      .putMessagesResult(putMessagesResult)
      .build();
    when(streamClient.putMessages(any())).thenReturn(putMessagesResponse);
    clientField.set(patientMessageProducer, streamClient);

    patientMessageProducer.sendMessage("key", "value");

    verify(streamClient, times(1)).putMessages(any());
  }
}
