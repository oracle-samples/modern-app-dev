/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.domain.repository.ProviderRepository;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.UsernameAlreadyTakenException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.*;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProviderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProviderService.class);

  private final ProviderRepository providerRepository;
  private final FeedbackRepository feedbackRepository;
  private final SlotRepository slotRepository;
  private final ProviderMapper mapper;
  private final Helper helper;

  public ProviderService(
    ProviderRepository providerRepository,
    FeedbackRepository feedbackRepository,
    SlotRepository slotRepository,
    ProviderMapper mapper,
    Helper helper
  ) {
    this.providerRepository = providerRepository;
    this.feedbackRepository = feedbackRepository;
    this.slotRepository = slotRepository;
    this.mapper = mapper;
    this.helper = helper;
  }

  @Transactional
  public ProviderEntity findProviderById(Integer id) throws ProviderNotFoundException {
    return providerRepository.findById(id).orElseThrow(() -> new ProviderNotFoundException("Provider Id : " + id));
  }

  @Transactional
  public ProviderEntity findProviderByUsername(String username) throws ProviderNotFoundException {
    return providerRepository
      .findByUsername(username)
      .orElseThrow(() -> new ProviderNotFoundException("Username : " + username));
  }

  @Transactional
  public void deleteProvider(Integer id) throws ProviderNotFoundException {
    ProviderEntity provider = providerRepository
      .findById(id)
      .orElseThrow(() -> new ProviderNotFoundException("Provider Id : " + id));
    providerRepository.delete(provider);
  }

  @Transactional
  public ProviderCollection search(String speciality, String city, String name, Integer limit, Integer page) {
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    Page<ProviderEntity> providerEntityPage = providerRepository.filterByQueries(
      speciality == null ? null : speciality,
      city == null ? null : city,
      name,
      Pageable.from(page, limit, Sort.of(order))
    );
    List<ProviderEntity> providers = providerEntityPage.getContent();
    List<ProviderSummary> providerSummaries = new ArrayList<>();
    for (ProviderEntity provider : providers) {
      Double aggRating = feedbackRepository.getAggregateRatingByProviderId(provider.getId());
      Optional<SlotEntity> earliestAvailableSlot =
        slotRepository.findByProviderAndStatusAndStartTimeGreaterThanEqualsOrderByStartTime(
          provider,
          Status.AVAILABLE,
          helper.getZonedCurrentTime()
        );
      providerSummaries.add(
        createNewProviderSummaryFromProvider(
          mapper.mapProviderEntityToProvider(provider),
          aggRating,
          earliestAvailableSlot.map(mapper::mapSlotEntityToSlot).orElse(null)
        )
      );
    }

    Integer nextPage = (providerEntityPage.getTotalPages() > providerEntityPage.nextPageable().getNumber())
      ? providerEntityPage.nextPageable().getNumber()
      : null;
    return new ProviderCollection().items(providerSummaries).nextPage(nextPage);
  }

  @Transactional
  public ProviderEntity createProvider(ProviderEntity providerEntity) throws UsernameAlreadyTakenException {
    try {
      return providerRepository.save(providerEntity);
    } catch (DataAccessException exception) {
      if (exception.getMessage().contains("ORA-00001")) {
        LOGGER.error("Invalid input: User with this username already exists", exception);
        throw new UsernameAlreadyTakenException("Invalid input: User with this username already exists");
      }
    }
    return null;
  }

  private ProviderSummary createNewProviderSummaryFromProvider(
    Provider provider,
    Double aggregateRating,
    Slot earliestAvailableSlot
  ) {
    return new ProviderSummary()
      .aggregateRating(aggregateRating)
      .city(provider.getCity())
      .country(provider.getCountry())
      .designation(provider.getDesignation())
      .earliestAvailableSlot(earliestAvailableSlot)
      .email(provider.getEmail())
      .expertise(provider.getExpertise())
      .firstName(provider.getFirstName())
      .gender(provider.getGender())
      .hospitalAddress(provider.getHospitalAddress())
      .hospitalName(provider.getHospitalName())
      .hospitalPhone(provider.getPhone())
      .id(provider.getId())
      .interests(provider.getInterests())
      .lastName(provider.getLastName())
      .middleName(provider.getMiddleName())
      .phone(provider.getPhone())
      .professionalSummary(provider.getProfessionalSummary())
      .qualification(provider.getQualification())
      .speciality(provider.getSpeciality())
      .tags(provider.getTags())
      .title(provider.getTitle())
      .username(provider.getUsername())
      .zip(provider.getZip());
  }
}
