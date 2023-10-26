/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.CreateGroupCursorDetails;
import com.oracle.bmc.streaming.model.Message;
import com.oracle.bmc.streaming.requests.CreateGroupCursorRequest;
import com.oracle.bmc.streaming.requests.GetMessagesRequest;
import com.oracle.bmc.streaming.responses.CreateGroupCursorResponse;
import com.oracle.bmc.streaming.responses.GetMessagesResponse;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.service.SlotService;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AppointmentsConsumer {

  private final SlotService slotService;

  private final Helper helper;

  private final String streamId;

  private StreamClient streamClient;

  private static final String STREAMING_GROUP = "UHO-PROVIDER-GROUP";

  private static final String INSTANCE_NAME = "uho-provider-" + UUID.randomUUID();

  private String groupCursor;

  private static final Logger LOG = LoggerFactory.getLogger(AppointmentsConsumer.class);

  public AppointmentsConsumer(
    SlotService slotService,
    Helper helper,
    Environment environment,
    @Value("${streaming.id}") String streamId,
    @Value("${streaming.endpoint}") String streamEndpoint
  ) throws IOException {
    this.slotService = slotService;
    this.helper = helper;
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
      groupCursor = getCursorByGroup(streamClient, streamId);
    }
  }

  @Scheduled(fixedDelay = "30s")
  public void test() {
    LOG.info("I'm just a test");
  }

  @Scheduled(fixedDelay = "30s")
  public void receive() {
    LOG.info("Receiving messages");
    try {
      GetMessagesRequest getRequest = GetMessagesRequest
        .builder()
        .streamId(streamId)
        .cursor(groupCursor)
        .limit(1)
        .build();
      LOG.info("Group cursor value {}", groupCursor);
      GetMessagesResponse getResponse = streamClient.getMessages(getRequest);

      LOG.info("Read {} messages.", getResponse.getItems().size());
      for (Message message : getResponse.getItems()) {
        try {
          LOG.info(
            "Key {}: Value {}",
            message.getKey() == null ? "Null" : new String(message.getKey(), UTF_8),
            new String(message.getValue(), UTF_8)
          );
          Map<String, String> map = helper.jsonToMap(new String(message.getValue(), UTF_8));
          String startTime = map.get("startTime");
          String endTime = map.get("endTime");
          ZonedDateTime slotStartTime = helper.getFormattedZonedDateTimeFor(startTime);
          ZonedDateTime slotEndTime = helper.getFormattedZonedDateTimeFor(endTime);
          SlotEntity entity = slotService.updateSlot(
            Integer.parseInt(new String(message.getKey(), UTF_8)),
            slotStartTime,
            slotEndTime,
            map.get("status")
          );
          LOG.info("Inserted slot {}", entity);
        } catch (Exception ex) {
          LOG.error("Cannot parse message", ex);
        }
      }
      groupCursor = getResponse.getOpcNextCursor();
    } catch (Exception e) {
      LOG.error("Cannot receive messages", e);
    }
  }

  private static String getCursorByGroup(StreamClient streamClient, String streamId) {
    LOG.info("Creating a cursor for group {}, instance {}.", STREAMING_GROUP, INSTANCE_NAME);

    CreateGroupCursorDetails cursorDetails = CreateGroupCursorDetails
      .builder()
      .groupName(STREAMING_GROUP)
      .instanceName(INSTANCE_NAME)
      .type(CreateGroupCursorDetails.Type.TrimHorizon)
      .commitOnGet(true)
      .build();

    CreateGroupCursorRequest createCursorRequest = CreateGroupCursorRequest
      .builder()
      .streamId(streamId)
      .createGroupCursorDetails(cursorDetails)
      .build();

    CreateGroupCursorResponse groupCursorResponse = streamClient.createGroupCursor(createCursorRequest);
    return groupCursorResponse.getCursor().getValue();
  }
}
