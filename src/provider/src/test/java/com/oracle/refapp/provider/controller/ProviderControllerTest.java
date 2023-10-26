/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.controller;

import static com.oracle.refapp.provider.TestUtils.TEST_CITY;
import static com.oracle.refapp.provider.TestUtils.TEST_END_TIME;
import static com.oracle.refapp.provider.TestUtils.TEST_FEEDBACK_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_FEEDBACK_RATING;
import static com.oracle.refapp.provider.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.provider.TestUtils.TEST_NAME;
import static com.oracle.refapp.provider.TestUtils.TEST_PAGE;
import static com.oracle.refapp.provider.TestUtils.TEST_PATIENT_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_PROVIDER_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_SCHEDULE_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_SCHEDULE_ID;
import static com.oracle.refapp.provider.TestUtils.TEST_SLOT;
import static com.oracle.refapp.provider.TestUtils.TEST_SLOT_ENTITY;
import static com.oracle.refapp.provider.TestUtils.TEST_SPECIALITY;
import static com.oracle.refapp.provider.TestUtils.TEST_START_TIME;
import static com.oracle.refapp.provider.TestUtils.TEST_USER_NAME;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_END_TIME;
import static com.oracle.refapp.provider.TestUtils.TEST_ZONE_START_TIME;
import static com.oracle.refapp.provider.TestUtils.buildProviderEntity;
import static com.oracle.refapp.provider.TestUtils.buildScheduleEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.ScheduleAlreadyExistsException;
import com.oracle.refapp.provider.exceptions.ScheduleNotFoundException;
import com.oracle.refapp.provider.exceptions.UsernameAlreadyTakenException;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.CreateFeedbackDetailsRequest;
import com.oracle.refapp.provider.models.CreateProviderDetailsRequest;
import com.oracle.refapp.provider.models.CreateScheduleDetailsRequest;
import com.oracle.refapp.provider.models.FeedbackCollection;
import com.oracle.refapp.provider.models.Gender;
import com.oracle.refapp.provider.models.Provider;
import com.oracle.refapp.provider.models.ProviderCollection;
import com.oracle.refapp.provider.models.ProviderSummary;
import com.oracle.refapp.provider.models.Schedule;
import com.oracle.refapp.provider.models.ScheduleCollection;
import com.oracle.refapp.provider.models.Slot;
import com.oracle.refapp.provider.models.SlotCollection;
import com.oracle.refapp.provider.service.FeedbackService;
import com.oracle.refapp.provider.service.ProviderService;
import com.oracle.refapp.provider.service.ScheduleService;
import com.oracle.refapp.provider.service.SlotService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@MicronautTest
class ProviderControllerTest {

  private static final Provider TEST_PROVIDER = buildProvider();

  @Inject
  @Client("/")
  HttpClient client;

  @Inject
  ProviderService providerService;

  @Inject
  ScheduleService scheduleService;

  @Inject
  SlotService slotService;

  @Inject
  FeedbackService feedbackService;

  @Inject
  ProviderMapper mapper;

  @MockBean(ProviderService.class)
  ProviderService mockedProviderService() {
    return mock(ProviderService.class);
  }

  @MockBean(ScheduleService.class)
  ScheduleService mockedScheduleService() {
    return mock(ScheduleService.class);
  }

  @MockBean(SlotService.class)
  SlotService mockedSlotService() {
    return mock(SlotService.class);
  }

  @MockBean(FeedbackService.class)
  FeedbackService mockedFeedbackService() {
    return mock(FeedbackService.class);
  }

  @Test
  @DisplayName("test Get Provider by id")
  void testGetProviderById() throws ProviderNotFoundException {
    when(providerService.findProviderById(TEST_PROVIDER_ID)).thenReturn(TEST_PROVIDER_ENTITY);
    HttpRequest<Object> request = HttpRequest.GET("v1/providers/" + TEST_PROVIDER_ID);
    HttpResponse<ProviderEntity> response = client.toBlocking().exchange(request, ProviderEntity.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Get Provider when provider does not exist ")
  void testGetProviderByIdException() throws ProviderNotFoundException {
    when(providerService.findProviderById(TEST_PROVIDER_ID)).thenThrow(ProviderNotFoundException.class);
    HttpRequest<Object> request = HttpRequest.GET("v1/providers/" + TEST_PROVIDER_ID);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, ProviderEntity.class));
  }

  @Test
  @DisplayName("test Get Provider by id")
  void testGetProviderByUsername() throws ProviderNotFoundException {
    when(providerService.findProviderByUsername(TEST_USER_NAME)).thenReturn(TEST_PROVIDER_ENTITY);
    HttpRequest<Object> request = HttpRequest.GET("v1/providers/username/" + TEST_USER_NAME);
    HttpResponse<ProviderEntity> response = client.toBlocking().exchange(request, ProviderEntity.class);

    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Get Provider when provider does not exist ")
  void testGetProviderByUsernameException() throws ProviderNotFoundException {
    when(providerService.findProviderByUsername(TEST_USER_NAME)).thenThrow(ProviderNotFoundException.class);
    HttpRequest<Object> request = HttpRequest.GET("v1/providers/username/" + TEST_USER_NAME);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request, ProviderEntity.class));
  }

  @Test
  @DisplayName("test Create Provider endpoint")
  void testCreateProvider() throws UsernameAlreadyTakenException {
    ProviderEntity testEntityWithNullId = buildProviderEntity();
    testEntityWithNullId.setId(null);
    CreateProviderDetailsRequest createProviderDetailsRequest = mapper.mapProviderEntityToCreateProviderDetails(
      TEST_PROVIDER_ENTITY
    );
    when(providerService.createProvider(testEntityWithNullId)).thenReturn(TEST_PROVIDER_ENTITY);
    HttpRequest<CreateProviderDetailsRequest> request = HttpRequest.POST("v1/providers/", createProviderDetailsRequest);
    HttpResponse<Provider> response = client.toBlocking().exchange(request, Provider.class);
    assertEquals(HttpStatus.OK, response.status());
    verify(providerService, times(1)).createProvider(testEntityWithNullId);
  }

  @Test
  @DisplayName("test Create Provider endpoint username taken exception")
  void testCreateProviderUserNameTakenException() throws UsernameAlreadyTakenException {
    ProviderEntity testEntityWithNullId = buildProviderEntity();
    testEntityWithNullId.setId(null);
    CreateProviderDetailsRequest createProviderDetailsRequest = mapper.mapProviderEntityToCreateProviderDetails(
      TEST_PROVIDER_ENTITY
    );
    when(providerService.createProvider(testEntityWithNullId)).thenThrow(UsernameAlreadyTakenException.class);
    HttpRequest<CreateProviderDetailsRequest> request = HttpRequest.POST("v1/providers/", createProviderDetailsRequest);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Provider.class));
  }

  @Test
  @DisplayName("test Create Provider endpoint username taken exception")
  void testCreateProviderOtherExceptions() throws UsernameAlreadyTakenException {
    ProviderEntity testEntityWithNullId = buildProviderEntity();
    testEntityWithNullId.setId(null);
    CreateProviderDetailsRequest createProviderDetailsRequest = mapper.mapProviderEntityToCreateProviderDetails(
      TEST_PROVIDER_ENTITY
    );
    when(providerService.createProvider(testEntityWithNullId)).thenThrow(RuntimeException.class);
    HttpRequest<CreateProviderDetailsRequest> request = HttpRequest.POST("v1/providers/", createProviderDetailsRequest);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Provider.class));
  }

  @Test
  @DisplayName("test Delete Provider by id")
  void testDeleteProvider() throws ProviderNotFoundException {
    doNothing().when(providerService).deleteProvider(TEST_PROVIDER_ID);

    HttpRequest<Object> request = HttpRequest.DELETE("v1/providers/" + TEST_PROVIDER_ID);
    HttpResponse<Object> response = client.toBlocking().exchange(request, Object.class);

    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Delete Provider by id exception")
  void testDeleteProviderException() throws ProviderNotFoundException {
    doThrow(ProviderNotFoundException.class).when((providerService)).deleteProvider(TEST_PROVIDER_ID);

    HttpRequest<Object> request = HttpRequest.DELETE("v1/providers/" + TEST_PROVIDER_ID);
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request));
  }

  @Test
  @DisplayName("test List Providers endpoint")
  void testListProviders() {
    List<ProviderSummary> providerEntityList = List.of(
      createNewProviderSummaryFromProvider(TEST_PROVIDER, 5.0, TEST_SLOT)
    );
    ProviderCollection providerCollection = new ProviderCollection().items(providerEntityList);
    when(providerService.search(TEST_SPECIALITY, TEST_CITY, TEST_NAME, TEST_LIMIT, TEST_PAGE))
      .thenReturn(providerCollection);
    URI requestUri = UriBuilder
      .of("v1/providers/actions/search/")
      .queryParam("speciality", TEST_SPECIALITY)
      .queryParam("city", TEST_CITY)
      .queryParam("name", TEST_NAME)
      .queryParam("page", TEST_PAGE)
      .queryParam("limit", TEST_LIMIT)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(requestUri);
    HttpResponse<ProviderCollection> response = client.toBlocking().exchange(request, ProviderCollection.class);

    assertEquals(HttpStatus.OK, response.status());
    assertEquals(1, response.getBody().get().getItems().size());
  }

  @Test
  void testListProvidersWithWhitespace() {
    List<ProviderSummary> providerEntityList = List.of(
      createNewProviderSummaryFromProvider(TEST_PROVIDER, 5.0, TEST_SLOT)
    );
    String city = "San Francisco";
    ProviderCollection providerCollection = new ProviderCollection().items(providerEntityList);
    when(providerService.search(TEST_SPECIALITY, city, TEST_NAME, TEST_LIMIT, TEST_PAGE))
      .thenReturn(providerCollection);
    URI requestUri = UriBuilder
      .of("v1/providers/actions/search/")
      .queryParam("speciality", TEST_SPECIALITY)
      .queryParam("city", city)
      .queryParam("name", TEST_NAME)
      .queryParam("page", TEST_PAGE)
      .queryParam("limit", TEST_LIMIT)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(requestUri);
    HttpResponse<ProviderCollection> response = client.toBlocking().exchange(request, ProviderCollection.class);

    assertEquals(HttpStatus.OK, response.status());
    assertEquals(1, response.getBody().get().getItems().size());
  }

  @Test
  @DisplayName("test Delete Schedule")
  void testDeleteSchedule() throws ScheduleNotFoundException {
    when(scheduleService.deleteSchedule(TEST_SCHEDULE_ID)).thenReturn(TEST_SCHEDULE_ENTITY);
    HttpRequest<Object> request = HttpRequest.DELETE(
      "v1/providers/" + TEST_PROVIDER_ID + "/schedules/" + TEST_SCHEDULE_ID
    );
    HttpResponse<Schedule> response = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test Delete Schedule when schedule does not exist")
  void testDeleteScheduleException() throws ScheduleNotFoundException {
    when(scheduleService.deleteSchedule(TEST_SCHEDULE_ID)).thenThrow(ScheduleNotFoundException.class);
    HttpRequest<Object> request = HttpRequest.DELETE(
      "v1/providers/" + TEST_PROVIDER_ID + "/schedules/" + TEST_SCHEDULE_ID
    );
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request));
  }

  @Test
  @DisplayName("test Create Schedule")
  void testCreateSchedule() throws Exception {
    ScheduleEntity testScheduleEntityWithNullIds = new ScheduleEntity();
    testScheduleEntityWithNullIds.setId(null);
    testScheduleEntityWithNullIds.setProvider(null);
    testScheduleEntityWithNullIds.setStartTime(TEST_ZONE_START_TIME); //.withZoneSameInstant(ZoneId.of("UTC")));
    testScheduleEntityWithNullIds.setEndTime(TEST_ZONE_END_TIME); //.withZoneSameInstant(ZoneId.of("UTC")));
    when(scheduleService.createSchedule(testScheduleEntityWithNullIds, TEST_PROVIDER_ID))
      .thenReturn(TEST_SCHEDULE_ENTITY);
    CreateScheduleDetailsRequest createScheduleDetailsRequest = new CreateScheduleDetailsRequest();
    createScheduleDetailsRequest.setStartTime(TEST_ZONE_START_TIME);
    createScheduleDetailsRequest.setEndTime(TEST_ZONE_END_TIME);
    HttpRequest<CreateScheduleDetailsRequest> request = HttpRequest.POST(
      "v1/providers/" + TEST_PROVIDER_ID + "/schedules/",
      createScheduleDetailsRequest
    );
    HttpResponse<Schedule> response = client.toBlocking().exchange(request, Schedule.class);
    assertNotNull(response.getBody().get().getProviderId());
    verify(scheduleService, times(1)).createSchedule(testScheduleEntityWithNullIds, TEST_PROVIDER_ID);
  }

  @Test
  @DisplayName("test Create Schedule when schedule already exists")
  void testCreateScheduleException() throws Exception {
    ScheduleEntity testScheduleEntityWithNullIds = buildScheduleEntity();
    testScheduleEntityWithNullIds.setId(null);
    when(scheduleService.createSchedule(testScheduleEntityWithNullIds, TEST_PROVIDER_ID))
      .thenThrow(ScheduleAlreadyExistsException.class);
    CreateScheduleDetailsRequest createScheduleDetailsRequest = new CreateScheduleDetailsRequest();
    createScheduleDetailsRequest.setStartTime(TEST_ZONE_START_TIME);
    createScheduleDetailsRequest.setEndTime(TEST_ZONE_END_TIME);
    HttpRequest<CreateScheduleDetailsRequest> request = HttpRequest.POST(
      "v1/providers/schedules/",
      createScheduleDetailsRequest
    );
    BlockingHttpClient blockingHttpClient = client.toBlocking();
    assertThrows(HttpClientResponseException.class, () -> blockingHttpClient.exchange(request));
  }

  @Test
  @DisplayName("test List Slots by provider id")
  void testListSlots() throws ProviderNotFoundException {
    List<SlotEntity> slotEntityList = List.of(TEST_SLOT_ENTITY);
    SlotCollection slotCollection = new SlotCollection()
      .items(mapper.mapSlotEntityListToSlotSummaryList(slotEntityList));
    when(slotService.search(TEST_PROVIDER_ID, TEST_ZONE_START_TIME, TEST_ZONE_END_TIME, TEST_LIMIT, TEST_PAGE))
      .thenReturn(slotCollection);
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/slots")
      .queryParam("startTime", "2021-11-28T09:30:00.000Z")
      .queryParam("endTime", "2021-11-28T10:00:00.000Z")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    HttpResponse<SlotCollection> response = client.toBlocking().exchange(request, SlotCollection.class);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(1, response.getBody().get().getItems().size());
    verify(slotService, times(1))
      .search(TEST_PROVIDER_ID, TEST_ZONE_START_TIME, TEST_ZONE_END_TIME, TEST_LIMIT, TEST_PAGE);
    verifyNoMoreInteractions(slotService);
  }

  @Test
  @DisplayName("test List Slots provider not found exception")
  void testListSlotsProviderNotFoundException() throws ProviderNotFoundException {
    when(slotService.search(TEST_PROVIDER_ID, TEST_ZONE_START_TIME, TEST_ZONE_END_TIME, TEST_LIMIT, TEST_PAGE))
      .thenThrow(ProviderNotFoundException.class);
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/slots")
      .queryParam("startTime", "2021-11-28T09:30:00.000Z")
      .queryParam("endTime", "2021-11-28T10:00:00.000Z")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, SlotCollection.class));
  }

  @Test
  @DisplayName("test List Slots provider not found exception")
  void testListSlotsDateTimeParseException() {
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/slots")
      .queryParam("startTime", "2021-11-28T09:X0:00.000Z")
      .queryParam("endTime", "2021-11-28T10:0X:00.000Z")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, SlotCollection.class));
    verifyNoInteractions(slotService);
  }

  @Test
  @DisplayName("test create feedback endpoint")
  void testCreateFeedbackEndpoint() {
    CreateFeedbackDetailsRequest createFeedbackDetailsRequest = new CreateFeedbackDetailsRequest()
      .patientId(2)
      .text("Good")
      .rating(3);
    when(feedbackService.createFeedback(1, createFeedbackDetailsRequest)).thenReturn(TEST_FEEDBACK_ENTITY);
    HttpRequest<Object> request = HttpRequest.POST("v1/providers/1/feedbacks/", createFeedbackDetailsRequest);
    HttpResponse<Object> response = client.toBlocking().exchange(request);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test List Feedbacks by Provider id")
  void testListFeedbacks() {
    List<FeedbackEntity> testFeedbackEntityList = List.of(TEST_FEEDBACK_ENTITY);
    FeedbackCollection feedbackCollection = new FeedbackCollection()
      .items(mapper.mapFeedbackEntityListToFeedbackSummaryList(testFeedbackEntityList));
    when(feedbackService.search(TEST_PROVIDER_ID, TEST_PATIENT_ID, TEST_FEEDBACK_RATING, TEST_LIMIT, TEST_PAGE))
      .thenReturn(feedbackCollection);
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/feedbacks")
      .queryParam("patientId", TEST_PATIENT_ID)
      .queryParam("rating", TEST_FEEDBACK_RATING)
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    HttpResponse<FeedbackCollection> response = client.toBlocking().exchange(request, FeedbackCollection.class);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(1, response.getBody().get().getItems().size());
    verify(feedbackService, times(1))
      .search(TEST_PROVIDER_ID, TEST_PATIENT_ID, TEST_FEEDBACK_RATING, TEST_LIMIT, TEST_PAGE);
    verifyNoMoreInteractions(feedbackService);
  }

  @Test
  @DisplayName("test get schedule by id")
  void testGetScheduleById() throws ScheduleNotFoundException {
    when(scheduleService.getSchedule(TEST_SCHEDULE_ID)).thenReturn(TEST_SCHEDULE_ENTITY);
    HttpRequest<Object> request = HttpRequest.GET(
      "v1/providers/" + TEST_PROVIDER_ID + "/schedules/" + TEST_SCHEDULE_ID
    );
    HttpResponse<ScheduleEntity> response = client.toBlocking().exchange(request, ScheduleEntity.class);
    assertEquals(HttpStatus.OK, response.status());
  }

  @Test
  @DisplayName("test get schedule by id")
  void testGetScheduleByIdException() throws ScheduleNotFoundException {
    when(scheduleService.getSchedule(TEST_SCHEDULE_ID)).thenThrow(ScheduleNotFoundException.class);
    HttpRequest<Object> request = HttpRequest.GET(
      "v1/providers/" + TEST_PROVIDER_ID + "/schedules/" + TEST_SCHEDULE_ID
    );
    assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, ScheduleEntity.class));
  }

  @Test
  @DisplayName("test list schedules")
  void testListSchedules() throws ProviderNotFoundException {
    List<ScheduleEntity> scheduleEntityList = List.of(TEST_SCHEDULE_ENTITY);
    ScheduleCollection scheduleCollection = new ScheduleCollection()
      .items(mapper.mapScheduleEntityListToScheduleSummaryList(scheduleEntityList));

    when(scheduleService.search(TEST_PROVIDER_ID, TEST_LIMIT, TEST_PAGE)).thenReturn(scheduleCollection);
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/schedules")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    HttpResponse<ScheduleCollection> response = client.toBlocking().exchange(request, ScheduleCollection.class);
    assertEquals(HttpStatus.OK, response.status());
    assertEquals(1, response.getBody().get().getItems().size());
    verify(scheduleService, times(1)).search(TEST_PROVIDER_ID, TEST_LIMIT, TEST_PAGE);
    verifyNoMoreInteractions(scheduleService);
  }

  @Test
  @DisplayName("test list schedules exception")
  void testListSchedulesException() throws ProviderNotFoundException {
    when(scheduleService.search(TEST_PROVIDER_ID, TEST_LIMIT, TEST_PAGE)).thenThrow(ProviderNotFoundException.class);
    URI uri = UriBuilder
      .of("v1/providers/" + TEST_PROVIDER_ID + "/schedules")
      .queryParam("limit", TEST_LIMIT)
      .queryParam("page", TEST_PAGE)
      .build();
    HttpRequest<Object> request = HttpRequest.GET(uri);
    assertThrows(
      HttpClientResponseException.class,
      () -> client.toBlocking().exchange(request, ScheduleCollection.class)
    );
  }

  private static Provider buildProvider() {
    List<String> tags = new ArrayList<>();
    tags.add("test");
    Provider provider = new Provider();
    provider.setId(TEST_PROVIDER_ID);
    provider.setUsername(TEST_USER_NAME);
    provider.setFirstName("John");
    provider.setMiddleName("Miller");
    provider.setLastName("Doe");
    provider.setTitle("Dr");
    provider.setPhone("123");
    provider.setEmail("jndoe@uho.com");
    provider.setGender(Gender.MALE);
    provider.setCity(TEST_CITY);
    provider.setSpeciality(TEST_SPECIALITY);
    provider.setTags(tags);
    return provider;
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
