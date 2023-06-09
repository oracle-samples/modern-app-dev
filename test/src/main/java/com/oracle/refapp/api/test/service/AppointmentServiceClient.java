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
import java.util.Map;

public class AppointmentServiceClient {

    @Inject
    APIClient apiClient;

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(PatientServiceClient.class);


    public HttpResponse createAppointmentClient(Configuration apiClientConfiguration) {
        HttpResponse response = null;
        try {
        LOG.info("Client : create appointment");
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();

        String serviceUrl = Utility.getServiceClientUrl(ServiceType.APPOINTMENT, apiClientConfiguration.getBaseUrl());
        serviceUrl += Constants.FORWARD_SLASH;
        LOG.info("Service URL {}", serviceUrl);

        File dataFile = Utility.getDataFilePath(ServiceType.APPOINTMENT);
        LOG.info("Data file path : {}" , dataFile);

        Map appointment = mapper.readValue(dataFile, Map.class);

        String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
        response = apiClient.post(serviceUrl,appointment, authHeaderVal);
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : create appointment returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse createAppointmentClient(Configuration apiClientConfiguration, Map<String, Object> appointment) {
        HttpResponse response = null;

            LOG.info("Client : create appointment");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.APPOINTMENT, apiClientConfiguration.getBaseUrl());
            serviceUrl += Constants.FORWARD_SLASH;
            LOG.info("Service URL {}", serviceUrl);

            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.post(serviceUrl, appointment, authHeaderVal);

        LOG.info("Client : create appointment returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse searchAppointments(Configuration apiClientConfiguration,Map<String, Object> criteria){
        HttpResponse response = null;
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
        StringBuilder serviceUrl = new StringBuilder();
        serviceUrl.append(apiClientConfiguration.getBaseUrl()).append(ServiceType.APPOINTMENT.getName());
        String url = Utility.searchByCriteriaUrl(serviceUrl, criteria);
        LOG.info("Service URL {}", url);
        response = apiClient.get(url, authHeaderVal);
        LOG.info("Client : get provider returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse getAppointmentClient(Configuration apiClientConfiguration) {
        HttpResponse response = null;
        try {
            LOG.info("Client : get appointment");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            File dataFile = Utility.getDataFilePath(ServiceType.APPOINTMENT);
            LOG.info("Data file path : {}", dataFile);

            Map appointment = mapper.readValue(dataFile, Map.class);
            Integer appointmentId = (Integer)appointment.get(Constants.ID);
            response = getAppointmentClient(apiClientConfiguration, appointmentId);
        }catch(IOException e){
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : get appointment returned with status : {}", response.getStatus());
        return response;
    }


    public HttpResponse getAppointmentClient(Configuration apiClientConfiguration, Integer appointmentId) {
        HttpResponse response = null;

            LOG.info("Client : get appointment");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.APPOINTMENT, apiClientConfiguration.getBaseUrl());
            String url = serviceUrl + Constants.FORWARD_SLASH + appointmentId;
            LOG.info("Service URL {}", url);

            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.get(url, authHeaderVal);
        LOG.info("Client : get appointment returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse deleteAppointmentById(Configuration apiClientConfiguration, Integer appointmentId){
        LOG.info("Client : delete appointment by Id");
        HttpResponse response = null;
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();

        String serviceUrl = Utility.getServiceClientUrl(ServiceType.APPOINTMENT, apiClientConfiguration.getBaseUrl());
        String url = serviceUrl + Constants.FORWARD_SLASH + appointmentId;
        LOG.info("Service URL : {} ", url);
        response = apiClient.delete(url,Utility.getTokenWithPrefix(authorisation));
        LOG.info("Client : delete appointment returned with status : {}", response.getStatus());
        return response;
    }
}
