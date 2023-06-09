/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.service;



import com.oracle.refapp.api.test.model.Constants;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import static io.micronaut.http.HttpHeaders.USER_AGENT;



@Singleton
public class APIClient {

    @Inject
    @Client("/")
    private HttpClient httpClient;

    private static final Logger LOG = LoggerFactory.getLogger(APIClient.class);

    public HttpResponse post(String url, Map body , String authorisationToken){

        HttpRequest<Map> request = HttpRequest.POST(url , body).
                contentType(MediaType.APPLICATION_JSON).
                header(Constants.AUTHORIZATION, authorisationToken).
                header(USER_AGENT, Constants.MICRONAUT_CLIENT).accept(MediaType.ALL);
        LOG.info("Calling Http POST");
        HttpResponse<Map> response = httpClient.toBlocking().exchange(request, Map.class);
        return response;
    }

    public HttpResponse put(String url, Map body, String authorisationToken){

        HttpRequest<Map> request = HttpRequest.PUT(url , body).
                contentType(MediaType.APPLICATION_JSON).
                header(Constants.AUTHORIZATION, authorisationToken).
                header(USER_AGENT, Constants.MICRONAUT_CLIENT).accept(MediaType.ALL);
        LOG.info("Calling Http PUT");
        HttpResponse<Map> response = httpClient.toBlocking().exchange(request, Map.class);
        return response;
    }

    public HttpResponse delete(String url,String authorisationToken ){
        HttpRequest request = HttpRequest.DELETE(url).header(Constants.AUTHORIZATION, authorisationToken).
                header(USER_AGENT, Constants.MICRONAUT_CLIENT).
                accept(MediaType.ALL);
        LOG.info("Calling Http DELETE");
        HttpResponse<Map> response = httpClient.toBlocking().exchange(request, Map.class);
        return response;
    }

    public HttpResponse get(String url,String authorisationToken){
        HttpResponse<Map> response = null;
        try {
            HttpRequest request = HttpRequest.GET(url).
                    contentType(MediaType.APPLICATION_JSON).
                    header(Constants.AUTHORIZATION, authorisationToken).
                    header(USER_AGENT, Constants.MICRONAUT_CLIENT).accept(MediaType.ALL);
            LOG.info("Calling Http GET");
            response = httpClient.toBlocking().exchange(request, Map.class);
        }catch(HttpClientResponseException e) {
            return HttpResponse.notFound();
        }
        return response;
    }
}
