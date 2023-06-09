/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.exceptions;

public class NoSuchProviderFoundException extends Exception {

  public NoSuchProviderFoundException(String message) {
    super(message);
  }
}
