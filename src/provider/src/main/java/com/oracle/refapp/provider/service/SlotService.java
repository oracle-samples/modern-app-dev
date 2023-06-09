/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.SlotCollection;
import com.oracle.refapp.provider.models.SlotSummary;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.List;

@Singleton
public class SlotService {

  private final SlotRepository slotRepository;
  private final ProviderService providerService;
  private final ProviderMapper mapper;

  public SlotService(SlotRepository slotRepository, ProviderService providerService, ProviderMapper mapper) {
    this.slotRepository = slotRepository;
    this.providerService = providerService;
    this.mapper = mapper;
  }

  public void saveAllSlots(List<SlotEntity> slotEntities) {
    slotRepository.saveAll(slotEntities);
  }

  public void deleteAllSlots(List<SlotEntity> slotEntities) {
    slotRepository.deleteAll(slotEntities);
  }

  public SlotCollection search(
    Integer providerId,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    Integer limit,
    Integer page
  ) throws ProviderNotFoundException {
    Page<SlotEntity> slotEntityPage;
    Sort.Order order = new Sort.Order("startTime", Sort.Order.Direction.ASC, true);
    ProviderEntity providerEntity = providerService.findProviderById(providerId);
    slotEntityPage =
      slotRepository.findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        providerEntity,
        startTime,
        endTime,
        Pageable.from(page, limit, Sort.of(order))
      );
    List<SlotEntity> slotEntities = slotEntityPage.getContent();
    List<SlotSummary> slotSummaries = mapper.mapSlotEntityListToSlotSummaryList(slotEntities);
    Integer nextPage = (slotEntityPage.getTotalPages() > slotEntityPage.nextPageable().getNumber())
      ? slotEntityPage.nextPageable().getNumber()
      : null;
    return new SlotCollection().items(slotSummaries).nextPage(nextPage);
  }

  public SlotEntity updateSlot(
    Integer providerId,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    String appointmentStatus
  ) throws ProviderNotFoundException {
    ProviderEntity providerEntity = providerService.findProviderById(providerId);
    SlotEntity slotEntity = slotRepository.findByProviderAndStartTimeAndEndTime(providerEntity, startTime, endTime);
    if (appointmentStatus.equals("CONFIRMED")) slotEntity.setStatus(Status.UNAVAILABLE); else slotEntity.setStatus(
      Status.AVAILABLE
    );
    slotRepository.update(slotEntity);
    return slotEntity;
  }
}
