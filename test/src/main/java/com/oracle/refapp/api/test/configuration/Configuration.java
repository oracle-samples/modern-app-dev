/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

package com.oracle.refapp.api.test.configuration;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.refapp.api.test.model.Authorisation;
import com.oracle.refapp.api.test.model.Constants;
import com.oracle.refapp.api.test.model.Scope;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static io.micronaut.http.HttpHeaders.USER_AGENT;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Singleton
public class Configuration {

    @Inject
    @Client("/")
    private HttpClient client;

    private String idcsUrl;
    private String scope;
    private String baseUrl;
    private String authHeaderValue;

    private Authorisation authorisation;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    public void initConfiguration(Scope serviceScope) throws IOException {

        LOG.info("Getting Authorization token for : {}", serviceScope );

        this.idcsUrl = System.getenv("IDCS_URL") + Constants.IDCS_URL_EXT;
        LOG.info("idcs Url : {}", idcsUrl );
        this.baseUrl= System.getenv("APIGW_URL") + Constants.VERSION;
        LOG.info("APIGW URL : {}", baseUrl );
        this.scope = getScope(System.getenv("APIGW_URL"), serviceScope);
        LOG.info("scope : {}", scope );

        this.authHeaderValue = getHeaderToken(serviceScope);
        this.authorisation = getToken();
    }

    public String getScope(String gatewayUri, Scope scope){
        return gatewayUri + Constants.FORWARD_SLASH + scope.getName();
    }

    public String getHeaderToken(Scope scope) throws IOException {
        String clientId = "";
        String clientSecret = "";
        String idcsSecretBundle = System.getenv("IDCS_SECRET_BUNDLE");
        Map<String,String> decodedSecret = objectMapper.readValue(Base64.getDecoder().decode(idcsSecretBundle), new TypeReference<>() {});

        switch(scope)
        {
            case PROVIDER:
                clientId = decodedSecret.get("provider-app-client-id");
                clientSecret = decodedSecret.get("provider-app-client-secret");
                break;
            case PATIENT:
                clientId = decodedSecret.get("patient-app-client-id");
                clientSecret = decodedSecret.get("patient-app-client-secret");
                break;
            case SERVICE:
                clientId = decodedSecret.get("service-app-client-id");
                clientSecret = decodedSecret.get("service-app-client-secret");
                break;
        }

        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        String clientIdEncoded =encoder.encodeToString(clientId.getBytes(StandardCharsets.UTF_8));
        String clientSecretEncoded =encoder.encodeToString(clientSecret.getBytes(StandardCharsets.UTF_8));
        String token = clientIdEncoded+ Constants.ENCODED_EQUALS+ clientSecretEncoded;
        return Constants.BASIC +Constants.SPACE + token;
    }

    public Authorisation getToken(){

        Map<String, String> body = new HashMap<String, String>();
        body.put(Constants.GRANT_TYPE, Constants.CLIENT_CREDENTIAL);
        body.put(Constants.SCOPE , this.scope);

        HttpRequest request = HttpRequest.POST(idcsUrl, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).
                header(Constants.AUTHORIZATION, this.authHeaderValue).
                header(USER_AGENT, Constants.MICRONAUT_CLIENT).accept(MediaType.ALL);
        HttpResponse<Map> response = client.toBlocking().exchange(request, Authorisation.class);
        Optional<Authorisation> auth = response.getBody(Authorisation.class);
        Authorisation access = auth.get();
        LOG.info("Authorization token received : {}", response.getStatus());
        return access;
    }
}
