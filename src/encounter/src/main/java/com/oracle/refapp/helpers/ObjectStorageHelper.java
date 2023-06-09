/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.helpers;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.oracle.refapp.connections.ObjectStorageConnection;
import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.model.Encounter;
import com.oracle.refapp.model.Recommendation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ObjectStorageHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectStorageHelper.class);

  private final ObjectStorageConnection objectStorage;

  public ObjectStorageHelper(ObjectStorageConnection objectStorage) {
    this.objectStorage = objectStorage;
  }

  public void upload(Encounter encounter) throws EncounterServiceException {
    ObjectStorageClient client = objectStorage.getClient();
    String osNamespace = objectStorage.getNameSpace();
    String filePath = encounter.getEncounterId() + "_" + encounter.getPatientId() + ".txt";

    try {
      PutObjectResponse putObjectResponse = putObjectToBucket(
        client,
        osNamespace,
        encounter.getRecommendation(),
        filePath
      );
      int responseCode = putObjectResponse.get__httpStatusCode__();
      if (responseCode != 200) {
        throw new EncounterServiceException("Received non 200 response from object storage." + putObjectResponse);
      }
    } catch (IOException e) {
      throw new EncounterServiceException("Error saving recommendation to object storage." + e);
    }
  }

  public PutObjectResponse putObjectToBucket(
    ObjectStorageClient osClient,
    String osNamespace,
    Recommendation recommendation,
    String objectName
  ) throws IOException {
    PutObjectResponse putObjectResponse;
    String osPathToObject = "/n/" + osNamespace + "/b/" + objectStorage.getBucket() + "/o/" + objectName;
    LOG.info("Uploading the recommendation as {}", osPathToObject);
    try (InputStream content = generatePrescriptionContent(recommendation); content) {
      PutObjectRequest putObjectRequest = PutObjectRequest
        .builder()
        .namespaceName(osNamespace)
        .bucketName(objectStorage.getBucket())
        .objectName(objectName)
        .putObjectBody(content)
        .build();
      putObjectResponse = osClient.putObject(putObjectRequest);
    }
    return putObjectResponse;
  }

  private InputStream generatePrescriptionContent(Recommendation recommendation) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.dd.MM");
    Date date = new Date();
    String data = String.format(
      "United Healthcare Organisation %n %n Date Generated: %s%n%n" +
      "Recommendation Date: %s%n" +
      "Recommended By: %s%n" +
      "Instructions: %s%n" +
      "Additional Instructions: %s",
      dateFormat.format(date),
      recommendation.getRecommendationDate(),
      recommendation.getRecommendedBy(),
      recommendation.getInstruction(),
      recommendation.getAdditionalInstructions()
    );

    return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
  }
}
