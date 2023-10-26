/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.model.Appointment;
import com.oracle.refapp.model.AppointmentSummary;
import com.oracle.refapp.model.CreateAppointmentRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "jsr330")
public interface AppointmentMapper {
  default LocalDate fromDate(Date date) {
    return new java.sql.Date(date.getTime()).toLocalDate();
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(source = "preVisitData", target = "preVisitData", qualifiedByName = "fromMapToJson")
  @Mapping(target = "uniqueString", ignore = true)
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapStringToZonedDateTime")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapStringToZonedDateTime")
  AppointmentEntity mapApiToDomainModels(CreateAppointmentRequest appointmentRequest);

  @Mapping(source = "preVisitData", target = "preVisitData", qualifiedByName = "fromJsonToMap")
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapZonedDateTimeToString")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapZonedDateTimeToString")
  Appointment mapDomainToApiModels(AppointmentEntity appointmentEntity);

  AppointmentSummary mapToSummary(Appointment appointment);

  List<AppointmentSummary> mapDomainToApiModels(List<AppointmentEntity> appointmentEntity);

  @Named("fromJsonToMap")
  default Map<String, String> fromJsonToMap(String storyInfo) throws IOException {
    if (Objects.nonNull(storyInfo)) {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(storyInfo, new TypeReference<>() {});
    }
    return Collections.emptyMap();
  }

  @Named("fromMapToJson")
  default String fromMapToJson(Map<String, String> storyInfo) throws JsonProcessingException {
    if (Objects.nonNull(storyInfo)) {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(storyInfo);
    }
    return null;
  }

  @Named("mapStringToZonedDateTime")
  default ZonedDateTime mapStringToZonedDateTime(String time) {
    return ZonedDateTime.parse(time);
  }

  @Named("mapZonedDateTimeToString")
  default String mapZonedDateTimeToString(ZonedDateTime zonedDateTime) {
    ZonedDateTime utcDateTime = ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of("UTC"));
    String time = utcDateTime.toString();
    int index = time.indexOf("[");
    if (index > 0) time = time.substring(0, index);
    return time;
  }
}
