/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.helpers;

import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.model.Encounter;
import com.oracle.refapp.model.Recommendation;
import io.micronaut.objectstorage.ObjectStorageException;
import io.micronaut.objectstorage.ObjectStorageOperations;
import io.micronaut.objectstorage.request.UploadRequest;
import jakarta.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ObjectStorageHelper {

  private static final Logger LOG = LoggerFactory.getLogger(ObjectStorageHelper.class);

  private final ObjectStorageOperations<?, ?, ?> objectStorage;

  public ObjectStorageHelper(ObjectStorageOperations<?, ?, ?> objectStorage) {
    this.objectStorage = objectStorage;
  }

  public void upload(Encounter encounter) throws EncounterServiceException {
    String filePath = encounter.getEncounterId() + "_" + encounter.getPatientId() + ".txt";
    String content = generatePrescriptionContent(encounter.getRecommendation());
    UploadRequest objectStorageUpload = UploadRequest.fromBytes(content.getBytes(), filePath);
    try {
      objectStorage.upload(objectStorageUpload);
      LOG.info("File uploaded: {}", filePath);
    } catch (ObjectStorageException exception) {
      throw new EncounterServiceException("Error saving recommendation to object storage.", exception);
    }
  }

  private String generatePrescriptionContent(Recommendation recommendation) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.dd.MM");
    Date date = new Date();
    return String.format(
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
  }
}
