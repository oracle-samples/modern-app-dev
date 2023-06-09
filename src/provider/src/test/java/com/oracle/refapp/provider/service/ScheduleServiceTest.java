/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import static com.oracle.refapp.provider.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.provider.TestUtils.TEST_PAGE;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_SCHEDULE_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_SCHEDULE_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_SLOT_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_END_TIME;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_START_TIME;
import static com.oracle.refapp.provider.TestUtils.buildScheduleEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.domain.repository.ProviderRepository;
import com.oracle.refapp.provider.domain.repository.ScheduleRepository;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.ScheduleAlreadyExistsException;
import com.oracle.refapp.provider.exceptions.ScheduleNotFoundException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.mappers.ProviderMapperImpl;
import com.oracle.refapp.provider.models.ScheduleCollection;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.annotation.MockBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScheduleServiceTest {

  private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
  private final SlotRepository slotRepository = mock(SlotRepository.class);
  private final FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);
  private final ProviderRepository providerRepository = mock(ProviderRepository.class);
  private final ProviderMapper mapper = new ProviderMapperImpl();
  private final ProviderService providerService = new ProviderService(
    providerRepository,
    feedbackRepository,
    slotRepository,
    mapper,
    new Helper()
  );
  private final SlotService slotService = new SlotService(slotRepository, providerService, mapper);
  private final ScheduleService scheduleService = new ScheduleService(
    scheduleRepository,
    slotRepository,
    slotService,
    providerService,
    mapper,
    new Helper()
  );

  @MockBean(ScheduleRepository.class)
  ScheduleRepository mockedScheduleRepository() {
    return mock(ScheduleRepository.class);
  }

  @MockBean(SlotRepository.class)
  SlotRepository mockedSlotRepository() {
    return mock(SlotRepository.class);
  }

  @MockBean(ProviderRepository.class)
  ProviderRepository mockedProviderRepository() {
    return mock(ProviderRepository.class);
  }

  @Test
  @DisplayName("test Get Schedule by schedule id")
  void testGetSchedule() throws ScheduleNotFoundException {
    ScheduleEntity testScheduleEntityWithNonNullId = buildScheduleEntity();
    testScheduleEntityWithNonNullId.setId(TEST_SCHEDULE_ID);
    when(scheduleRepository.findById(TEST_SCHEDULE_ID))
      .thenReturn(Optional.ofNullable(testScheduleEntityWithNonNullId));
    ScheduleEntity schedule = scheduleService.getSchedule(TEST_SCHEDULE_ID);
    assertEquals(schedule.getId(), TEST_SCHEDULE_ID);
    verify(scheduleRepository, times(1)).findById(TEST_SCHEDULE_ID);
    verifyNoMoreInteractions(scheduleRepository);
  }

  @Test
  @DisplayName("test Get Schedule by schedule id exception")
  void testGetScheduleException() {
    when(scheduleRepository.findById(TEST_SCHEDULE_ID)).thenReturn(Optional.empty());
    assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getSchedule(TEST_SCHEDULE_ID));
  }

  @Test
  @DisplayName("test Delete Schedule")
  void testDeleteSchedule() throws ScheduleNotFoundException {
    List<SlotEntity> slotEntityList = new ArrayList<>();
    slotEntityList.add(TEST_SLOT_ENTITY);
    when(scheduleRepository.findById(TEST_SCHEDULE_ID)).thenReturn(Optional.ofNullable(TEST_SCHEDULE_ENTITY));
    when(
      slotRepository.findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        TEST_SCHEDULE_ENTITY.getProvider(),
        TEST_SCHEDULE_ENTITY.getStartTime(),
        TEST_SCHEDULE_ENTITY.getEndTime()
      )
    )
      .thenReturn(slotEntityList);

    scheduleService.deleteSchedule(TEST_SCHEDULE_ID);
    verify(slotRepository, times(1)).deleteAll(slotEntityList);
    verify(scheduleRepository, times(1)).delete(TEST_SCHEDULE_ENTITY);
  }

  @Test
  @DisplayName("test Delete Schedule when schedule does not exist")
  void testDeleteScheduleException() {
    when(scheduleRepository.findById(TEST_SCHEDULE_ID)).thenReturn(Optional.empty());
    assertThrows(ScheduleNotFoundException.class, () -> scheduleService.deleteSchedule(TEST_SCHEDULE_ID));
  }

  @Test
  @DisplayName("test add Schedule")
  void testAddSchedule() throws Exception {
    SlotEntity slotEntity = new SlotEntity(
      null,
      TEST_PROVIDER_ENTITY,
      TEST_ZONE_START_TIME,
      TEST_ZONE_END_TIME,
      Status.AVAILABLE
    );
    List<SlotEntity> slotEntityList = new ArrayList<>();
    slotEntityList.add(slotEntity);
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    when(
      scheduleRepository.findByProviderAndStartTimeAndEndTime(
        TEST_PROVIDER_ENTITY,
        TEST_ZONE_START_TIME,
        TEST_ZONE_END_TIME
      )
    )
      .thenReturn(Optional.empty());
    when(scheduleRepository.save(TEST_SCHEDULE_ENTITY)).thenReturn(TEST_SCHEDULE_ENTITY);

    scheduleService.createSchedule(TEST_SCHEDULE_ENTITY, TEST_PROVIDER_ID);
    verify(slotRepository, times(1)).saveAll(slotEntityList);
    verify(scheduleRepository, times(1)).save(TEST_SCHEDULE_ENTITY);
  }

  @Test
  @DisplayName("test Add Schedule when schedule already exists")
  void testAddScheduleException() {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    when(
      scheduleRepository.findByProviderAndStartTimeAndEndTime(
        TEST_PROVIDER_ENTITY,
        TEST_ZONE_START_TIME,
        TEST_ZONE_END_TIME
      )
    )
      .thenReturn(Optional.ofNullable(TEST_SCHEDULE_ENTITY));
    Assertions.assertThrows(
      ScheduleAlreadyExistsException.class,
      () -> scheduleService.createSchedule(TEST_SCHEDULE_ENTITY, TEST_PROVIDER_ID)
    );
  }

  @Test
  @DisplayName("test Add Schedule when provider is not found")
  void testAddScheduleProviderNotFoundException() {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.empty());

    Assertions.assertThrows(
      ProviderNotFoundException.class,
      () -> scheduleService.createSchedule(TEST_SCHEDULE_ENTITY, TEST_PROVIDER_ID)
    );
  }

  @Test
  @DisplayName("test List Schedules")
  void testListSchedules() throws ProviderNotFoundException {
    ScheduleEntity testScheduleEntityWithNonNullId = buildScheduleEntity();
    testScheduleEntityWithNonNullId.setId(TEST_SCHEDULE_ID);
    Page<ScheduleEntity> page = Page.of(
      List.of(testScheduleEntityWithNonNullId),
      Pageable.from(TEST_PAGE, TEST_LIMIT),
      1
    );
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    when(scheduleRepository.findByProvider(TEST_PROVIDER_ENTITY, Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order))))
      .thenReturn(page);
    ScheduleCollection actualResponse = scheduleService.search(TEST_PROVIDER_ID, TEST_LIMIT, TEST_PAGE);
    assertEquals(1, actualResponse.getItems().size());
    assertEquals(actualResponse.getItems().get(0).getId(), testScheduleEntityWithNonNullId.getId());
    verify(scheduleRepository, times(1))
      .findByProvider(TEST_PROVIDER_ENTITY, Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order)));
    verify(providerRepository, times(1)).findById(TEST_PROVIDER_ID);
    verifyNoMoreInteractions(providerRepository);
  }

  @Test
  @DisplayName("test List Schedule when provider does not exist")
  void testListSchedulesException() {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.empty());
    Assertions.assertThrows(
      ProviderNotFoundException.class,
      () -> scheduleService.search(TEST_PROVIDER_ID, TEST_LIMIT, TEST_PAGE)
    );
  }
}
