/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.models.Gender;
import com.oracle.refapp.provider.models.Slot;
import com.oracle.refapp.provider.models.Status;
import java.time.ZonedDateTime;

public class TestUtils {

  public static final Integer TEST_PROVIDER_ID = 1;
  public static final Integer TEST_SCHEDULE_ID = 2;
  public static final Integer TEST_PATIENT_ID = 3;

  public static final String TEST_USER_NAME = "john_doe";
  public static final Integer TEST_LIMIT = 2;
  public static final Integer TEST_PAGE = 0;
  public static final String TEST_SPECIALITY = "Physician";
  public static final String TEST_CITY = "Seattle";
  public static final String TEST_NAME = "john doe";
  public static final Double TEST_AGGREGATE_RATING = 3.5;
  public static final String TEST_START_TIME = "2021-11-28T09:30Z";
  public static final String TEST_END_TIME = "2021-11-28T10:00Z";
  public static final ZonedDateTime TEST_ZONE_START_TIME = ZonedDateTime.parse(TEST_START_TIME);
  public static final ZonedDateTime TEST_ZONE_END_TIME = ZonedDateTime.parse(TEST_END_TIME);
  public static final Slot TEST_SLOT = new Slot().id(2).startTime(TEST_ZONE_START_TIME).endTime(TEST_ZONE_END_TIME);
  public static final String TEST_FEEDBACK_TEXT = "Good";
  public static final Integer TEST_FEEDBACK_RATING = 3;

  public static final ProviderEntity TEST_PROVIDER_ENTITY = buildProviderEntity();
  public static final ScheduleEntity TEST_SCHEDULE_ENTITY = buildScheduleEntity();
  public static final SlotEntity TEST_SLOT_ENTITY = new SlotEntity(
    null,
    TEST_PROVIDER_ENTITY,
    TEST_ZONE_START_TIME,
    TEST_ZONE_END_TIME,
    Status.AVAILABLE
  );
  public static final FeedbackEntity TEST_FEEDBACK_ENTITY = buildFeedbackEntity();

  public static ProviderEntity buildProviderEntity() {
    ProviderEntity providerEntity = new ProviderEntity();
    providerEntity.setId(TEST_PROVIDER_ID);
    providerEntity.setUsername("jndoe");
    providerEntity.setFirstName("John");
    providerEntity.setMiddleName("Miller");
    providerEntity.setLastName("Doe");
    providerEntity.setTitle("Dr");
    providerEntity.setPhone("123");
    providerEntity.setEmail("jndoe@uho.com");
    providerEntity.setGender(Gender.MALE);
    providerEntity.setCity(TEST_CITY);
    providerEntity.setSpeciality(TEST_SPECIALITY);
    providerEntity.setTags("test");
    return providerEntity;
  }

  public static ScheduleEntity buildScheduleEntity() {
    ScheduleEntity scheduleEntity = new ScheduleEntity();
    scheduleEntity.setProvider(TEST_PROVIDER_ENTITY);
    scheduleEntity.setStartTime(TEST_ZONE_START_TIME);
    scheduleEntity.setEndTime(TEST_ZONE_END_TIME);
    return scheduleEntity;
  }

  public static FeedbackEntity buildFeedbackEntity() {
    FeedbackEntity feedbackEntity = new FeedbackEntity();
    feedbackEntity.setProviderId(TEST_PROVIDER_ID);
    feedbackEntity.setPatientId(TEST_PATIENT_ID);
    feedbackEntity.setText(TEST_FEEDBACK_TEXT);
    feedbackEntity.setRating(TEST_FEEDBACK_RATING);
    return feedbackEntity;
  }
}
