/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelperTest {

  private final Helper helper = new Helper();

  @Test
  @DisplayName("test Helper jsonToMap")
  void testHelperJsonToMap() throws IOException {
    String testString = "{\"key1\":\"123\", \"key2\": \"Test String\"}";
    Map<String, String> map = helper.jsonToMap(testString);
    assertEquals("123", map.get("key1"));
    assertEquals("Test String", map.get("key2"));
  }

  @Test
  @DisplayName("test Helper isValidTimestamp")
  void testHelperIsValidTimestamp() {
    ZonedDateTime testTimestampValid1 = ZonedDateTime.parse(
      "2021-11-28T09:30:00.000-0000",
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    );
    ZonedDateTime testTimestampValid2 = ZonedDateTime.parse(
      "2021-11-28T10:00:00.000-0000",
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    );
    ZonedDateTime testTimestampNotValid1 = ZonedDateTime.parse(
      "2021-11-28T09:32:00.000-0000",
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    );
    ZonedDateTime testTimestampNotValid2 = ZonedDateTime.parse(
      "2021-11-28T10:00:05.000-0000",
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    );

    assertTrue(helper.isValidTimeStamp(testTimestampValid1));
    assertTrue(helper.isValidTimeStamp(testTimestampValid2));
    assertFalse(helper.isValidTimeStamp(testTimestampNotValid1));
    assertFalse(helper.isValidTimeStamp(testTimestampNotValid2));
  }
}
