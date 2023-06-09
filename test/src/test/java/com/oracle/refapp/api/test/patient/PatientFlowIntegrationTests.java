/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.patient;

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
public class PatientFlowIntegrationTests {

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

    Integer patientId;
    Integer appointmentId;
    Integer providerId;
    Map<String, Object> slot;
    Map<String, Object> patient;
    Map<String, Object> appointment;
    Map<String, Object> encounter;
    Map provider;
    Integer scheduleId;
    List<String> createdEncounterList = new ArrayList<>(1);

    private static final Logger LOG = LoggerFactory.getLogger(PatientFlowIntegrationTests.class);

    @BeforeAll

    void preconditonOne() throws IOException {
        configuration.initConfiguration(Scope.PROVIDER);
        HttpResponse getProviderResponse = providerServiceClient.getProviderClient(configuration, Constants.ID, null);
        if (HttpStatus.OK == getProviderResponse.getStatus())
            provider = (Map) getProviderResponse.getBody().get();

        HttpResponse createEncounterResponse = encounterServiceClient.createEncounterClient(configuration);
        if (HttpStatus.OK == createEncounterResponse.getStatus()) {
            Map<String, Object> encounter = (Map) createEncounterResponse.getBody().get();
            createdEncounterList.add((String)encounter.get(Constants.ENCOUNTER_ID));
        }
    }

    @BeforeAll
    void preconditonTwo() throws IOException {
        LOG.info("Running configuration initialization for scope : patient" );
        configuration.initConfiguration(Scope.PATIENT);
        LOG.info("Init complete : patient" );
        Assumptions.assumeTrue(configuration.getAuthorisation() != null);
        HttpResponse response = patientServiceClient.getPatientClient(configuration, Constants.ID);
        if (HttpStatus.OK == response.getStatus()){
            Map patient = (Map)response.getBody().get();
            patientId = (Integer)patient.get(Constants.ID);
        }
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Nested
    @DisplayName("Search provider by specific criteria")
    class testSearchProvider{

        @Test
        @DisplayName("List provider by city")
        void patient_001(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.CITY,Constants.SEATTLE );
            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            List<Map<String, Object>> providerArray = null;
            Map<String, Object> provider = null;
            Map responseBody = (Map)response.getBody().get();
            if(responseBody != null)
                providerArray = (List<Map<String, Object>>) responseBody.get(Constants.ITEMS);
            if(providerArray.size() > 0)
                provider = providerArray.get(0);

            String city = (String)provider.get(Constants.CITY);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(Constants.SEATTLE, city);
        }

        @Test
        @DisplayName("Get provider by name")
        void patient_002(){
            Assumptions.assumeTrue(provider != null);
            String providerUserName = (String) provider.get(Constants.USERNAME);
            HttpResponse response = providerServiceClient.getProviderClient(configuration, Constants.NAME, providerUserName);
            assertEquals(HttpStatus.OK, response.getStatus());
            provider = (Map) response.getBody().get();
            String userName = (String) provider.get(Constants.USERNAME);
            assertEquals(providerUserName, userName);
        }

        @Test
        @DisplayName("List provider by Speciality")
        void patient_003(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.SPECIALITY,Constants.OPHTHALMOLOGIST );
            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            List<Map<String, Object>> providerArray = null;
            Map<String, Object> provider = null;
            Map responseBody = (Map)response.getBody().get();
            if(responseBody != null)
                providerArray = (List<Map<String, Object>>) responseBody.get(Constants.ITEMS);
            if(providerArray.size() > 0)
                provider = providerArray.get(0);

            String speciality = (String)provider.get(Constants.SPECIALITY);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(Constants.OPHTHALMOLOGIST, speciality);
        }

        @Test
        @DisplayName("Get provider by Id")
        void patient_004(){
            HttpResponse response = providerServiceClient.getProviderClient(configuration, Constants.ID, null);
            Map provider = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(provider);
        }

        @Test
        @DisplayName("List provider by city with no physician")
        void patient_019(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.CITY,Constants.HOUSTON );
            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            Map provider = (Map)response.getBody().get();
            assertTrue(provider.isEmpty());
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        @DisplayName("List provider by Speciality with no results")
        void patient_021(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.SPECIALITY,Constants.NEUROLOGIST );
            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            Map provider = (Map)response.getBody().get();
            assertTrue(provider.isEmpty());
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        @DisplayName("Get provider by invalid Id")
        void patient_022(){
            HttpResponse response = providerServiceClient.getProviderClient(configuration, Constants.ID, 6);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        }

        @Test
        @DisplayName("List provider by Speciality and city with no results")
        void patient_023(){
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.SPECIALITY,Constants.NEUROLOGIST);
            criteria.put(Constants.CITY,Constants.HOUSTON);
            criteria.put(Constants.LIMIT, 10);
            criteria.put(Constants.PAGE, 0);

            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            Map provider = (Map)response.getBody().get();
            assertTrue(provider.isEmpty());
            assertEquals(HttpStatus.OK, response.getStatus());

        }
    }

    @Nested
    @DisplayName("Book appointment by patient")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class testBookAppointmentByPatient{

        @Test
        @Order(1)
        @DisplayName("List provider by Speciality and city")
        void patient_005(){
            boolean isPatientSelected = patientId != null;
            Assumptions.assumeTrue(isPatientSelected);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.SPECIALITY,Constants.PHYSICIAN);
            criteria.put(Constants.CITY,Constants.SEATTLE);
            criteria.put(Constants.LIMIT, 10);
            criteria.put(Constants.PAGE, 0);

            HttpResponse response = providerServiceClient.listProviderByCriteria(configuration, criteria);
            if (HttpStatus.OK == response.getStatus()){
                List<Map<String, Object>> providerArray = null;
                Map<String, Object> provider = null;
                Map responseBody = (Map)response.getBody().get();
                if(responseBody != null)
                    providerArray = (List<Map<String, Object>>) responseBody.get(Constants.ITEMS);
                if(providerArray.size() > 0)
                    provider = providerArray.get(0);
                providerId = (Integer) provider.get(Constants.ID);

            }
            assertEquals(HttpStatus.OK, response.getStatus());
        }
        @Test
        @Order(2)
        @DisplayName("List slots for the specified provider")
        void patient_006() {
            boolean isProviderSelected = providerId != null;
            Assumptions.assumeTrue(isProviderSelected);
            Map<String, Object> criteria = new HashMap<>(1);
            String startDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_START_TIME_1);
            String endDateTime = Utility.getDateAsString(Constants.TEST_SCHEDULE_END_TIME_1);
            criteria.put(Constants.START_TIME, startDateTime);
            criteria.put(Constants.END_TIME, endDateTime);
            criteria.put(Constants.LIMIT, 20);
            HttpResponse response = providerServiceClient.listSlotsByCriteria(configuration, criteria, providerId);
            if (HttpStatus.OK == response.getStatus()) {
                List<Map<String, Object>> slotArray = null;
                Map responseBody = (Map) response.getBody().get();
                if (responseBody != null)
                    slotArray = (List<Map<String, Object>>) responseBody.get(Constants.ITEMS);
                LOG.info("slotArray : {} :", slotArray);

                for(Map<String, Object> slotObj : slotArray){
                    String status = (String)slotObj.get(Constants.STATUS);
                    if (Constants.AVAILABLE.equalsIgnoreCase(status)) {
                        slot = slotObj;
                        break;
                    }
                }
                assertEquals(HttpStatus.OK, response.getStatus());
                assertNotNull(slot);
            }
        }
        @Test
        @Order(3)
        @DisplayName("Book Appointment for the selected slot")
        void patient_007() {
            boolean isSlotSelected = slot != null;
            Assumptions.assumeTrue(isSlotSelected);
            Map<String, Object> slotDetails = new HashMap<>();
            slotDetails.put(Constants.PATIENT_ID, patientId);
            slotDetails.put(Constants.PROVIDER_ID, providerId);
            slotDetails.put(Constants.START_TIME, slot.get(Constants.START_TIME));
            slotDetails.put(Constants.END_TIME, slot.get(Constants.END_TIME));
            slotDetails.put(Constants.PRESCRIPTION, "testprescription");
            Map<String, String> conditionMap = new HashMap<>(1);
            conditionMap.put(Constants.CONDITION, "Common cold");
            slotDetails.put(Constants.PRE_VISIT_DATA, conditionMap);
            String status = "";
            HttpResponse response = appointmentServiceClient.createAppointmentClient(configuration, slotDetails);
            if (HttpStatus.OK == response.getStatus()) {
                appointment = (Map) response.getBody().get();
                appointmentId =(Integer) appointment.get(Constants.ID);
                status = (String)appointment.get(Constants.STATUS);
            }
            assertEquals(Constants.CONFIRMED, status);
        }


        @Test
        @Order(4)
        @DisplayName("Search appointments booked by the patient")
        void patient_012(){
            Assumptions.assumeTrue(patientId != null);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PATIENT_ID, patientId );
            HttpResponse response = appointmentServiceClient.searchAppointments(configuration, criteria);
            Map responseBody = (Map)response.getBody().get();
            List<Map> appointmentList = (List<Map>)responseBody.get(Constants.ITEMS);
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue(appointmentList.size() > 0);
        }

        @Test
        @Order(5)
        @DisplayName("View selected appointment with physician details by the patient")
        void patient_013(){
            Assumptions.assumeTrue(appointment != null);
            HttpResponse response = providerServiceClient.getProviderClient(configuration, Constants.ID, appointment.get(Constants.PROVIDER_ID) );
            Map provider = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(provider);
        }

        @Test
        @Order(6)
        @DisplayName("View selected appointment with pre visit details by the patient")
        void patient_014(){
            Assumptions.assumeTrue(appointment != null);
            HttpResponse response = appointmentServiceClient.getAppointmentClient(configuration, appointmentId );
            Map appointment = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            Map<String, String> preVisitData = (Map)appointment.get(Constants.PRE_VISIT_DATA);
            assertNotNull(preVisitData);
        }

        @Test
        @Order(7)
        @DisplayName("Not slots available for the specified provider")
        void patient_024() {
            boolean isProviderSelected = providerId != null;
            Assumptions.assumeTrue(isProviderSelected);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.START_TIME, "2022-03-07T09:00:00-00:00");
            criteria.put(Constants.END_TIME, "2022-03-07T11:00:00-00:00");
            criteria.put(Constants.LIMIT, 10);
            HttpResponse response = providerServiceClient.listSlotsByCriteria(configuration, criteria, providerId);
            Map responseBody = (Map) response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue(responseBody.isEmpty());
        }
    }

    @Nested
    @DisplayName("Update profile information by patient")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class testUpdateProfileByPatient{

        @Test
        @Order(1)
        @DisplayName("get profile by patient")
        void patient_000(){
            HttpResponse response = patientServiceClient.getPatientClient(configuration, Constants.ID);
            Assumptions.assumeTrue(HttpStatus.OK == response.getStatus());
            patient = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("Update Phone number/address by the patient")
        void patient_009(){
            Assumptions.assumeTrue(patient != null);

            String zip = "21202";
            String city = "MARYLAND";
            String country = "USA";

            patient.replace(Constants.ZIP, zip);
            patient.replace(Constants.CITY, city);
            patient.replace(Constants.COUNTRY, country);

            HttpResponse response = patientServiceClient.updatePatientClient(configuration, patient, (Integer)patient.get("id"));
            Map updatedPatient = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(zip, updatedPatient.get(Constants.ZIP));
            assertEquals(city, updatedPatient.get(Constants.CITY));
            assertEquals(country, updatedPatient.get(Constants.COUNTRY));
        }
        //Disabled: Update API for patient is not enabled
        @Test
        @Disabled
        @Order(3)
        @DisplayName("Update Insurance Provider by the patient")
        void patient_010(){
            Assumptions.assumeTrue(patient != null);
            String insuranceProvider = "United Health Care";
            patient.replace(Constants.INSURANCE_PROVIDER, insuranceProvider);
            HttpResponse response = patientServiceClient.updatePatientClient(configuration, patient, (Integer)patient.get("id"));
            Map updatedPatient = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(insuranceProvider, updatedPatient.get(Constants.INSURANCE_PROVIDER));
        }

        @Test
        @Order(4)
        @DisplayName("Update Primary healthcare Provider by the patient")
        void patient_011(){
            Assumptions.assumeTrue(patient != null);
            Map primaryCareProvider = (Map)patient.get(Constants.PRIMARY_CARE_PROVIDER);
            primaryCareProvider.replace(Constants.PROFESSIONAL_SUMMARY,  Constants.PHYSICIAN);
            HttpResponse response = patientServiceClient.updatePatientPrimaryProviderClient(configuration, patient);
            Map updatedPatient = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(updatedPatient);
        }
    }

    @Nested
    @DisplayName("View Encounter information by patient")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class testViewEncountersByPatient{

        @Test
        @Order(1)
        @DisplayName("List all encounters by the patient")
        void patient_015(){
            boolean precondition = patientId != null;
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PATIENT_ID, patientId );

            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            Map responseBody = (Map)response.getBody().get();
            List<Map> encounterList = (List<Map>)responseBody.get(Constants.ITEMS);
            if(encounterList.size() > 0)
                encounter = encounterList.get(0);
            assertEquals(HttpStatus.OK, response.getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("View details of the selected encounter ")
        void patient_016(){
            Assumptions.assumeTrue(encounter != null);
            HttpResponse response = encounterServiceClient.getEncounterClient(configuration, (String)encounter.get(Constants.ENCOUNTER_ID));
            Map provider = (Map)response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(provider);
        }

        @Test
        @Order(3)
        @DisplayName("No list of encounters returned for specific patient and a provider")
        void patient_025(){
            boolean precondition = patientId != null;
            Assumptions.assumeTrue(precondition);
            Map<String, Object> criteria = new HashMap<>(1);
            criteria.put(Constants.PATIENT_ID, patientId );
            criteria.put(Constants.PROVIDER_ID, 3 );

            HttpResponse response = encounterServiceClient.listEncounterClient(configuration, criteria);
            Map responseBody = (Map) response.getBody().get();
            assertEquals(HttpStatus.OK, response.getStatus());
            assertTrue(responseBody.isEmpty());
        }
    }

    @AfterAll
    void tearDownSchedule() throws IOException {
        Assumptions.assumeTrue(createdEncounterList.size() > 0);
        createdEncounterList.forEach((encounterId) -> encounterServiceClient.deleteEncounterById(configuration, encounterId));
    }
}
