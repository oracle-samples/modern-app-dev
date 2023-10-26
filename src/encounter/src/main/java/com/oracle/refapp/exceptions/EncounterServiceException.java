/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.exceptions;

public class EncounterServiceException extends Exception {

  public EncounterServiceException(String message) {
    super(message);
  }

  public EncounterServiceException(String message, Exception e) {
    super(message, e);
  }
}
