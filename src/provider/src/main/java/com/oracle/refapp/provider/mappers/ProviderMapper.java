/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.mappers;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.models.CreateProviderDetailsRequest;
import com.oracle.refapp.provider.models.CreateScheduleDetailsRequest;
import com.oracle.refapp.provider.models.Feedback;
import com.oracle.refapp.provider.models.FeedbackSummary;
import com.oracle.refapp.provider.models.Provider;
import com.oracle.refapp.provider.models.Schedule;
import com.oracle.refapp.provider.models.ScheduleSummary;
import com.oracle.refapp.provider.models.Slot;
import com.oracle.refapp.provider.models.SlotSummary;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "jsr330")
public interface ProviderMapper {
  default LocalDate fromDate(Date date) {
    return new java.sql.Date(date.getTime()).toLocalDate();
  }

  @Mapping(target = "tags", source = "tags", qualifiedByName = "mapListToString")
  @Mapping(target = "id", ignore = true)
  ProviderEntity mapCreateProviderDetailsToProviderEntity(CreateProviderDetailsRequest provider);

  @Mapping(target = "tags", source = "tags", qualifiedByName = "mapStringToList")
  CreateProviderDetailsRequest mapProviderEntityToCreateProviderDetails(ProviderEntity providerEntity);

  @Mapping(target = "tags", source = "tags", qualifiedByName = "mapStringToList")
  Provider mapProviderEntityToProvider(ProviderEntity providerEntity);

  @Named("mapListToString")
  default String mapListToString(List<String> tags) {
    if (tags == null) {
      return null;
    }
    return String.join(",", tags);
  }

  @Mapping(target = "provider", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapStringToZonedDateTime")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapStringToZonedDateTime")
  ScheduleEntity mapApiToDomainModels(CreateScheduleDetailsRequest schedule);

  @Mapping(target = "providerId", source = "provider.id")
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapZonedDateTimeToString")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapZonedDateTimeToString")
  Schedule mapDomainToApiModels(ScheduleEntity scheduleEntity);

  @Mapping(target = "providerId", source = "provider.id")
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapZonedDateTimeToString")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapZonedDateTimeToString")
  ScheduleSummary mapDomainToApiModelsForSummary(ScheduleEntity scheduleEntity);

  @Mapping(target = "providerId", source = "provider.id")
  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapZonedDateTimeToString")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapZonedDateTimeToString")
  SlotSummary mapDomainToApiModels(SlotEntity slotEntity);

  @Mapping(target = "providerId", source = "provider.id")
  List<ScheduleSummary> mapScheduleEntityListToScheduleSummaryList(List<ScheduleEntity> scheduleEntity);

  Feedback mapDomainToApiModels(FeedbackEntity feedbackEntity);

  @Mapping(target = "providerId", source = "provider.id")
  List<FeedbackSummary> mapFeedbackEntityListToFeedbackSummaryList(List<FeedbackEntity> feedbackEntities);

  @Mapping(target = "providerId", source = "provider.id")
  List<SlotSummary> mapSlotEntityListToSlotSummaryList(List<SlotEntity> slotEntity);

  @Mapping(target = "startTime", source = "startTime", qualifiedByName = "mapZonedDateTimeToString")
  @Mapping(target = "endTime", source = "endTime", qualifiedByName = "mapZonedDateTimeToString")
  Slot mapSlotEntityToSlot(SlotEntity slotEntity);

  @Mapping(target = "providerId", source = "provider.id")
  List<Schedule> mapDomainToApiListModels(List<ScheduleEntity> scheduleEntities);

  @Named("mapStringToList")
  default List<String> mapStringToList(String tags) {
    if (tags == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(tags.split(",")).map(String::trim).collect(Collectors.toList());
  }

  @Named("mapStringToZonedDateTime")
  default ZonedDateTime mapStringToZonedDateTime(String time) {
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(time);
    return zonedDateTime;
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
