/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.connections.JSONConnection;
import com.oracle.refapp.domain.entity.EncounterEntity;
import com.oracle.refapp.domain.entity.EncounterEntityCollection;
import com.oracle.refapp.exceptions.EncounterCRUDFailedException;
import com.oracle.refapp.model.FilterSpecification;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import oracle.soda.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class EncounterRepository {

  private static final Logger LOG = LoggerFactory.getLogger(EncounterRepository.class);
  private static final String ENCOUNTER_COLLECTION_NAME = "ENCOUNTERS";
  private static final String ENCOUNTER_ID_KEY = "encounter_id";

  private final JSONConnection jsonConnection;
  private final ObjectMapper mapper;

  public EncounterRepository(JSONConnection jsonConnection, ObjectMapper mapper) {
    this.jsonConnection = jsonConnection;
    this.mapper = mapper;
  }

  public EncounterEntity findByEncounterId(String encounterId) throws EncounterCRUDFailedException {
    try {
      OracleDatabase db = jsonConnection.getDbConnection();
      LOG.info("Getting encounter record for id : {}", encounterId);
      FilterSpecification<String> filterSpec = new FilterSpecification<>(ENCOUNTER_ID_KEY, encounterId);
      String filter = getFilter(filterSpec);
      System.out.println(filter);
      OracleCollection collection = db.openCollection(ENCOUNTER_COLLECTION_NAME);
      OracleDocument response = collection.find().filter(filter).getOne();
      if (response == null) {
        return null;
      }
      LOG.info("Response {}", response);
      String document = response.getContentAsString();
      LOG.info("Document {}", document);
      return mapper.readValue(document, EncounterEntity.class);
    } catch (OracleException | JsonProcessingException e) {
      LOG.error("Could not find encounter id={} : {}", encounterId, e.getLocalizedMessage());
      throw new EncounterCRUDFailedException("Could not find encounter id=" + encounterId);
    }
  }

  public void createEncounter(EncounterEntity encounterEntity) throws EncounterCRUDFailedException {
    try {
      OracleDatabase db = jsonConnection.getDbConnection();
      OracleCollection collection = db.openCollection(ENCOUNTER_COLLECTION_NAME);
      String encounterJson = mapper.writeValueAsString(encounterEntity);
      OracleDocument encounterDocument = db.createDocumentFromString(encounterJson);
      OracleDocument insertedEncounterRecord = collection.insertAndGet(encounterDocument);
      if (insertedEncounterRecord == null) {
        throw new EncounterCRUDFailedException("Failed to Persist Encounter");
      }
    } catch (OracleException | JsonProcessingException e) {
      LOG.error("Error creating encounter record : {}", e.getLocalizedMessage());
      throw new EncounterCRUDFailedException("Failed to Persist Encounter");
    }
  }

  public void updateEncounter(String encounterId, EncounterEntity encounterEntity) throws EncounterCRUDFailedException {
    try {
      OracleDatabase db = jsonConnection.getDbConnection();
      FilterSpecification<String> filterSpec = new FilterSpecification<>(ENCOUNTER_ID_KEY, encounterId);
      String filter = getFilter(filterSpec);
      OracleCollection collection = db.openCollection(ENCOUNTER_COLLECTION_NAME);
      OracleDocument existingDocument = collection.find().filter(filter).getOne();
      String key = existingDocument.getKey();
      String encounterJson = mapper.writeValueAsString(encounterEntity);
      OracleDocument encounterDocument = db.createDocumentFromString(encounterJson);
      OracleDocument currentDocument = collection.find().key(key).replaceOneAndGet(encounterDocument);
      if (currentDocument == null) {
        throw new EncounterCRUDFailedException("Failed to Update Encounter = " + encounterId);
      }
    } catch (OracleException | JsonProcessingException e) {
      LOG.error("Error updating encounter record = {} : {}", encounterId, e.getLocalizedMessage());
      throw new EncounterCRUDFailedException("Failed to Update Encounter = " + encounterId);
    }
  }

  public Integer deleteEncounter(String encounterId) throws EncounterCRUDFailedException {
    try {
      OracleDatabase db = jsonConnection.getDbConnection();
      FilterSpecification<String> filterSpec = new FilterSpecification<>(ENCOUNTER_ID_KEY, encounterId);
      String filter = getFilter(filterSpec);
      LOG.info("Deleting the encounter record with id : {}", encounterId);
      OracleCollection collection = db.openCollection(ENCOUNTER_COLLECTION_NAME);
      int count = collection.find().filter(filter).remove();
      LOG.info("Number of records deleted : {}", count);
      return count;
    } catch (OracleException e) {
      LOG.error("Error deleting the encounter record : {}", e.getLocalizedMessage());
      throw new EncounterCRUDFailedException("Could not delete encounter id=" + encounterId);
    }
  }

  public EncounterEntityCollection filterEncounters(String filter, Integer page, Integer limit)
    throws EncounterCRUDFailedException {
    OracleCursor cursor = null;
    try {
      OracleDatabase db = jsonConnection.getDbConnection();
      OracleCollection encountersColl = db.openCollection(ENCOUNTER_COLLECTION_NAME);
      OracleDocument filterSpec = db.createDocumentFromString(filter);
      cursor = encountersColl.find().filter(filterSpec).skip((long) page * limit).limit(limit).getCursor();
      long totalNumberOfItems = encountersColl.find().filter(filterSpec).count();
      List<EncounterEntity> items = new ArrayList<>();
      while (cursor.hasNext()) {
        OracleDocument resultDoc = cursor.next();
        items.add(mapper.readValue(resultDoc.getContentAsString(), EncounterEntity.class));
      }
      Integer nextPage = totalNumberOfItems > (long) (page + 1) * limit ? page + 1 : null;
      return new EncounterEntityCollection(items, nextPage);
    } catch (OracleException | JsonProcessingException e) {
      LOG.error("Error while filtering encounters", e);
      throw new EncounterCRUDFailedException("Error while filtering encounters");
    } finally {
      if (cursor != null) {
        try {
          cursor.close();
        } catch (IOException e) {
          LOG.error("Error while filtering encounters", e);
          throw new EncounterCRUDFailedException("Error while filtering encounters");
        }
      }
    }
  }

  private String getFilter(FilterSpecification<String> filterSpecObj) {
    return "{" + "\"" + filterSpecObj.getKey() + "\"" + ":" + "\"" + filterSpecObj.getValue() + "\"" + "}";
  }
}
