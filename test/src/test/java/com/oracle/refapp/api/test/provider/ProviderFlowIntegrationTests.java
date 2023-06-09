/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.provider;

import com.oracle.refapp.api.test.configuration.Configuration;
import com.oracle.refapp.api.test.model.Constants;
import com.oracle.refapp.api.test.model.Scope;
import com.oracle.refapp.api.test.service.AppointmentServiceClient;
import com.oracle.refapp.api.test.service.EncounterServiceClient;
import com.oracle.refapp.api.test.service.PatientServiceClient;
import com.oracle.refapp.api.test.service.ProviderServiceClient;
import com.oracle.refapp.api.test.util.Utility;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProviderFlowIntegrationTests {

    @Inject
    EncounterServiceClient encounterServiceClient;

    @Inject
    PatientServiceClient patientServiceClient;

    @Inject
    AppointmentServiceClient appointmentServiceClient;

    @Inject
    ProviderServiceClient providerServiceClient;

    @Inject
    Configuration configuration;

    Map provider;
    Map appointment;
    Map patient;
    Map encounter;
    Integer patientId;
    Integer providerId;
    List<String> createdEncounterList = new ArrayList<>(1);
    private static final Logger LOG = LoggerFactory.getLogger(ProviderFlowIntegrationTests.class);

    @BeforeAll
    void preconditionOne() throws IOException {
        LOG.info("Running configuration initialization for scope : provider" );
        configuration.initConfiguration(Scope.PROVIDER);
        Assumptions.assumeTrue(configuration.getAuthorisation() != null);
        HttpResponse getProviderResponse = providerServiceClient.getProviderClient(configuration, Constants.ID, null);
        if (HttpStatus.OK == getProviderResponse.getStatus()) {
            provider = (Map) getProviderResponse.getBody().get();
            providerId= (Integer)provider.get(Constants.ID);
        }
        LOG.info("Init complete : provider" );
    }

    @BeforeAll
    void preconditionTwo() throws IOException {
        Assumptions.assumeTrue(provider != null );
        Integer providerId = (Integer)provider.get(Constants.ID);
        configuration.initConfiguration(Scope.PATIENT);
        LOG.info("Init complete : patient" );
        Assumptions.assumeTrue(configuration.getAuthorisation() != null);
        HttpResponse response = patientServiceClient.getPatientClient(configuration, Constants.ID);
        if (HttpStatus.OK == response.getStatus()){
            Map patient = (Map)response.getBody().get();
            patientId = (Integer)patient.get(Constants.ID);
            Map<String, Object> criteria = new HashMap<>(1);
            String startDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_START_TIME_1);
            String endDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_END_TIME_1);
            criteria.put(Constants.START_TIME, startDateTime);
            criteria.put(Constants.END_TIME, endDateTime);
            criteria.put(Constants.LIMIT, 20);
            HttpResponse getSlotResponse = providerServiceClient.listSlotsByCriteria(configuration, criteria, providerId);
            if (HttpStatus.OK == getSlotResponse.getStatus()) {
                Map slotResponse = (Map) getSlotResponse.getBody().get();
                Map slot = Utility.getSlot(slotResponse);
                Map<String, Object> slotDetails = new HashMap<>();
                slotDetails.put(Constants.PATIENT_ID, patientId);
                slotDetails.put(Constants.PROVIDER_ID, providerId);
                slotDetails.put(Constants.START_TIME, slot.get(Constants.START_TIME));
                slotDetails.put(Constants.END_TIME, slot.get(Constants.END_TIME));
                slotDetails.put(Constants.PRESCRIPTION, "testprescription");
                Map<String, String> conditionMap = new HashMap<>(1);
                conditionMap.put(Constants.CONDITION, "Common cold");
                slotDetails.put(Constants.PRE_VISIT_DATA, conditionMap);
                appointmentServiceClient.createAppointmentClient(configuration, slotDetails);
            }
        }
    }

    @BeforeAll
    void preconditionThree() throws IOException {
        LOG.info("Running configuration initialization for scope : provider" );
        configuration.initConfiguration(Scope.PROVIDER);
        HttpResponse createEncounterResponse = encounterServiceClient.createEncounterClient(configuration);
        if (HttpStatus.OK == createEncounterResponse.getStatus()) {
            Map<String, Object> encounter = (Map) createEncounterResponse.getBody().get();
            createdEncounterList.add((String)encounter.get(Constants.ENCOUNTER_ID));
        }
        LOG.info("Init complete : provider" );

    }

    @Nested
    @DisplayName("Search appointment by specific criteria")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class testSearchAppointment {

        @Test
        @Order(1)
        @DisplayName("List appointments for the specified date")
        void provider_001() {
            Assumptions.assumeTrue(provider != null);
            Map<String, Object> criteria = new HashMap<>(1);
            String startDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_START_TIME_1);
            String endDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_END_TIME_1);
            criteria.put(Constants.START_TIME, startDateTime);
            criteria.put(Constants.END_TIME, endDateTime);
            criteria.put(Constants.PROVIDER_ID, providerId);
            HttpResponse response = appointmentServiceClient.searchAppointments(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()) {
                Map responseBody = (Map) response.getBody().get();
                List<Map> appointmentList = (List<Map>) responseBody.get(Constants.ITEMS);
                if (appointmentList.size() > 0)
                    appointment = appointmentList.get(0);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertTrue(appointmentList.size() > 0);
            }
        }

        @Test
        @Order(2)
        @DisplayName("View patient details of the appointment")
        void provider_002() {
            Assumptions.assumeTrue(appointment != null);
            Integer patientId = (Integer) appointment.get(Constants.PATIENT_ID);
            HttpResponse response = patientServiceClient.getPatientByTypeClient(configuration, Constants.ID, patientId);
            assertEquals(HttpStatus.OK, response.getStatus());
            patient = (Map) response.getBody().get();
            assertNotNull(patient);
        }

        @Test
        @Order(3)
        @DisplayName("View past encounter list with the patient")
        void provider_003a(){
            boolean precondition = (patient != null && provider != null);
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PROVIDER_ID, providerId);
            criteria.put(Constants.PATIENT_ID, patientId);
            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()) {
                Map responseBody = (Map) response.getBody().get();
                List<Map> encounterList = (List<Map>) responseBody.get(Constants.ITEMS);
                if (encounterList.size() > 0)
                    encounter = encounterList.get(0);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertTrue(encounterList.size() > 0);
            }
        }
        @Test
        @Order(4)
        @DisplayName("View selected encounter details")
        void provider_003b(){
            Assumptions.assumeTrue(encounter != null);
            String encounterId = (String) encounter.get(Constants.ENCOUNTER_ID);
            HttpResponse response = encounterServiceClient.getEncounterClient(configuration, encounterId);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(response.getBody());
        }

        @Test
        @Order(5)
        @DisplayName("View current conditions and symptoms of the patient")
        void provider_004(){
            Assumptions.assumeTrue(appointment != null);
            Integer appointmentId = (Integer) appointment.get(Constants.ID);
            HttpResponse response = appointmentServiceClient.getAppointmentClient(configuration,appointmentId );
            assertEquals(HttpStatus.OK, response.getStatus());
            Map<String, Object>  appointment =(Map) response.getBody().get();
            assertNotNull(appointment.get(Constants.PRE_VISIT_DATA));
        }

        @Test
        @Order(6)
        @DisplayName("List appointments by the patient id")
        void provider_005() {
            boolean precondition = (patient != null && provider != null);
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PATIENT_ID, patientId);

            HttpResponse response = appointmentServiceClient.searchAppointments(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()) {
                Map responseBody = (Map) response.getBody().get();
                List<Map> appointmentList = (List<Map>) responseBody.get(Constants.ITEMS);
                if (appointmentList.size() > 0)
                    appointment = appointmentList.get(0);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertTrue(appointmentList.size() > 0);
            }
        }

        @Test
        @Order(6)
        @DisplayName("View past encounter list with the patient")
        void provider_006a(){
            boolean precondition = (patient != null && provider != null);
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PROVIDER_ID, providerId);
            criteria.put(Constants.PATIENT_ID, patientId);
            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()) {
                Map responseBody = (Map) response.getBody().get();
                List<Map> encounterList = (List<Map>) responseBody.get(Constants.ITEMS);
                if (encounterList.size() > 0)
                    encounter = encounterList.get(0);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertTrue(encounterList.size() > 0);
            }
        }
        @Test
        @Order(7)
        @DisplayName("View selected encounter details")
        void provider_006b(){
            Assumptions.assumeTrue(encounter != null);
            String encounterId = (String) encounter.get(Constants.ENCOUNTER_ID);
            HttpResponse response = encounterServiceClient.getEncounterClient(configuration, encounterId);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(response.getBody());
        }

        @Test
        @Order(8)
        @DisplayName("View past encounter list with the patient")
        void provider_007(){
            boolean precondition = (patient != null && provider != null);
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PROVIDER_ID, providerId);
            criteria.put(Constants.PATIENT_ID, patientId);
            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()) {
                Map responseBody = (Map) response.getBody().get();
                List<Map> encounterList = (List<Map>) responseBody.get(Constants.ITEMS);
                if (encounterList.size() > 0)
                    encounter = encounterList.get(0);
                assertEquals(HttpStatus.OK, response.getStatus());
                assertNotNull(encounter.get("reasonCode"));
            }
        }


        @Test
        @Order(8)
        @DisplayName("Create new encounter by the provider with followup not requested")
        void provider_009() {
            boolean precondition = (patient != null && provider != null && appointment !=null);
            Assumptions.assumeTrue(precondition);
            Integer appointmentId = (Integer) appointment.get(Constants.ID);
            boolean followUpRequested = false;

            HttpResponse response = encounterServiceClient.
                    createEncounterWithInputs(configuration, patientId, providerId, appointmentId,followUpRequested);
            if (HttpStatus.OK == response.getStatus()) {
                encounter = (Map) response.getBody().get();
                createdEncounterList.add((String)encounter.get(Constants.ENCOUNTER_ID));
            }
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(encounter);
            assertFalse((boolean)encounter.get(Constants.FOLLOWUP_REQUESTED));

        }
        @Test
        @Order(9)
        @DisplayName("Post Encounter: update encounter with followup request and recommendation")
        void provider_010() {
            Assumptions.assumeTrue(encounter != null);
            Map<String, Object> recommendation= new HashMap<>(4);
            String providerName = provider.get("title") + Constants.SPACE + provider.get("firstName")+
                    Constants.SPACE + provider.get("middleName") +
                    Constants.SPACE + provider.get("lastName");
            String recommDateString = Utility.getDateAsString("05:00");
            recommendation.put("recommendationDate", recommDateString);
            recommendation.put("instruction", "Chest X ray to be done");
            recommendation.put("recommendedBy", providerName);
            recommendation.put("additionalInstructions", "CT scan recommended");
            encounter.replace(Constants.FOLLOWUP_REQUESTED, true);
            encounter.replace("recommendation", recommendation);

            HttpResponse response = encounterServiceClient.updateEncounterClient(configuration, encounter);
            Map reponseBody = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue((boolean)reponseBody.get(Constants.FOLLOWUP_REQUESTED));
        }

        @Test
        @Order(8)
        @DisplayName("Create new encounter by the provider with a followup request")
        void provider_011() {
            boolean precondition = (patient != null && provider != null && appointment !=null);
            Assumptions.assumeTrue(precondition);
            Integer appointmentId = (Integer) appointment.get(Constants.ID);
            boolean followUpRequested = true;

            HttpResponse response = encounterServiceClient.
                    createEncounterWithInputs(configuration, patientId, providerId, appointmentId, followUpRequested);
            Map reponseBody = (Map)response.getBody().get();
            createdEncounterList.add((String)reponseBody.get(Constants.ENCOUNTER_ID));
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue((boolean)reponseBody.get(Constants.FOLLOWUP_REQUESTED));
        }

        @Test
        @Order(10)
        @DisplayName("No appointments scheduled for the specified date")
        void provider_012() {
            Assumptions.assumeTrue(provider != null);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.START_TIME, "2022-02-15T00:00:00-00:00");
            criteria.put(Constants.END_TIME, "2022-02-15T23:59:00-00:00");
            criteria.put(Constants.PROVIDER_ID, providerId);
            HttpResponse response = appointmentServiceClient.searchAppointments(configuration, criteria);
            Map responseBody = (Map) response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue(responseBody.isEmpty());

        }

        @Test
        @Order(11)
        @DisplayName("No past encounter history with a patient for the provider")
        void provider_013(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PROVIDER_ID, 3);
            criteria.put(Constants.PATIENT_ID, 1);
            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            Map responseBody = (Map) response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue(responseBody.isEmpty());
        }
    }

    @AfterAll
    void tearDownEncounters(){
        Assumptions.assumeTrue(createdEncounterList.size() > 0);
        createdEncounterList.forEach((encounterId) -> encounterServiceClient.deleteEncounterById(configuration, encounterId));
    }
}
