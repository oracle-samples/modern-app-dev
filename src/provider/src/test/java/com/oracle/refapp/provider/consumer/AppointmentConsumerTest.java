/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.Message;
import com.oracle.bmc.streaming.responses.CreateGroupCursorResponse;
import com.oracle.bmc.streaming.responses.GetMessagesResponse;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.service.SlotService;
import io.micronaut.context.env.Environment;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class AppointmentConsumerTest {

  private final SlotService mockedSlotService = mock(SlotService.class);
  private final Environment environment = mock(Environment.class);
  private AppointmentsConsumer consumer;
  private final ArgumentCaptor<Integer> keyArg = ArgumentCaptor.forClass(Integer.class);
  private final ArgumentCaptor<String> statusArg = ArgumentCaptor.forClass(String.class);
  private final ArgumentCaptor<ZonedDateTime> startTimeArg = ArgumentCaptor.forClass(ZonedDateTime.class);
  private final ArgumentCaptor<ZonedDateTime> endTimeArg = ArgumentCaptor.forClass(ZonedDateTime.class);
  private final StreamClient streamClient = mock(StreamClient.class);

  @BeforeEach
  void before() throws IOException {
    when(environment.getActiveNames()).thenReturn((Collections.singleton("test")));

    consumer =
      new AppointmentsConsumer(
        mockedSlotService,
        new Helper(),
        environment,
        "ocid1.stream.oc1.ap-melbourne-1.amaaaaaa4jyktxia3masavsa6vr5smac4qjyls5i6zugnpc5rdxe7y7miasq",
        "https://cell-1.streaming.ap-melbourne-1.oci.oraclecloud.com"
      );
  }

  @Test
  public void testMessageProcessedSuccess()
    throws ProviderNotFoundException, NoSuchFieldException, IllegalAccessException, IOException {
    DateTimeFormatter testDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    String testStartDate = testDatePattern.format(LocalDate.of(2022, 3, 7).atStartOfDay(ZoneId.of("UTC-3")));
    String testEndDate = testDatePattern.format(LocalDate.of(2022, 3, 7).atStartOfDay(ZoneId.of("UTC-3")).plusHours(2));
    String message =
      "{\"startTime\":\"" + testStartDate + "\",\"endTime\":\"" + testEndDate + "\" , \"status\":\"CONFIRMED\"}";

    Field clientField = AppointmentsConsumer.class.getDeclaredField("streamClient");
    clientField.setAccessible(true);
    GetMessagesResponse getMessagesResponse = GetMessagesResponse
      .builder()
      .items(
        Collections.singletonList(
          Message
            .builder()
            .key("1".getBytes(StandardCharsets.UTF_8))
            .value(message.getBytes(StandardCharsets.UTF_8))
            .build()
        )
      )
      .build();

    when(streamClient.createGroupCursor(any())).thenReturn(CreateGroupCursorResponse.builder().build());
    when(streamClient.getMessages(any())).thenReturn(getMessagesResponse);
    clientField.set(consumer, streamClient);
    consumer.receive();
    verify(mockedSlotService, times(1))
      .updateSlot(keyArg.capture(), startTimeArg.capture(), endTimeArg.capture(), statusArg.capture());
    assertEquals(1, keyArg.getValue());
    assertEquals(2022, startTimeArg.getValue().getYear());
    assertEquals(3, startTimeArg.getValue().getMonth().getValue());
    assertEquals(7, startTimeArg.getValue().getDayOfMonth());
    assertEquals(0, startTimeArg.getValue().getHour());
    assertEquals(2022, endTimeArg.getValue().getYear());
    assertEquals(3, endTimeArg.getValue().getMonth().getValue());
    assertEquals(7, endTimeArg.getValue().getDayOfMonth());
    assertEquals(2, endTimeArg.getValue().getHour());
    assertEquals("CONFIRMED", statusArg.getValue());
  }
}
