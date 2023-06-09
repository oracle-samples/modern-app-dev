/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.helpers;

import static com.oracle.refapp.TestUtils.TEST_ENCOUNTER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import com.oracle.refapp.connections.ObjectStorageConnection;
import com.oracle.refapp.exceptions.EncounterServiceException;
import org.junit.jupiter.api.Test;

public class ObjectStorageHelperTest {

  private final ObjectStorageConnection objectStorage = mock(ObjectStorageConnection.class);
  private final ObjectStorageHelper objectStorageHelper = new ObjectStorageHelper(objectStorage);

  @Test
  public void testUpload() throws EncounterServiceException {
    ObjectStorageClient mockObjectStorageClient = mock(ObjectStorageClient.class);
    PutObjectResponse mockPutObjectResponse = mock(PutObjectResponse.class);
    String mockePdfFile = TEST_ENCOUNTER.getEncounterId() + "_" + TEST_ENCOUNTER.getPatientId() + ".pdf";
    when(objectStorage.getClient()).thenReturn(mockObjectStorageClient);
    when(mockObjectStorageClient.putObject(any(PutObjectRequest.class))).thenReturn(mockPutObjectResponse);
    when(mockPutObjectResponse.get__httpStatusCode__()).thenReturn(200);
    objectStorageHelper.upload(TEST_ENCOUNTER);
    verify(mockObjectStorageClient, times(1)).putObject(any(PutObjectRequest.class));
  }

  @Test
  public void testUploadFail_ObjectStorageFailed() {
    ObjectStorageClient mockObjectStorageClient = mock(ObjectStorageClient.class);
    PutObjectResponse mockPutObjectResponse = mock(PutObjectResponse.class);
    when(objectStorage.getClient()).thenReturn(mockObjectStorageClient);
    when(mockObjectStorageClient.putObject(any(PutObjectRequest.class))).thenReturn(mockPutObjectResponse);
    when(mockPutObjectResponse.get__httpStatusCode__()).thenReturn(500);
    assertThrows(EncounterServiceException.class, () -> objectStorageHelper.upload(TEST_ENCOUNTER));
    verify(mockObjectStorageClient, times(1)).putObject(any(PutObjectRequest.class));
  }
}
