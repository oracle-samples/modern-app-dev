/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.service;

import static com.oracle.refapp.provider.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.provider.TestUtils.TEST_PAGE;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_SLOT_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_END_TIME;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_START_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.domain.repository.FeedbackRepository;
import com.oracle.refapp.provider.domain.repository.ProviderRepository;
import com.oracle.refapp.provider.domain.repository.SlotRepository;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.helpers.Helper;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.mappers.ProviderMapperImpl;
import com.oracle.refapp.provider.models.SlotCollection;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.test.annotation.MockBean;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SlotServiceTest {

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

  @MockBean(SlotRepository.class)
  SlotRepository mockedSlotRepository() {
    return mock(SlotRepository.class);
  }

  @MockBean(ProviderRepository.class)
  ProviderRepository mockedProviderRepository() {
    return mock(ProviderRepository.class);
  }

  @Test
  @DisplayName("test List Slots")
  void testListSlots() throws ProviderNotFoundException {
    Sort.Order order = new Sort.Order("startTime", Sort.Order.Direction.ASC, true);
    Pageable pageable = Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order));
    Page<SlotEntity> page = Page.of(List.of(TEST_SLOT_ENTITY), Pageable.from(TEST_PAGE, TEST_LIMIT), 1);
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    when(
      slotRepository.findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        TEST_PROVIDER_ENTITY,
        TEST_ZONE_START_TIME,
        TEST_ZONE_END_TIME,
        pageable
      )
    )
      .thenReturn(page);

    SlotCollection actualResponse = slotService.search(
      TEST_PROVIDER_ID,
      TEST_ZONE_START_TIME,
      TEST_ZONE_END_TIME,
      TEST_LIMIT,
      TEST_PAGE
    );

    assertEquals(actualResponse.getItems().size(), page.getContent().size());
    verify(slotRepository, times(1))
      .findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        TEST_PROVIDER_ENTITY,
        TEST_ZONE_START_TIME,
        TEST_ZONE_END_TIME,
        pageable
      );
    verifyNoMoreInteractions(slotRepository);
  }

  @Test
  @DisplayName("test List Slots when provider is not found")
  void testListSlotsException() {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.empty());
    Assertions.assertThrows(
      ProviderNotFoundException.class,
      () -> slotService.search(TEST_PROVIDER_ID, TEST_ZONE_START_TIME, TEST_ZONE_END_TIME, TEST_LIMIT, TEST_PAGE)
    );
  }

  @Test
  @DisplayName("test Update Slots")
  void testUpdateSlots() throws ProviderNotFoundException {
    when(providerRepository.findById(TEST_PROVIDER_ID)).thenReturn(Optional.ofNullable(TEST_PROVIDER_ENTITY));
    when(
      slotRepository.findByProviderAndStartTimeAndEndTime(
        TEST_PROVIDER_ENTITY,
        TEST_ZONE_START_TIME,
        TEST_ZONE_END_TIME
      )
    )
      .thenReturn(TEST_SLOT_ENTITY);

    SlotEntity actualResponse = slotService.updateSlot(
      TEST_PROVIDER_ID,
      TEST_ZONE_START_TIME,
      TEST_ZONE_END_TIME,
      "CONFIRMED"
    );

    assertEquals(Status.UNAVAILABLE, actualResponse.getStatus());
    verify(slotRepository, times(1))
      .findByProviderAndStartTimeAndEndTime(TEST_PROVIDER_ENTITY, TEST_ZONE_START_TIME, TEST_ZONE_END_TIME);
    verify(slotRepository, times(1)).update(TEST_SLOT_ENTITY);
    verifyNoMoreInteractions(slotRepository);
  }
}
