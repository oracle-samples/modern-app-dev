/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.refapp.constants.CodeType;
import com.oracle.refapp.domain.entity.CodeEntity;
import com.oracle.refapp.domain.entity.EncounterEntity;
import com.oracle.refapp.domain.entity.EncounterEntityCollection;
import com.oracle.refapp.domain.repository.CodeRepository;
import com.oracle.refapp.domain.repository.EncounterRepository;
import com.oracle.refapp.exceptions.EncounterCRUDFailedException;
import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.helpers.ObjectStorageHelper;
import com.oracle.refapp.mapper.EncounterMapper;
import com.oracle.refapp.model.*;
import com.oracle.refapp.search.SearchCriteria;
import com.oracle.refapp.search.SearchFilterBuilder;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class EncounterService {

  private final CodeRepository codeRepository;
  private final EncounterMapper encounterMapper;
  private final ObjectStorageHelper objectStorageHelper;
  private final EncounterRepository encounterRepository;
  private final SearchFilterBuilder searchFilterBuilder;

  private static final Logger LOG = LoggerFactory.getLogger(EncounterService.class);

  public EncounterService(
    CodeRepository codeRepository,
    EncounterRepository encounterRepository,
    EncounterMapper encounterMapper,
    ObjectStorageHelper objectStorageHelper,
    SearchFilterBuilder searchFilterBuilder
  ) {
    this.objectStorageHelper = objectStorageHelper;
    this.encounterRepository = encounterRepository;
    this.searchFilterBuilder = searchFilterBuilder;
    this.encounterMapper = encounterMapper;
    this.codeRepository = codeRepository;
  }

  @Transactional
  public CodeCollection listCodes(CodeType type, Integer limit, Integer page) {
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    Page<CodeEntity> codeEntityPage = codeRepository.findByType(type, Pageable.from(page, limit, Sort.of(order)));
    List<CodeEntity> codeEntityList = codeEntityPage.getContent();
    Integer nextPage = (codeEntityPage.getTotalPages() > codeEntityPage.nextPageable().getNumber())
      ? codeEntityPage.nextPageable().getNumber()
      : null;
    return new CodeCollection().nextPage(nextPage).items(encounterMapper.mapDomainToApiModels(codeEntityList));
  }

  public EncounterCollection listEncounters(SearchCriteria searchCriteria) throws EncounterServiceException {
    try {
      String searchFilter = searchFilterBuilder.build(searchCriteria);
      EncounterEntityCollection entityCollection = encounterRepository.filterEncounters(
        searchFilter,
        searchCriteria.getPage(),
        searchCriteria.getLimit()
      );
      List<EncounterSummary> encounterSummaries = new ArrayList<>();
      entityCollection
        .getEncounterEntityList()
        .forEach(encounterEntity ->
          encounterSummaries.add(encounterMapper.mapEncounterEntityToEncounterSummary(encounterEntity))
        );
      return new EncounterCollection().items(encounterSummaries).nextPage(entityCollection.getNextPage());
    } catch (EncounterCRUDFailedException e) {
      throw new EncounterServiceException(e.getLocalizedMessage());
    }
  }

  public Encounter getEncounter(String encounterId) throws EncounterServiceException {
    try {
      EncounterEntity encounterEntity = encounterRepository.findByEncounterId(encounterId);
      if (encounterEntity == null) {
        throw new EncounterServiceException("Could not find encounter id : " + encounterId);
      }
      return encounterMapper.mapDomainToApiModels(encounterEntity);
    } catch (EncounterCRUDFailedException e) {
      throw new EncounterServiceException(e.getLocalizedMessage());
    }
  }

  public Encounter createEncounter(Encounter encounter) throws EncounterServiceException {
    try {
      assignUniqueIds(encounter);
      LOG.info("Creating the encounter record with id : {}", encounter.getEncounterId());
      EncounterEntity encounterEntity = encounterMapper.mapApiToDomainModels(encounter);
      LOG.info("Encounter {}", encounter);
      LOG.info("EncounterEntity {}", encounterEntity);
      encounterRepository.createEncounter(encounterEntity);
      if (encounter.getRecommendation() != null) {
        LOG.info("Uploading prescription to object storage");
        objectStorageHelper.upload(encounter);
      }
      LOG.info("Encounter record created successfully");
      return encounter;
    } catch (EncounterCRUDFailedException e) {
      throw new EncounterServiceException(e.getLocalizedMessage());
    }
  }

  public Encounter updateEncounter(String encounterId, Encounter updatedEncounter) throws EncounterServiceException {
    try {
      EncounterEntity encounterEntity = encounterMapper.mapApiToDomainModels(updatedEncounter);
      encounterRepository.updateEncounter(encounterId, encounterEntity);
    } catch (EncounterCRUDFailedException e) {
      LOG.error("Error updating the encounter record : {}", e.getLocalizedMessage());
      throw new EncounterServiceException(e.getLocalizedMessage());
    }
    return updatedEncounter;
  }

  public int deleteEncounter(String encounterId) throws EncounterServiceException {
    try {
      return encounterRepository.deleteEncounter(encounterId);
    } catch (EncounterCRUDFailedException e) {
      throw new EncounterServiceException("Could not delete encounter id : " + encounterId);
    }
  }

  private void assignUniqueIds(Encounter encounter) {
    String encounterId = generateUniqueId();
    encounter.setEncounterId(encounterId);
    if (encounter.getRecommendation() != null) {
      String recommendationId = generateUniqueId();
      encounter.getRecommendation().setRecommendationId(recommendationId);
    }
    if (encounter.getConditions() != null) {
      for (Condition condition : encounter.getConditions()) {
        String conditionId = generateUniqueId();
        condition.setConditionId(conditionId);
      }
    }
    if (encounter.getObservations() != null) {
      for (Observation observation : encounter.getObservations()) {
        String observationId = generateUniqueId();
        observation.setObservationId(observationId);
      }
    }
  }

  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }
}
