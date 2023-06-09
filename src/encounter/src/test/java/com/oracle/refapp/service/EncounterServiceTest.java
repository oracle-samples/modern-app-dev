/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.oracle.refapp.domain.entity.CodeEntity;
import com.oracle.refapp.domain.entity.EncounterEntity;
import com.oracle.refapp.domain.entity.EncounterEntityCollection;
import com.oracle.refapp.domain.repository.CodeRepository;
import com.oracle.refapp.domain.repository.EncounterRepository;
import com.oracle.refapp.exceptions.EncounterCRUDFailedException;
import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.helpers.ObjectStorageHelper;
import com.oracle.refapp.mapper.EncounterMapper;
import com.oracle.refapp.model.CodeCollection;
import com.oracle.refapp.model.CodeSummary;
import com.oracle.refapp.model.Encounter;
import com.oracle.refapp.model.EncounterCollection;
import com.oracle.refapp.search.SearchCriteria;
import com.oracle.refapp.search.SearchFilterBuilder;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EncounterServiceTest {

  private final CodeRepository codeRepository = mock(CodeRepository.class);
  private final EncounterRepository encounterRepository = mock(EncounterRepository.class);
  private final EncounterMapper encounterMapper = mock(EncounterMapper.class);
  private final ObjectStorageHelper objectStorageHelper = mock(ObjectStorageHelper.class);
  private final SearchFilterBuilder searchFilterBuilder = mock(SearchFilterBuilder.class);

  private final EncounterService encounterService = new EncounterService(
    codeRepository,
    encounterRepository,
    encounterMapper,
    objectStorageHelper,
    searchFilterBuilder
  );

  @Test
  @DisplayName(("test List Codes method"))
  void testListCodes() {
    List<CodeSummary> codeSummaryList = List.of(buildCodeSummary());
    CodeCollection codeCollection = new CodeCollection().items(codeSummaryList);
    Page<CodeEntity> page = Page.of(List.of(buildCodeEntity()), Pageable.from(TEST_PAGE, TEST_LIMIT), 1);
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    when(codeRepository.findByType(TEST_CODE_TYPE, Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order))))
      .thenReturn(page);
    when(encounterMapper.mapDomainToApiModels(page.getContent())).thenReturn(codeSummaryList);
    CodeCollection actual = encounterService.listCodes(TEST_CODE_TYPE, TEST_LIMIT, TEST_PAGE);
    assertIterableEquals(codeCollection.getItems(), actual.getItems());
    verify(codeRepository, times(1)).findByType(TEST_CODE_TYPE, Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order)));
    verifyNoMoreInteractions(codeRepository);
  }

  @Test
  @DisplayName(("test List ENCOUNTERS method"))
  void testListEncounters() throws EncounterCRUDFailedException, EncounterServiceException {
    SearchCriteria searchCriteria = new SearchCriteria();
    searchCriteria.setPatientId(TEST_PATIENT_ID);
    searchCriteria.setProviderId(TEST_PROVIDER_ID);
    searchCriteria.setAppointmentId(TEST_APPOINTMENT_ID);
    searchCriteria.setPage(4);
    searchCriteria.setLimit(5);
    int testNextPage = 10;
    String testSearchFilter = "testFilter";
    when(searchFilterBuilder.build(searchCriteria)).thenReturn(testSearchFilter);
    List<EncounterEntity> encounterEntityList = new ArrayList<>();
    encounterEntityList.add(TEST_ENCOUNTER_ENTITY);
    EncounterEntityCollection encounterEntityCollection = new EncounterEntityCollection(
      encounterEntityList,
      testNextPage
    );
    when(encounterRepository.filterEncounters(testSearchFilter, searchCriteria.getPage(), searchCriteria.getLimit()))
      .thenReturn(encounterEntityCollection);
    when(encounterMapper.mapEncounterEntityToEncounterSummary(TEST_ENCOUNTER_ENTITY))
      .thenReturn(TEST_ENCOUNTER_SUMMARY);
    EncounterCollection actual = encounterService.listEncounters(searchCriteria);
    assertEquals(1, actual.getItems().size());
    assertEquals(TEST_PATIENT_ID, actual.getItems().get(0).getPatientId());
    assertEquals(TEST_PROVIDER_ID, actual.getItems().get(0).getProviderId());
    assertEquals(10, actual.getNextPage()); // Larger than (page+1)*limit hence return page+1 (= 0+1 = 1)
    verify(encounterMapper, times(1)).mapEncounterEntityToEncounterSummary(any());
  }

  @Test
  @DisplayName(("Test encounter creation"))
  void testCreateEncounter() throws EncounterServiceException {
    String filePath = "testPath";
    when(encounterMapper.mapApiToDomainModels(TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER_ENTITY);
    Encounter actual = encounterService.createEncounter(TEST_ENCOUNTER);
    verify(objectStorageHelper, times(1)).upload(TEST_ENCOUNTER);
    assertNotNull(actual);
  }

  @Test
  @DisplayName(("Test encounter creation without recommendation"))
  void testCreateEncounterWithoutRecommendation() throws EncounterServiceException {
    Encounter encounter = new Encounter(null, null, null, null, null, null, null);
    encounter.setRecommendation(null);
    encounter.setConditions(TEST_ENCOUNTER.getConditions());
    encounter.setObservations(TEST_ENCOUNTER.getObservations());
    when(encounterMapper.mapApiToDomainModels(encounter)).thenReturn(TEST_ENCOUNTER_ENTITY);
    Encounter actual = encounterService.createEncounter(encounter);
    verifyNoInteractions(objectStorageHelper);
    assertNotNull(actual);
  }

  @Test
  void testCreateEncounterException() throws EncounterCRUDFailedException {
    when(encounterMapper.mapApiToDomainModels(TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER_ENTITY);
    doThrow(EncounterCRUDFailedException.class).when(encounterRepository).createEncounter(TEST_ENCOUNTER_ENTITY);
    assertThrows(EncounterServiceException.class, () -> encounterService.createEncounter(TEST_ENCOUNTER));
    verifyNoInteractions(objectStorageHelper);
  }

  @Test
  @DisplayName(("Test encounter deletion"))
  void testDeleteEncounter() throws EncounterServiceException, EncounterCRUDFailedException {
    when(encounterRepository.deleteEncounter(ENCOUNTER_ID)).thenReturn(1);
    int deleted = encounterService.deleteEncounter(ENCOUNTER_ID);
    verify(encounterRepository, times(1)).deleteEncounter(ENCOUNTER_ID);
    assertEquals(1, deleted);
  }

  @Test
  void testDeleteEncounterException() throws EncounterServiceException, EncounterCRUDFailedException {
    encounterService.deleteEncounter(ENCOUNTER_ID);
    when(encounterRepository.deleteEncounter(ENCOUNTER_ID)).thenThrow(EncounterCRUDFailedException.class);
    assertThrows(EncounterServiceException.class, () -> encounterService.deleteEncounter(ENCOUNTER_ID));
  }

  @Test
  @DisplayName(("Test get encounter"))
  void testGetEncounter() throws EncounterCRUDFailedException, EncounterServiceException {
    when(encounterRepository.findByEncounterId(TEST_ENCOUNTER_ENTITY.getEncounterId()))
      .thenReturn(TEST_ENCOUNTER_ENTITY);
    when(encounterMapper.mapDomainToApiModels(TEST_ENCOUNTER_ENTITY)).thenReturn(TEST_ENCOUNTER);
    Encounter actual = encounterService.getEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId());
    assertEquals(TEST_ENCOUNTER_ENTITY.getEncounterId(), actual.getEncounterId());
    verify(encounterRepository, times(1)).findByEncounterId(TEST_ENCOUNTER_ENTITY.getEncounterId());
  }

  @Test
  void testGetEncounterException() throws EncounterCRUDFailedException {
    when(encounterRepository.findByEncounterId(ENCOUNTER_ID)).thenThrow(EncounterCRUDFailedException.class);
    assertThrows(EncounterServiceException.class, () -> encounterService.getEncounter(ENCOUNTER_ID));
  }

  @Test
  @DisplayName(("Test encounter update"))
  void testUpdateEncounter() throws EncounterServiceException, EncounterCRUDFailedException {
    when(encounterMapper.mapApiToDomainModels(TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER_ENTITY);
    Encounter actual = encounterService.updateEncounter(TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER);
    verify(encounterRepository, times(1)).updateEncounter(TEST_ENCOUNTER.getEncounterId(), TEST_ENCOUNTER_ENTITY);
    assertNotNull(actual);
    assertEquals(TEST_ENCOUNTER.getEncounterId(), actual.getEncounterId());
    assertEquals(TEST_ENCOUNTER.getPatientName(), actual.getPatientName());
  }

  @Test
  void testUpdateEncounterException() throws EncounterCRUDFailedException {
    when(encounterMapper.mapApiToDomainModels(TEST_ENCOUNTER)).thenReturn(TEST_ENCOUNTER_ENTITY);
    doThrow(EncounterCRUDFailedException.class)
      .when(encounterRepository)
      .updateEncounter(ENCOUNTER_ID, TEST_ENCOUNTER_ENTITY);
    assertThrows(EncounterServiceException.class, () -> encounterService.getEncounter(ENCOUNTER_ID));
  }
}
