/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Singleton
public class Helper {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public Map<String, String> jsonToMap(String t) throws IOException {
    return OBJECT_MAPPER.readValue(t, new TypeReference<>() {});
  }

  public boolean isValidTimeStamp(ZonedDateTime timestamp) {
    LocalDateTime localDateTime = timestamp.toLocalDateTime();
    return (localDateTime.getMinute() == 30 || localDateTime.getMinute() == 0) && localDateTime.getSecond() == 0;
  }

  public ZonedDateTime getZonedCurrentTime() {
    return ZonedDateTime.now(ZoneId.of("UTC"));
  }

  public ZonedDateTime getFormattedZonedDateTimeFor(String date) {
    return ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
  }
}
