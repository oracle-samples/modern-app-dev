/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.ScheduleRepository;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.InvalidTimestampException;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.ScheduleAlreadyExistsException;
import com.oracle.refapp.provider.exceptions.ScheduleNotFoundException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.ScheduleCollection;
import com.oracle.refapp.provider.models.ScheduleSummary;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ScheduleService {

  private final ScheduleRepository scheduleRepository;
  private final SlotRepository slotRepository;
  private final SlotService slotService;
  private final ProviderService providerService;
  private final ProviderMapper mapper;
  private final Helper helper;

  public ScheduleService(
    ScheduleRepository scheduleRepository,
    SlotRepository slotRepository,
    SlotService slotService,
    ProviderService providerService,
    ProviderMapper mapper,
    Helper helper
  ) {
    this.scheduleRepository = scheduleRepository;
    this.slotRepository = slotRepository;
    this.slotService = slotService;
    this.providerService = providerService;
    this.mapper = mapper;
    this.helper = helper;
  }

  @Transactional
  public ScheduleEntity getSchedule(Integer scheduleId) throws ScheduleNotFoundException {
    return scheduleRepository
      .findById(scheduleId)
      .orElseThrow(() -> new ScheduleNotFoundException("Schedule Id : " + scheduleId));
  }

  @Transactional
  public ScheduleEntity createSchedule(ScheduleEntity schedule, Integer providerId)
    throws ScheduleAlreadyExistsException, InvalidTimestampException, ProviderNotFoundException {
    ProviderEntity providerEntity = providerService.findProviderById(providerId);
    schedule.setProvider(providerEntity);
    ZonedDateTime startTimeStamp = schedule.getStartTime();
    ZonedDateTime endTimeStamp = schedule.getEndTime();

    Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findByProviderAndStartTimeAndEndTime(
      providerEntity,
      startTimeStamp,
      endTimeStamp
    );
    if (scheduleEntity.isPresent()) {
      throw new ScheduleAlreadyExistsException("Schedule already exists");
    }

    List<SlotEntity> slotEntities = new ArrayList<>();
    if (
      helper.isValidTimeStamp(startTimeStamp) &&
      helper.isValidTimeStamp(endTimeStamp) &&
      endTimeStamp.compareTo(startTimeStamp) > 0
    ) {
      while (endTimeStamp.compareTo(startTimeStamp) > 0) {
        SlotEntity slotEntity = new SlotEntity();
        slotEntity.setProvider(schedule.getProvider());
        slotEntity.setStartTime(startTimeStamp);
        startTimeStamp = startTimeStamp.plusMinutes(30);
        slotEntity.setEndTime(startTimeStamp);
        slotEntity.setStatus(Status.AVAILABLE);
        slotEntities.add(slotEntity);
      }
      slotService.saveAllSlots(slotEntities);
      scheduleRepository.save(schedule);
      return schedule;
    }
    throw new InvalidTimestampException("Invalid timestamps");
  }

  @Transactional
  public ScheduleEntity deleteSchedule(Integer scheduleId) throws ScheduleNotFoundException {
    Optional<ScheduleEntity> scheduleEntity = scheduleRepository.findById(scheduleId);
    if (scheduleEntity.isEmpty()) {
      throw new ScheduleNotFoundException("Schedule does not exist");
    }
    List<SlotEntity> slotEntities = slotRepository.findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
      scheduleEntity.get().getProvider(),
      scheduleEntity.get().getStartTime(),
      scheduleEntity.get().getEndTime()
    );
    slotService.deleteAllSlots(slotEntities);
    scheduleRepository.delete(scheduleEntity.get());
    return scheduleEntity.get();
  }

  @Transactional
  public ScheduleCollection search(Integer providerId, Integer limit, Integer page) throws ProviderNotFoundException {
    Page<ScheduleEntity> scheduleEntityPage;
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    ProviderEntity providerEntity = providerService.findProviderById(providerId);
    scheduleEntityPage = scheduleRepository.findByProvider(providerEntity, Pageable.from(page, limit, Sort.of(order)));

    List<ScheduleEntity> scheduleEntities = scheduleEntityPage.getContent();
    List<ScheduleSummary> scheduleSummaries = mapper.mapScheduleEntityListToScheduleSummaryList(scheduleEntities);

    Integer nextPage = (scheduleEntityPage.getTotalPages() > scheduleEntityPage.nextPageable().getNumber())
      ? scheduleEntityPage.nextPageable().getNumber()
      : null;
    return new ScheduleCollection().items(scheduleSummaries).nextPage(nextPage);
  }
}
