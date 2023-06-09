/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.oracle.refapp.api.test.configuration.Configuration;
import com.oracle.refapp.api.test.model.Authorisation;
import com.oracle.refapp.api.test.model.Constants;
import com.oracle.refapp.api.test.model.ServiceType;
import com.oracle.refapp.api.test.util.Utility;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import java.util.Map;


@Singleton
public class EncounterServiceClient {

    @Inject
    APIClient apiClient;

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(EncounterServiceClient.class);

    public HttpResponse createEncounterWithInputs(Configuration apiClientConfiguration, Integer patientId,
                                                  Integer providerId, Integer appointmentId, boolean followUpRequested){
        HttpResponse response = null;
        try {
            File dataFile = Utility.getDataFilePath(ServiceType.ENCOUNTER);
            LOG.info("Data file path : {}" , dataFile);

            Map encounter = mapper.readValue(dataFile, Map.class);
            encounter.replace(Constants.PROVIDER_ID, providerId);
            encounter.replace(Constants.PATIENT_ID, patientId);
            encounter.replace(Constants.APPOINTMENT_ID, appointmentId);
            encounter.replace(Constants.FOLLOWUP_REQUESTED, followUpRequested);

            response =  createEncounter(apiClientConfiguration, encounter);

        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        return response;
    }

    public HttpResponse createEncounterClient(Configuration apiClientConfiguration){
        HttpResponse response = null;
        try {
            File dataFile = Utility.getDataFilePath(ServiceType.ENCOUNTER);
            LOG.info("Data file path : {}", dataFile);
            Map encounter = mapper.readValue(dataFile, Map.class);
            response = createEncounter(apiClientConfiguration, encounter);
        }catch(HttpClientResponseException e){
            LOG.error(e.getMessage());
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        return response;
    }

    public HttpResponse createEncounter(Configuration apiClientConfiguration, Map encounter){
        HttpResponse response = null;
        try {
            LOG.info("Client : create encounter");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.ENCOUNTER, apiClientConfiguration.getBaseUrl());
            serviceUrl += Constants.FORWARD_SLASH;
            LOG.info("Service URL : {}" , serviceUrl);

            response = apiClient.post(serviceUrl,encounter, Utility.getTokenWithPrefix(authorisation));

            Map body = (Map)response.getBody(Map.class).get();
            File dataFile = Utility.getDataFilePath(ServiceType.ENCOUNTER);
            mapper.writeValue(dataFile, body);
        }catch(IOException e){
            LOG.error("Error writing input data file");
        }
        LOG.info("Client : create encounter returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse getEncounterClient(Configuration apiClientConfiguration, String encounterId) {
        HttpResponse response =null;
        try{
            LOG.info("Client : get encounter");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();
            if (encounterId == null) {
                File dataFile = Utility.getDataFilePath(ServiceType.ENCOUNTER);
                LOG.info("Data file path : {}", dataFile);

                Map encounter = mapper.readValue(dataFile, Map.class);
                encounterId = (String) encounter.get(Constants.ENCOUNTER_ID);
            }
            String serviceUrl = Utility.getServiceClientUrl(ServiceType.ENCOUNTER, apiClientConfiguration.getBaseUrl());
            String url = serviceUrl + Constants.FORWARD_SLASH + encounterId;
            LOG.info("Service URL {}" , url);

            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.get(url, authHeaderVal);
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : get encounter returned with status : {}", response.getStatus());
        return response;
    }


    public HttpResponse updateEncounterClient(Configuration apiClientConfiguration, Map encounter){
        HttpResponse response =null;

        LOG.info("Client : update encounter");
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        String encounterId = (String) encounter.get(Constants.ENCOUNTER_ID);

        String serviceUrl = Utility.getServiceClientUrl(ServiceType.ENCOUNTER, apiClientConfiguration.getBaseUrl());
        String url = serviceUrl + Constants.FORWARD_SLASH + encounterId;
        LOG.info("Service URL : " + url);
        response = apiClient.put(url,encounter,Utility.getTokenWithPrefix(authorisation) );
        LOG.info("Client : update encounter returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse deleteEncounterClient(Configuration apiClientConfiguration){
        LOG.info("Client : delete encounter");
        HttpResponse response =null;
        try {
            File dataFile = Utility.getDataFilePath(ServiceType.ENCOUNTER);
            LOG.info("Data file path : {} ", dataFile);

            Map encounter = mapper.readValue(dataFile, Map.class);
            String encounterId = (String) encounter.get(Constants.ENCOUNTER_ID);
            response = deleteEncounterById(apiClientConfiguration,encounterId );
            return response;
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : delete encounter returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse deleteEncounterById(Configuration apiClientConfiguration, String encounterId){
        LOG.info("Client : delete encounter by Id");
        HttpResponse response = null;
        try {
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.ENCOUNTER, apiClientConfiguration.getBaseUrl());
            String url = serviceUrl + Constants.FORWARD_SLASH + encounterId;
            LOG.info("Service URL : {} ", url);
            response = apiClient.delete(url, Utility.getTokenWithPrefix(authorisation));
            LOG.info("Client : delete encounter returned with status : {}", response.getStatus());
        }catch (HttpClientResponseException  e){
            LOG.error(e.getMessage());
            return HttpResponse.notFound();
        }
        return response;
    }

    public HttpResponse listEncounterClient(Configuration apiClientConfiguration, Map<String, Object> criteria){

        HttpResponse response = null;
        LOG.info("Client :List encounter");
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        StringBuilder serviceUrl = new StringBuilder();
        serviceUrl.append(apiClientConfiguration.getBaseUrl()).append(ServiceType.ENCOUNTER.getName());
        String url = Utility.searchByCriteriaUrl(serviceUrl, criteria);
        LOG.info("Service URL {}", url);
        response = apiClient.get(url, Utility.getTokenWithPrefix(authorisation));
        LOG.info("Client : List Encounter returned with status code : {}", response.getStatus());

        return response;

    }

}
