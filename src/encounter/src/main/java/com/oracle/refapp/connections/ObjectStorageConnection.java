/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.connections;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.refapp.exceptions.ObjectStorageConnectionException;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.ReflectiveAccess;
import java.io.IOException;
import javax.inject.Singleton;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Data
public class ObjectStorageConnection {

  @Value("${micronaut.objectstorage.oraclecloud.bucketName}")
  @ReflectiveAccess
  private String bucket;

  @Value("${micronaut.objectstorage.oraclecloud.compartmentId}")
  @ReflectiveAccess
  private String compartmentId;

  private String nameSpace;

  private ObjectStorageClient client;

  private static final Logger LOG = LoggerFactory.getLogger(ObjectStorageConnection.class);

  public ObjectStorageConnection(Environment environment) throws ObjectStorageConnectionException {
    try {
      LOG.info("Getting object storage client");
      if (environment.getActiveNames().contains("dev") || environment.getActiveNames().contains("test")) {
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
          configFile
        );
        this.client = new ObjectStorageClient(provider);
      } else {
        InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider
          .builder()
          .build();
        this.client = new ObjectStorageClient(provider);
      }
      this.nameSpace = getObjectStorageNamespace(client, compartmentId);
      LOG.info("Endpoint : {}", client.getEndpoint());
    } catch (IOException e) {
      LOG.error("Unable to get config information for object storage : {}", e.getMessage());
      throw new ObjectStorageConnectionException("Error reading Object storage configuration");
    }
  }

  private String getObjectStorageNamespace(ObjectStorageClient osClient, String compId) {
    LOG.info("Querying for Object Storage namespace");
    // Get Object Storage Namespace
    // https://docs.oracle.com/en-us/iaas/api/#/en/objectstorage/20160918/Namespace/GetNamespace
    var getNamespaceRequest = GetNamespaceRequest.builder().compartmentId(compId).build();
    GetNamespaceResponse getNamespaceResponse = osClient.getNamespace(getNamespaceRequest);
    var osNamespace = getNamespaceResponse.getValue();
    LOG.info("Namespace: {}", osNamespace);
    return osNamespace;
  }
}
