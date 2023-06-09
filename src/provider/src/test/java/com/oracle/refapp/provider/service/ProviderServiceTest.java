/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import static com.oracle.refapp.provider.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.domain.repository.ProviderRepository;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.UsernameAlreadyTakenException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.mappers.ProviderMapperImpl;
import com.oracle.refapp.provider.models.ProviderCollection;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ProviderServiceTest {

  private final ProviderRepository providerRepository = mock(ProviderRepository.class);
  private final FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);
  private final SlotRepository slotRepository = mock(SlotRepository.class);
  private final Helper helper = mock(Helper.class);
  private final ProviderMapper providerMapper = new ProviderMapperImpl();
  private final ProviderService providerService = new ProviderService(
    providerRepository,
    feedbackRepository,
    slotRepository,
    providerMapper,
    helper
  );

  @Test
  @DisplayName("test Get Provider by id")
  void testGetProviderById() throws ProviderNotFoundException {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    ProviderEntity actualResponse = providerService.findProviderById(TEST_PROVIDER_ID);
    assertEquals(actualResponse.getId(), TEST_PROVIDER_ENTITY.getId());
    verify(providerRepository, times(1)).findById(TEST_PROVIDER_ID);
    verifyNoMoreInteractions(providerRepository);
  }

  @Test
  @DisplayName("test Get provider by username")
  void testGetProviderByUsername() throws ProviderNotFoundException {
    when(providerRepository.findByUsername(TEST_USER_NAME)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    ProviderEntity actualResponse = providerService.findProviderByUsername(TEST_USER_NAME);
    assertEquals(actualResponse.getId(), TEST_PROVIDER_ENTITY.getId());
    verify(providerRepository, times(1)).findByUsername(TEST_USER_NAME);
    verifyNoMoreInteractions(providerRepository);
  }

  @Test
  @DisplayName("test Get Provider by id Exception")
  void testGetProviderByIdException() {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.empty());
    Assertions.assertThrows(ProviderNotFoundException.class, () -> providerService.findProviderById(TEST_PROVIDER_ID));
  }

  @Test
  @DisplayName("test Get Provider by username Exception")
  void testGetProviderByUsernameException() {
    when(providerRepository.findByUsername(TEST_USER_NAME)).thenReturn(Optional.empty());
    Assertions.assertThrows(
      ProviderNotFoundException.class,
      () -> providerService.findProviderByUsername(TEST_USER_NAME)
    );
  }

  @Test
  @DisplayName("test create provider")
  void testCreateProvider() throws UsernameAlreadyTakenException {
    when(providerRepository.save(TEST_PROVIDER_ENTITY)).thenReturn(TEST_PROVIDER_ENTITY);
    ProviderEntity actualResponse = providerService.createProvider(TEST_PROVIDER_ENTITY);
    assertEquals(actualResponse.getUsername(), TEST_PROVIDER_ENTITY.getUsername());
    verify(providerRepository, times(1)).save(TEST_PROVIDER_ENTITY);
    verifyNoMoreInteractions(providerRepository);
  }

  @Test
  @DisplayName("test create provider")
  void testCreateProviderException() {
    when(providerRepository.save(TEST_PROVIDER_ENTITY))
      .thenThrow(new DataAccessException("Error executing SQL UPDATE: ORA-00001: Unique Constraint Violated."));
    assertThrows(UsernameAlreadyTakenException.class, () -> providerService.createProvider(TEST_PROVIDER_ENTITY));
  }

  @Test
  @DisplayName("test List Providers endpoint")
  void testListProviders() {
    Page<ProviderEntity> page = Page.of(List.of(TEST_PROVIDER_ENTITY), Pageable.from(TEST_PAGE, TEST_LIMIT), 1);
    SlotEntity testSlotEntity = new SlotEntity(
      2,
      TEST_PROVIDER_ENTITY,
      TEST_ZONE_START_TIME,
      TEST_ZONE_END_TIME,
      Status.AVAILABLE
    );
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("UTC"));
    Mockito.doReturn(currentTime).when(helper).getZonedCurrentTime();
    Mockito.doReturn(currentTime).when(helper).getZonedCurrentTime();
    when(
      providerRepository.filterByQueries(
        TEST_SPECIALITY,
        TEST_CITY,
        TEST_NAME,
        Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order))
      )
    )
      .thenReturn(page);
    when(feedbackRepository.getAggregateRatingByProviderId(TEST_PROVIDER_ID)).thenReturn(TEST_AGGREGATE_RATING);
    when(
      slotRepository.findByProviderAndStatusAndStartTimeGreaterThanEqualsOrderByStartTime(
        TEST_PROVIDER_ENTITY,
        Status.AVAILABLE,
        currentTime
      )
    )
      .thenReturn(Optional.of(testSlotEntity));
    ProviderCollection actualResponse = providerService.search(
      TEST_SPECIALITY,
      TEST_CITY,
      TEST_NAME,
      TEST_LIMIT,
      TEST_PAGE
    );
    assertEquals(1, actualResponse.getItems().size());
    verify(providerRepository, times(1))
      .filterByQueries(TEST_SPECIALITY, TEST_CITY, TEST_NAME, Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order)));
    verifyNoMoreInteractions(providerRepository);
    verify(feedbackRepository, times(1)).getAggregateRatingByProviderId(TEST_PROVIDER_ID);
    verifyNoMoreInteractions(feedbackRepository);
    verify(slotRepository, times(1))
      .findByProviderAndStatusAndStartTimeGreaterThanEqualsOrderByStartTime(
        TEST_PROVIDER_ENTITY,
        Status.AVAILABLE,
        currentTime
      );
    verifyNoMoreInteractions(slotRepository);
    assertEquals(TEST_AGGREGATE_RATING, actualResponse.getItems().get(0).getAggregateRating());
    assertEquals(TEST_SLOT, actualResponse.getItems().get(0).getEarliestAvailableSlot());
  }
}
