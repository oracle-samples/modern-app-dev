/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.model.PutMessagesResultEntry;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AppointmentMessageProducer {

  private final String streamId;

  private StreamClient streamClient;

  private static final Logger LOG = LoggerFactory.getLogger(AppointmentMessageProducer.class);

  public AppointmentMessageProducer(
    Environment environment,
    @Value("${streaming.id}") String streamId,
    @Value("${streaming.endpoint}") String streamEndpoint
  ) throws IOException {
    this.streamId = streamId;
    if (!environment.getActiveNames().contains("test")) {
      if (environment.getActiveNames().contains("dev")) {
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
          configFile
        );
        streamClient = StreamClient.builder().endpoint(streamEndpoint).build(provider);
      } else {
        InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider
          .builder()
          .build();
        streamClient = StreamClient.builder().endpoint(streamEndpoint).build(provider);
      }
    }
    LOG.info("AppointMessageProducer - Stream Endpoint: {}, Id: {}", streamEndpoint, streamId);
  }

  public void sendMessage(String key, String value) {
    LOG.info("Sending message: {} - {}", key, value);
    List<PutMessagesDetailsEntry> messages = new ArrayList<>();
    messages.add(
      PutMessagesDetailsEntry
        .builder()
        .key(key.getBytes(StandardCharsets.UTF_8))
        .value(value.getBytes(StandardCharsets.UTF_8))
        .build()
    );

    PutMessagesDetails messagesDetails = PutMessagesDetails.builder().messages(messages).build();

    PutMessagesRequest putRequest = PutMessagesRequest
      .builder()
      .streamId(streamId)
      .putMessagesDetails(messagesDetails)
      .build();
    LOG.info("Sending on stream id: {}", streamId);
    LOG.info("Data: {}", putRequest);
    PutMessagesResponse putResponse = streamClient.putMessages(putRequest);

    for (PutMessagesResultEntry entry : putResponse.getPutMessagesResult().getEntries()) {
      if (StringUtils.isNotBlank(entry.getError())) {
        LOG.error("Error({}): {}", entry.getError(), entry.getErrorMessage());
      } else {
        LOG.info("Published message to partition {}, offset {}", entry.getPartition(), entry.getOffset());
      }
    }
  }
}
