/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.mappers;

import static com.oracle.refapp.provider.TestUtils.*;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import com.oracle.refapp.provider.models.CreateProviderDetailsRequest;
import com.oracle.refapp.provider.models.CreateScheduleDetailsRequest;
import com.oracle.refapp.provider.models.Gender;
import com.oracle.refapp.provider.models.Provider;
import com.oracle.refapp.provider.models.Schedule;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProviderMapperTest {

  private final ProviderMapper providerMapper = new ProviderMapperImpl();

  private static final CreateProviderDetailsRequest TEST_PROVIDER_REQUEST = buildCreateProviderDetailsRequest();

  @Test
  @DisplayName("test Provider Mapper - mapApiToDomainModels")
  void testMapperMapApiToDomainModels() {
    ProviderEntity result = providerMapper.mapCreateProviderDetailsToProviderEntity(TEST_PROVIDER_REQUEST);
    Assertions.assertEquals(result.getUsername(), TEST_PROVIDER_REQUEST.getUsername());
    Assertions.assertEquals(result.getFirstName(), TEST_PROVIDER_REQUEST.getFirstName());
    Assertions.assertEquals(result.getMiddleName(), TEST_PROVIDER_REQUEST.getMiddleName());
    Assertions.assertEquals(result.getLastName(), TEST_PROVIDER_REQUEST.getLastName());
    Assertions.assertEquals(result.getTitle(), TEST_PROVIDER_REQUEST.getTitle());
    Assertions.assertEquals(result.getPhone(), TEST_PROVIDER_REQUEST.getPhone());
    Assertions.assertEquals(result.getEmail(), TEST_PROVIDER_REQUEST.getEmail());
    Assertions.assertEquals(result.getGender(), TEST_PROVIDER_REQUEST.getGender());
    Assertions.assertEquals(result.getZip(), TEST_PROVIDER_REQUEST.getZip());
    Assertions.assertEquals(result.getCity(), TEST_PROVIDER_REQUEST.getCity());
    Assertions.assertEquals(result.getCountry(), TEST_PROVIDER_REQUEST.getCountry());
    Assertions.assertEquals(result.getSpeciality(), TEST_PROVIDER_REQUEST.getSpeciality());
    Assertions.assertEquals(result.getQualification(), TEST_PROVIDER_REQUEST.getQualification());
    Assertions.assertEquals(result.getDesignation(), TEST_PROVIDER_REQUEST.getDesignation());
    Assertions.assertEquals(result.getProfessionalSummary(), TEST_PROVIDER_REQUEST.getProfessionalSummary());
    Assertions.assertEquals(result.getInterests(), TEST_PROVIDER_REQUEST.getInterests());
    Assertions.assertEquals(result.getInterests(), TEST_PROVIDER_REQUEST.getInterests());
    Assertions.assertEquals(result.getHospitalName(), TEST_PROVIDER_REQUEST.getHospitalName());
    Assertions.assertEquals(result.getHospitalPhone(), TEST_PROVIDER_REQUEST.getHospitalPhone());
    Assertions.assertEquals(result.getHospitalAddress(), TEST_PROVIDER_REQUEST.getHospitalAddress());
  }

  @Test
  @DisplayName("test Provider Mapper - mapApiToDomainModels - null check")
  void testMapperMapApiToDomainModelsNullCheck() {
    ProviderEntity result = providerMapper.mapCreateProviderDetailsToProviderEntity(null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Provider Mapper - mapDomainToApiModels")
  void testMapperMapDomainToApiModels() {
    Provider result = providerMapper.mapProviderEntityToProvider(TEST_PROVIDER_ENTITY);
    Assertions.assertEquals(result.getId(), TEST_PROVIDER_ENTITY.getId());
    Assertions.assertEquals(result.getUsername(), TEST_PROVIDER_ENTITY.getUsername());
    Assertions.assertEquals(result.getFirstName(), TEST_PROVIDER_ENTITY.getFirstName());
    Assertions.assertEquals(result.getMiddleName(), TEST_PROVIDER_ENTITY.getMiddleName());
    Assertions.assertEquals(result.getLastName(), TEST_PROVIDER_ENTITY.getLastName());
    Assertions.assertEquals(result.getTitle(), TEST_PROVIDER_ENTITY.getTitle());
    Assertions.assertEquals(result.getPhone(), TEST_PROVIDER_ENTITY.getPhone());
    Assertions.assertEquals(result.getEmail(), TEST_PROVIDER_ENTITY.getEmail());
    Assertions.assertEquals(result.getGender(), TEST_PROVIDER_ENTITY.getGender());
    Assertions.assertEquals(result.getZip(), TEST_PROVIDER_ENTITY.getZip());
    Assertions.assertEquals(result.getCity(), TEST_PROVIDER_ENTITY.getCity());
    Assertions.assertEquals(result.getCountry(), TEST_PROVIDER_ENTITY.getCountry());
    Assertions.assertEquals(result.getSpeciality(), TEST_PROVIDER_ENTITY.getSpeciality());
    Assertions.assertEquals(result.getQualification(), TEST_PROVIDER_ENTITY.getQualification());
    Assertions.assertEquals(result.getDesignation(), TEST_PROVIDER_ENTITY.getDesignation());
    Assertions.assertEquals(result.getProfessionalSummary(), TEST_PROVIDER_ENTITY.getProfessionalSummary());
    Assertions.assertEquals(result.getInterests(), TEST_PROVIDER_ENTITY.getInterests());
    Assertions.assertEquals(result.getInterests(), TEST_PROVIDER_ENTITY.getInterests());
    Assertions.assertEquals(result.getHospitalName(), TEST_PROVIDER_ENTITY.getHospitalName());
    Assertions.assertEquals(result.getHospitalPhone(), TEST_PROVIDER_ENTITY.getHospitalPhone());
    Assertions.assertEquals(result.getHospitalAddress(), TEST_PROVIDER_ENTITY.getHospitalAddress());
    Assertions.assertEquals(1, result.getTags().size());
  }

  @Test
  @DisplayName("test Provider Mapper - mapDomainToApiModels - null check")
  void testMapperMapDomainToApiModelsNullCheck() {
    Provider result = providerMapper.mapProviderEntityToProvider(null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Schedule - mapApiToDomainModels")
  void testScheduleMapApiToDomainModels() {
    CreateScheduleDetailsRequest request = new CreateScheduleDetailsRequest();
    request.setStartTime(TEST_ZONE_START_TIME);
    request.setEndTime(TEST_ZONE_END_TIME);
    Assertions.assertEquals(TEST_ZONE_START_TIME, request.getStartTime());
    Assertions.assertEquals(TEST_ZONE_END_TIME, request.getEndTime());
  }

  @Test
  @DisplayName("test Schedule - mapApiToDomainModels - null check")
  void testScheduleMapApiToDomainModelsNullCheck() {
    ScheduleEntity result = providerMapper.mapApiToDomainModels(null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Schedule - mapDomainToApiModels")
  void testScheduleMapDomainToApiModels() {
    ScheduleEntity scheduleEntity = new ScheduleEntity();
    scheduleEntity.setId(1);
    scheduleEntity.setProvider(TEST_PROVIDER_ENTITY);
    scheduleEntity.setStartTime(TEST_ZONE_START_TIME);
    scheduleEntity.setEndTime(TEST_ZONE_END_TIME);
    Schedule result = providerMapper.mapDomainToApiModels(scheduleEntity);
    Assertions.assertEquals(result.getProviderId(), scheduleEntity.getProvider().getId());
    Assertions.assertEquals(TEST_ZONE_START_TIME, result.getStartTime());
    Assertions.assertEquals(TEST_ZONE_END_TIME, result.getEndTime());
  }

  @Test
  @DisplayName("test Schedule- mapDomainToApiModels - null check")
  void testScheduleMapDomainToApiModelsNullCheck() {
    Schedule result = providerMapper.mapDomainToApiModels((ScheduleEntity) null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test Schedule List - mapDomainToApiModels")
  void testScheduleMapDomainToApiListModels() {
    List<ScheduleEntity> scheduleEntityList = new ArrayList<>();
    ScheduleEntity scheduleEntity = new ScheduleEntity();
    scheduleEntity.setId(1);
    scheduleEntity.setProvider(TEST_PROVIDER_ENTITY);
    scheduleEntity.setStartTime(TEST_ZONE_START_TIME);
    scheduleEntity.setEndTime(TEST_ZONE_END_TIME);
    scheduleEntityList.add(scheduleEntity);
    List<Schedule> result = providerMapper.mapDomainToApiListModels(scheduleEntityList);
    Assertions.assertEquals(result.size(), scheduleEntityList.size());
  }

  @Test
  @DisplayName("test Schedule List- mapDomainToApiModels - null check")
  void testScheduleMapDomainToApiListModelsNullCheck() {
    List<Schedule> result = providerMapper.mapDomainToApiListModels(null);
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("test map string To list")
  void testMapStringToList() {
    String tags = "tag1,tag2";
    List<String> tagList = providerMapper.mapStringToList(tags);
    Assertions.assertEquals(2, tagList.size());
  }

  @Test
  @DisplayName("test map list To string")
  void testMapListToString() {
    List<String> tagList = new ArrayList<>();
    tagList.add("tag1");
    tagList.add("tag2");
    String tags = providerMapper.mapListToString(tagList);
    Assertions.assertEquals("tag1,tag2", tags);
  }

  private static CreateProviderDetailsRequest buildCreateProviderDetailsRequest() {
    List<String> tags = new ArrayList<>();
    tags.add("test");
    CreateProviderDetailsRequest request = new CreateProviderDetailsRequest();
    request.setUsername("jndoe");
    request.setFirstName("John");
    request.setMiddleName("Miller");
    request.setLastName("Doe");
    request.setTitle("Dr");
    request.setPhone("123");
    request.setEmail("jndoe@uho.com");
    request.setGender(Gender.MALE);
    request.setCity(TEST_CITY);
    request.setSpeciality(TEST_SPECIALITY);
    request.setTags(tags);
    return request;
  }
}
