/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.util;


import com.oracle.refapp.api.test.configuration.Configuration;
import com.oracle.refapp.api.test.model.Authorisation;
import com.oracle.refapp.api.test.model.Constants;
import com.oracle.refapp.api.test.model.ServiceType;
import io.micronaut.http.uri.UriBuilder;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class Utility {

    public static String getServiceClientUrl(ServiceType serviceName, String baseUrl) {
        String url = baseUrl;
        switch (serviceName) {
            case ENCOUNTER:
                url += ServiceType.ENCOUNTER.getName();
                break;
            case APPOINTMENT:
                url += ServiceType.APPOINTMENT.getName();
                break;
            case PATIENT:
                url += ServiceType.PATIENT.getName();
                break;
            case PROVIDER:
                url += ServiceType.PROVIDER.getName();
                break;
        }
        return url;
    }

    public static String getTokenWithPrefix(Authorisation authorisation){
        return authorisation.getToken_type() + " " + authorisation.getAccess_token();
    }

    public static File getDataFilePath(ServiceType serviceName){
        String filePath = "";
        switch (serviceName) {
            case ENCOUNTER:
                filePath += Constants.DATA_FILE_PATH + ServiceType.ENCOUNTER.getName() + Constants.DATA_FILE_EXT;
                break;
            case APPOINTMENT:
                filePath += Constants.DATA_FILE_PATH + ServiceType.APPOINTMENT.getName() + Constants.DATA_FILE_EXT;
                break;
            case PATIENT:
                filePath += Constants.DATA_FILE_PATH + ServiceType.PATIENT.getName() + Constants.DATA_FILE_EXT;
                break;
            case PROVIDER:
                filePath += Constants.DATA_FILE_PATH + ServiceType.PROVIDER.getName() + Constants.DATA_FILE_EXT;
                break;
        }
        return Paths.get(filePath).toFile();
    }

    public static String getEntityByUsernameUrl(Configuration apiClientConfiguration, ServiceType type, String username){
        String baseUrl = Utility.getServiceClientUrl(type, apiClientConfiguration.getBaseUrl());
        String url = baseUrl + Constants.FORWARD_SLASH + Constants.USERNAME + Constants.FORWARD_SLASH + username;
        return url;
    }

    public static String getEntityByIdClient(Configuration apiClientConfiguration , ServiceType type, Integer id){
        String baseUrl = Utility.getServiceClientUrl(type, apiClientConfiguration.getBaseUrl());
        String url = baseUrl + Constants.FORWARD_SLASH + id;
        return url;
    }

    public static String searchByCriteriaUrl(StringBuilder serviceUrl, Map<String, Object> criteria){
        UriBuilder builder = UriBuilder.of(serviceUrl);
        builder.path(Constants.ACTIONS).path(Constants.SEARCH);
        for( String key : criteria.keySet()){
            builder.queryParam(key, criteria.get(key));
        }
        builder.build();
    return builder.toString();

    }

    public static String getDateAsString(String timeString){
        Date today= new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = formatter.format(today);
        StringBuilder currentDate = new StringBuilder();
        currentDate.append(date).append("T").append(timeString).append(":00-00:00");
        return currentDate.toString();
    }

    public static Map<String, Object> getSlot(Map getSlotResponse){
        List<Map<String, Object>> slotArray = null;
        Map<String, Object> slot = null;
        slotArray = (List<Map<String, Object>>) getSlotResponse.get(Constants.ITEMS);
        for(Map<String, Object> slotObj : slotArray) {
            String status = (String) slotObj.get(Constants.STATUS);
            if (Constants.AVAILABLE.equalsIgnoreCase(status)) {
                slot = slotObj;
                break;
            }
        }
        return slot;
    }
}
