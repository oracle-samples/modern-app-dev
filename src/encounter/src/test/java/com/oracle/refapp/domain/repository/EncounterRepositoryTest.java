/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.repository;

import static com.oracle.refapp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.connections.JSONConnection;
import com.oracle.refapp.domain.entity.*;
import com.oracle.refapp.exceptions.EncounterCRUDFailedException;
import com.oracle.refapp.model.*;
import java.io.IOException;
import java.util.Map;
import oracle.soda.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class EncounterRepositoryTest {

  private final JSONConnection jsonConnection = mock(JSONConnection.class);
  private final ObjectMapper mapper = new ObjectMapper();
  private final EncounterRepository encounterRepository = new EncounterRepository(jsonConnection, mapper);
  private final OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);

  @Test
  public void testFindByEncounterId() throws OracleException, JsonProcessingException, EncounterCRUDFailedException {
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).getOne()).thenReturn(mockedOracleDocument);
    when(mockedOracleDocument.getContentAsString()).thenReturn(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY));
    EncounterEntity actual = encounterRepository.findByEncounterId(TEST_ENCOUNTER_ENTITY.getEncounterId());
    assertEquals(TEST_ENCOUNTER_ENTITY, actual);
  }

  @Test
  public void testFindByEncounterId_Null_Response() throws OracleException, EncounterCRUDFailedException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).getOne()).thenReturn(null);
    EncounterEntity actual = encounterRepository.findByEncounterId(TEST_ENCOUNTER_ENTITY.getEncounterId());
    assertNull(actual);
  }

  @Test
  public void testFindByEncounterIdException() throws OracleException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).getOne()).thenThrow(OracleException.class);
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.findByEncounterId(TEST_ENCOUNTER_ENTITY.getEncounterId())
    );
  }

  @Test
  @DisplayName(("Test encounter creation"))
  void testCreateEncounter() throws OracleException, IOException, EncounterCRUDFailedException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    when(mockedOracleDatabase.createDocumentFromString(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY)))
      .thenReturn(mockedOracleDocument);
    when(mockedOracleCollection.insertAndGet(mockedOracleDocument)).thenReturn(mockedOracleDocument);
    encounterRepository.createEncounter(TEST_ENCOUNTER_ENTITY);
    verify(mockedOracleCollection, times(1)).insertAndGet(mockedOracleDocument);
  }

  @Test
  public void testCreateEncounterException() throws OracleException, JsonProcessingException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    when(mockedOracleDatabase.createDocumentFromString(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY)))
      .thenReturn(mockedOracleDocument);
    when(mockedOracleCollection.insertAndGet(mockedOracleDocument)).thenReturn(null);
    assertThrows(EncounterCRUDFailedException.class, () -> encounterRepository.createEncounter(TEST_ENCOUNTER_ENTITY));
    when(mockedOracleCollection.insertAndGet(mockedOracleDocument)).thenThrow(OracleException.class);
    assertThrows(EncounterCRUDFailedException.class, () -> encounterRepository.createEncounter(TEST_ENCOUNTER_ENTITY));
  }

  @Test
  @DisplayName(("Test encounter deletion"))
  void testDeleteEncounter() throws OracleException, EncounterCRUDFailedException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).remove()).thenReturn(1);
    int deleted = encounterRepository.deleteEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId());
    assertEquals(1, deleted);
  }

  @Test
  void testDeleteEncounterException() throws OracleException {
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).remove()).thenThrow(OracleException.class);
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.deleteEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId())
    );
  }

  @Test
  @DisplayName(("Test encounter update"))
  void testUpdateEncounter() throws OracleException, JsonProcessingException, EncounterCRUDFailedException {
    String encounterKey = "31e5937198ba4ab4854e5e016965c3di";
    OracleDatabase mockedOracleDatabase = mock(OracleDatabase.class);
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).getOne()).thenReturn(mockedOracleDocument);
    when(mockedOracleDocument.getKey()).thenReturn(encounterKey);
    when(mockedOracleDatabase.createDocumentFromString(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY)))
      .thenReturn(mockedOracleDocument);
    when(mockedOracleCollection.find().key(encounterKey).replaceOneAndGet(mockedOracleDocument))
      .thenReturn(mockedOracleDocument);
    when(mockedOracleDocument.getContentAsString()).thenReturn(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY));
    encounterRepository.updateEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId(), TEST_ENCOUNTER_ENTITY);
    verify(mockedOracleCollection.find().key(encounterKey), times(1)).replaceOneAndGet(mockedOracleDocument);
  }

  @Test
  @DisplayName(("Test encounter update exception"))
  void testUpdateEncounterException() throws OracleException, JsonProcessingException {
    String encounterKey = "31e5937198ba4ab4854e5e016965c3di";
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    FilterSpecification<String> filterSpec = new FilterSpecification<>(
      ENCOUNTER_ID,
      TEST_ENCOUNTER_ENTITY.getEncounterId()
    );
    String filter = getFilter(filterSpec);
    when(mockedOracleCollection.find().filter(filter).getOne()).thenReturn(mockedOracleDocument);
    when(mockedOracleDocument.getKey()).thenReturn(encounterKey);
    when(mockedOracleDatabase.createDocumentFromString(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY)))
      .thenReturn(mockedOracleDocument);
    when(mockedOracleCollection.find().key(encounterKey).replaceOneAndGet(mockedOracleDocument)).thenReturn(null);
    when(mockedOracleDocument.getContentAsString()).thenReturn(mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY));
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.updateEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId(), TEST_ENCOUNTER_ENTITY)
    );
    when(mockedOracleCollection.find().key(encounterKey).replaceOneAndGet(mockedOracleDocument))
      .thenThrow(OracleException.class);
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.updateEncounter(TEST_ENCOUNTER_ENTITY.getEncounterId(), TEST_ENCOUNTER_ENTITY)
    );
  }

  @Test
  public void testFilterEncountersException() throws OracleException, JsonProcessingException {
    String filter = buildTestQueryString();
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    OracleCursor mockedOracleCursor = mock(OracleCursor.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    when(mockedOracleDatabase.createDocumentFromString(filter)).thenReturn(mockedOracleDocument);
    when(
      mockedOracleCollection
        .find()
        .filter(mockedOracleDocument)
        .skip((long) TEST_PAGE * TEST_LIMIT)
        .limit(TEST_LIMIT)
        .getCursor()
    )
      .thenReturn(mockedOracleCursor);
    when(mockedOracleCollection.find().filter(mockedOracleDocument).count()).thenReturn(50L);
    when(mockedOracleCursor.hasNext()).thenReturn(true, false);
    when(mockedOracleCursor.next()).thenThrow(OracleException.class);
    when(mockedOracleDocument.getContentAsString()).thenReturn(buildTestContentAsString());
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.filterEncounters(filter, TEST_PAGE, TEST_LIMIT)
    );
  }

  @Test
  public void testFilterEncountersException_CursorNotClosed() throws OracleException, IOException {
    String filter = buildTestQueryString();
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    OracleCursor mockedOracleCursor = mock(OracleCursor.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    when(mockedOracleDatabase.createDocumentFromString(filter)).thenReturn(mockedOracleDocument);
    when(
      mockedOracleCollection
        .find()
        .filter(mockedOracleDocument)
        .skip((long) TEST_PAGE * TEST_LIMIT)
        .limit(TEST_LIMIT)
        .getCursor()
    )
      .thenReturn(mockedOracleCursor);
    when(mockedOracleCollection.find().filter(mockedOracleDocument).count()).thenReturn(50L);
    when(mockedOracleCursor.hasNext()).thenReturn(true, false);
    when(mockedOracleCursor.next()).thenReturn(mockedOracleDocument);
    doThrow(IOException.class).when(mockedOracleCursor).close();
    when(mockedOracleDocument.getContentAsString()).thenReturn(buildTestContentAsString());
    assertThrows(
      EncounterCRUDFailedException.class,
      () -> encounterRepository.filterEncounters(filter, TEST_PAGE, TEST_LIMIT)
    );
  }

  @Test
  public void testFilterEncounters() throws OracleException, JsonProcessingException, EncounterCRUDFailedException {
    String filter = buildTestQueryString();
    OracleCollection mockedOracleCollection = mock(OracleCollection.class, RETURNS_DEEP_STUBS);
    OracleDocument mockedOracleDocument = mock(OracleDocument.class);
    OracleCursor mockedOracleCursor = mock(OracleCursor.class);
    when(jsonConnection.getDbConnection()).thenReturn(mockedOracleDatabase);
    when(mockedOracleDatabase.openCollection(TEST_ENCOUNTER_COLLECTION_NAME)).thenReturn(mockedOracleCollection);
    when(mockedOracleDatabase.createDocumentFromString(filter)).thenReturn(mockedOracleDocument);
    when(
      mockedOracleCollection
        .find()
        .filter(mockedOracleDocument)
        .skip((long) TEST_PAGE * TEST_LIMIT)
        .limit(TEST_LIMIT)
        .getCursor()
    )
      .thenReturn(mockedOracleCursor);
    when(mockedOracleCollection.find().filter(mockedOracleDocument).count()).thenReturn(50L);
    when(mockedOracleCursor.hasNext()).thenReturn(true, false);
    when(mockedOracleCursor.next()).thenReturn(mockedOracleDocument);
    when(mockedOracleDocument.getContentAsString()).thenReturn(buildTestContentAsString());
    EncounterEntityCollection actual = encounterRepository.filterEncounters(filter, TEST_PAGE, TEST_LIMIT);
    assertEquals(1, actual.getEncounterEntityList().size());
    assertEquals(1, actual.getNextPage()); // Larger than (page+1)*limit hence return page+1 (= 0+1 = 1)
  }

  private String getFilter(FilterSpecification<String> filterSpecObj) {
    return "{" + "\"" + filterSpecObj.getKey() + "\"" + ":" + "\"" + filterSpecObj.getValue() + "\"" + "}";
  }

  private String buildTestQueryString() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(Map.of("patientID", TEST_PATIENT_ID));
  }

  private String buildTestContentAsString() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(TEST_ENCOUNTER_ENTITY);
  }
}
