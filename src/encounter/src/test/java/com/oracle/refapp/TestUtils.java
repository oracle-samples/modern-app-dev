/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp;

import com.oracle.refapp.constants.CodeType;
import com.oracle.refapp.domain.entity.*;
import com.oracle.refapp.model.*;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

  public static final Integer TEST_CODE_ENTITY = 1;
  public static final CodeType TEST_CODE_TYPE = CodeType.ENCOUNTER;
  public static final String TEST_CODE = "410620009";
  public static final String TEST_TEXT = "Well child visit (procedure)";
  public static final Integer TEST_PATIENT_ID = 1;
  public static final Integer TEST_PROVIDER_ID = 2;
  public static final Integer TEST_APPOINTMENT_ID = 3;
  public static final Integer TEST_LIMIT = 10;
  public static final Integer TEST_PAGE = 0;

  public static final String ENCOUNTER_ID = "encounter_id";
  public static final Encounter TEST_ENCOUNTER = buildEncounter();
  public static final EncounterEntity TEST_ENCOUNTER_ENTITY = buildEncounterEntity();
  public static final EncounterSummary TEST_ENCOUNTER_SUMMARY = buildEncounterSummary();
  public static final String TEST_ENCOUNTER_COLLECTION_NAME = "ENCOUNTERS";

  public static CodeEntity buildCodeEntity() {
    CodeEntity codeEntity = new CodeEntity();
    codeEntity.setId(TEST_CODE_ENTITY);
    codeEntity.setType(TEST_CODE_TYPE);
    codeEntity.setCode(TEST_CODE);
    codeEntity.setText(TEST_TEXT);
    return codeEntity;
  }

  public static CodeSummary buildCodeSummary() {
    CodeSummary codeSummary = new CodeSummary();
    codeSummary.setCode(TEST_CODE);
    codeSummary.setText(TEST_TEXT);
    return codeSummary;
  }

  private static EncounterEntity buildEncounterEntity() {
    RecommendationEntity recommendation = new RecommendationEntity();
    recommendation.setRecommendationId("31e59371-98ba-4ab4-854e-5e016965c3di");
    recommendation.setRecommendationDate("2012-12-02T12:48:16+05:30");
    recommendation.setRecommendedBy("Dr. Danilo Mraz");
    recommendation.setInstruction("Test recommendation");
    recommendation.setAdditionalInstructions("None");

    ConditionEntity condition = new ConditionEntity();
    condition.setConditionId("be4ae8a8-6002-4b66-883f-f9351d7f09cc");
    condition.setCode("Risk activity involvement (finding)");
    condition.setCategory("Encounter Diagnosis");
    condition.setClinicalStatus("active");
    condition.setVerificationStatus("confirmed");
    condition.setRecordedDate("2012-12-02T12:48:16+05:30");

    ObservationEntity observation = new ObservationEntity();
    observation.setObservationId("0b07bd29-2cf6-4253-974c-6b595b4a955f");
    observation.setCategory("vital - signs");
    observation.setStatus("final");
    observation.setParameterType("Body Height");
    observation.setDateRecorded("2012-12 - 02 T12: 48: 16 + 05: 30");

    EncounterEntity encounter = new EncounterEntity();
    encounter.setEncounterId("31e59371-98ba-4ab4-854e-5e016965c3df");
    encounter.setProviderId(TEST_PROVIDER_ID);
    encounter.setPatientId(TEST_PATIENT_ID);
    encounter.setAppointmentId(TEST_APPOINTMENT_ID);
    encounter.setLocation("NP2U, LLC");
    encounter.setFollowUpRequested(true);
    encounter.setReasonCode("Choroidal hemorrhage");
    encounter.setServiceProvider("NP2U, LLC");
    encounter.setRecommendation(recommendation);
    List<ConditionEntity> conditionList = new ArrayList<>(1);
    conditionList.add(condition);
    encounter.setConditions(conditionList);
    List<ObservationEntity> observationList = new ArrayList<>(1);
    observationList.add(observation);
    encounter.setObservations(observationList);
    return encounter;
  }

  private static EncounterSummary buildEncounterSummary() {
    return new EncounterSummary(
      "31e59371-98ba-4ab4-854e-5e016965c3df",
      TEST_PATIENT_ID,
      TEST_PROVIDER_ID,
      TEST_APPOINTMENT_ID,
      "Dr. John Doe",
      "Encounter for problem (procedure)",
      "Choroidal hemorrhage",
      "Test recommendation"
    )
      .type("Mr John Smith");
  }

  private static Encounter buildEncounter() {
    Recommendation recommendation = new Recommendation("Test recommendation", "None")
      .recommendedBy("Dr. Danilo Mraz")
      .recommendationId("31e59371-98ba-4ab4-854e-5e016965c3di")
      .recommendationDate("2012-12-02T12:48:16+05:30");

    Condition condition = new Condition(
      "be4ae8a8-6002-4b66-883f-f9351d7f09cc",
      "Risk activity involvement (finding)",
      "active",
      "confirmed",
      "Encounter Diagnosis",
      "2012-12-02T12:48:16+05:30"
    );

    Observation observation = new Observation();
    observation.setObservationId("0b07bd29-2cf6-4253-974c-6b595b4a955f");
    observation.setCategory("vital - signs");
    observation.setStatus("final");
    observation.setParameterType("Body Height");
    observation.setDateRecorded("2012-12 - 02 T12: 48: 16 + 05: 30");
    Encounter encounter = new Encounter(
      TEST_PROVIDER_ID,
      TEST_PATIENT_ID,
      TEST_APPOINTMENT_ID,
      "Test Type",
      true,
      "Choroidal hemorrhage",
      recommendation
    )
      .encounterId("31e59371-98ba-4ab4-854e-5e016965c3df")
      .participant(new Participant().type("Test Type").name("Test Participant"))
      .patientName("Mr John Smith")
      .period(new Period("2012-12-02T12:48:16+05:30", "2012-12-02T12:48:17+05:30"))
      .location("NP2U, LLC")
      .status("Test Status");
    List<Condition> conditionList = new ArrayList<>(1);
    conditionList.add(condition);
    encounter.setConditions(conditionList);
    List<Observation> observationList = new ArrayList<>(1);
    observationList.add(observation);
    encounter.setObservations(observationList);
    return encounter;
  }
}
