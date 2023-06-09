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
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PatientServiceClient {

    @Inject
    APIClient apiClient;

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(PatientServiceClient.class);

    public HttpResponse createPatientClient(Configuration apiClientConfiguration){
        HttpResponse response = null;
        try {
            LOG.info("Client : create patient");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.PATIENT, apiClientConfiguration.getBaseUrl());
            LOG.info("Service URL : {}" , serviceUrl);

            File dataFile = Utility.getDataFilePath(ServiceType.PATIENT);
            LOG.info("Data file path : {}" , dataFile);

            Map patient = mapper.readValue(dataFile, Map.class);

            response = apiClient.post(serviceUrl, patient, Utility.getTokenWithPrefix(authorisation));
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : create patient returned with status : {}", response.getStatus());
        return response;
    }


    public HttpResponse getPatientByTypeClient(Configuration apiClientConfiguration, String paramType,Object paramVal) {
        HttpResponse response = null;

            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = null;


            if (Constants.ID.equalsIgnoreCase(paramType)){
                serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration,ServiceType.PATIENT ,(Integer)paramVal);
            }else if(Constants.USERNAME.equalsIgnoreCase(paramType)){
                serviceUrl = Utility.getEntityByUsernameUrl(apiClientConfiguration,ServiceType.PATIENT,(String)paramVal);
            }
            LOG.info("Service URL {}", serviceUrl);
            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.get(serviceUrl, authHeaderVal);
            LOG.info("Client : get patient returned with status : {}", response.getStatus());

        return response;
    }

    public HttpResponse getPatientClient(Configuration apiClientConfiguration, String param) {
        HttpResponse response = null;
        try {
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();
            File dataFile = Utility.getDataFilePath(ServiceType.PATIENT);
            LOG.info("Data file path : {}", dataFile);
            String serviceUrl = null;
            Map patient = mapper.readValue(dataFile, Map.class);

            if (Constants.ID.equalsIgnoreCase(param)){
                serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration,ServiceType.PATIENT ,(Integer)patient.get(Constants.ID));
            }else if(Constants.USERNAME.equalsIgnoreCase(param)){
                serviceUrl = Utility.getEntityByUsernameUrl(apiClientConfiguration,ServiceType.PATIENT,(String)patient.get(Constants.NAME));
            }
            LOG.info("Service URL {}", serviceUrl);
            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.get(serviceUrl, authHeaderVal);
            LOG.info("Client : get patient returned with status : {}", response.getStatus());
        } catch (IOException e) {
            LOG.error("Error reading input data file");
        }
        return response;
    }

    public HttpResponse deletePatientClient(Configuration apiClientConfiguration) {
        LOG.info("Client : delete patient");
        HttpResponse response = null;
        try {
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            File dataFile = Utility.getDataFilePath(ServiceType.PATIENT);
            LOG.info("Data file path : {} ", dataFile);

            Map patient = mapper.readValue(dataFile, Map.class);
            String serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration, ServiceType.PATIENT,(Integer) patient.get(Constants.ID));
            LOG.info("Service URL : {}", serviceUrl);
            response = apiClient.delete(serviceUrl, Utility.getTokenWithPrefix(authorisation));
        } catch (IOException e) {
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : delete patient returned with status : {}", response.getStatus());
        return response;
    }


    public HttpResponse updatePatientPrimaryProviderClient(Configuration apiClientConfiguration, Map<String, Object> patient){
        HttpResponse reponse = null;
        try{

            Authorisation authorisation = apiClientConfiguration.getAuthorisation();
            File dataFile = Utility.getDataFilePath(ServiceType.PROVIDER);
            LOG.info("Data file path : {} ", dataFile);

            Map provider = mapper.readValue(dataFile, Map.class);
            Map<String, Object> providerMap = new HashMap<>();
            providerMap.put("username", (String)provider.get("username"));
            providerMap.put("firstName", (String)provider.get("firstName"));
            providerMap.put("middleName", (String)provider.get("middleName"));
            providerMap.put("lastName", (String)provider.get("lastName"));

            providerMap.put("title", (String)provider.get("title"));
            providerMap.put("phone", (String)provider.get("phone"));
            providerMap.put("email", (String)provider.get("email"));
            providerMap.put("gender", (String)provider.get("gender"));

            providerMap.put("zip", (String)provider.get("zip"));
            providerMap.put("country", (String)provider.get("country"));
            providerMap.put("city", (String)provider.get("city"));
            providerMap.put("speciality", (String)provider.get("speciality"));

            providerMap.put("qualification", (String)provider.get("qualification"));
            providerMap.put("designation", (String)provider.get("designation"));
            providerMap.put("professionalSummary", (String)provider.get("professionalSummary"));
            providerMap.put("interests", (String)provider.get("interests"));

            providerMap.put("expertise", (String)provider.get("expertise"));
            providerMap.put("hospitalName", (String)provider.get("hospitalName"));
            providerMap.put("hospitalAddress", (String)provider.get("hospitalAddress"));
            providerMap.put("hospitalPhone", (String)provider.get("hospitalPhone"));

            patient.put("primaryCareProvider", providerMap);

            reponse = updatePatientClient(apiClientConfiguration, patient, (Integer)patient.get("id"));

        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        return reponse;
    }

    public HttpResponse updatePatientClient(Configuration apiClientConfiguration, Map<String, Object> patient, Integer patienId ) {
        HttpResponse response = null;

            LOG.info("Client : update patient");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();
            String serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration, ServiceType.PATIENT, patienId);
            LOG.info("Service URL : {}", serviceUrl);

            response = apiClient.put(serviceUrl, patient, Utility.getTokenWithPrefix(authorisation));
            LOG.info("Client : update patient returned with status : {}", response.getStatus());
        return response;
    }


}
