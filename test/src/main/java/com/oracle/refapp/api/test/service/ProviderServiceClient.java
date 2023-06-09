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
import io.micronaut.http.uri.UriBuilder;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

public class ProviderServiceClient {

    @Inject
    APIClient apiClient;

    ObjectMapper mapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(ProviderServiceClient.class);

    public HttpResponse createProviderClient(Configuration apiClientConfiguration) {
        HttpResponse response = null;
        try {
            LOG.info("Client : create provider");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            File dataFile = Utility.getDataFilePath(ServiceType.PROVIDER);
            LOG.info("Data file path : {}", dataFile);
            Map provider = mapper.readValue(dataFile, Map.class);

            String serviceUrl = Utility.getServiceClientUrl(ServiceType.PROVIDER, apiClientConfiguration.getBaseUrl());
            LOG.info("Service URL {}", serviceUrl);

            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.post(serviceUrl,provider,authHeaderVal);
        } catch (IOException e) {
            LOG.error("Error reading input data file");
        }
        LOG.info("Client : create provider returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse listProviderByCriteria(Configuration apiClientConfiguration, Map<String, Object> criteria){

        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        String authHeaderVal = Utility.getTokenWithPrefix(authorisation);

        StringBuilder serviceUrl = new StringBuilder();
        serviceUrl.append(apiClientConfiguration.getBaseUrl()).append(ServiceType.PROVIDER.getName());
        String url = Utility.searchByCriteriaUrl(serviceUrl, criteria);
        LOG.info("Service URL {}", url);
        HttpResponse response = apiClient.get(url, authHeaderVal);
        LOG.info("Client : get provider returned with status : {}", response.getStatus());
    return response;
    }

    public HttpResponse listSlotsByCriteria(Configuration apiClientConfiguration, Map<String, Object> criteria, Integer providerId)  {
        HttpResponse response = null;
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
        try {
            StringBuilder serviceUrl = new StringBuilder();
            serviceUrl.append(apiClientConfiguration.getBaseUrl()).append(ServiceType.PROVIDER.getName());

            UriBuilder builder = UriBuilder.of(serviceUrl);
            builder.path(String.valueOf(providerId)).path(Constants.SLOTS);
            for( String key : criteria.keySet()){
                builder.queryParam(key, criteria.get(key));
            }
            URL url = new URL(URLDecoder.decode(builder.build().toString(), "UTF-8"));
            LOG.info("Service URL {}", url);
            response = apiClient.get(url.toString(), authHeaderVal);
            LOG.info("Client : get provider returned with status : {}", response.getStatus());
        }catch(UnsupportedEncodingException | MalformedURLException e){
            LOG.error("Invalid API endpoint url : " + e.getMessage());
            return HttpResponse.badRequest();
        }
        return response;
    }

    public HttpResponse getProviderClient(Configuration apiClientConfiguration, String paramType , String paramValue){
        HttpResponse response = null;
        try {
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();

            File dataFile = Utility.getDataFilePath(ServiceType.PROVIDER);
            LOG.info("Data file path : {}", dataFile);
            Map provider = mapper.readValue(dataFile, Map.class);

            String serviceUrl = null;
            if (Constants.ID.equalsIgnoreCase(paramType)){
                serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration,ServiceType.PROVIDER ,(Integer)provider.get(Constants.ID));
            }else if(Constants.NAME.equalsIgnoreCase(paramType)){
                if (paramValue == null)
                    paramValue =  (String)provider.get(Constants.USERNAME);
                serviceUrl = Utility.getEntityByUsernameUrl(apiClientConfiguration,ServiceType.PROVIDER,paramValue);
            }
            LOG.info("Service URL {}", serviceUrl);
            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.get(serviceUrl, authHeaderVal);
            LOG.info("Client : get provider returned with status : {}", response.getStatus());
        } catch (IOException e) {
            LOG.error("Error reading input data file");
        }
        return response;
    }

    public HttpResponse getProviderClient(Configuration apiClientConfiguration, String paramType , Object paramValue){
        HttpResponse response = null;
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();
        String serviceUrl = null;
        if (Constants.ID.equalsIgnoreCase(paramType)){
            serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration,ServiceType.PROVIDER ,(Integer)paramValue);
        }else if(Constants.NAME.equalsIgnoreCase(paramType)){
            serviceUrl = Utility.getEntityByUsernameUrl(apiClientConfiguration,ServiceType.PROVIDER,(String)paramValue);
        }
        LOG.info("Service URL {}", serviceUrl);
        String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
        response = apiClient.get(serviceUrl, authHeaderVal);
        LOG.info("Client : get provider returned with status : {}", response.getStatus());
        return response;
    }

    public HttpResponse addScheduleClient(Configuration apiClientConfiguration, Map<String, Object> schedule, Integer providerId){
        HttpResponse response = null;
        try {
            LOG.info("Client : Add schedule for the provider");
            Authorisation authorisation = apiClientConfiguration.getAuthorisation();
            String serviceUrl = null;
            serviceUrl = Utility.getEntityByIdClient(apiClientConfiguration, ServiceType.PROVIDER, providerId);
            serviceUrl += Constants.FORWARD_SLASH + Constants.SCHEDULES;
            LOG.info("Service URL {}", serviceUrl);
            String authHeaderVal = Utility.getTokenWithPrefix(authorisation);
            response = apiClient.post(serviceUrl, schedule, authHeaderVal);
            LOG.info("Client : Add schedule for the provider : {}", response.getStatus());
        }catch(HttpClientResponseException e){
            LOG.error(e.getMessage());
            return HttpResponse.notAllowed();
        }
        return response;
    }

    public HttpResponse deleteScheduleById(Configuration apiClientConfiguration, Integer providerId ,Integer scheduleId){
        LOG.info("Client : delete schedule by Id");
        HttpResponse response = null;
        try {
        Authorisation authorisation = apiClientConfiguration.getAuthorisation();

        String serviceUrl = Utility.getServiceClientUrl(ServiceType.PROVIDER, apiClientConfiguration.getBaseUrl());
        String url = serviceUrl + Constants.FORWARD_SLASH + providerId +
                Constants.FORWARD_SLASH + Constants.SCHEDULES + Constants.FORWARD_SLASH +scheduleId;
        LOG.info("Service URL : {} ", url);
        response = apiClient.delete(url,Utility.getTokenWithPrefix(authorisation));
        LOG.info("Client : delete schedule returned with status : {}", response.getStatus());
        return response;
        }catch(HttpClientResponseException e){
            LOG.error(e.getMessage());
            return HttpResponse.notFound();
        }
    }

}
