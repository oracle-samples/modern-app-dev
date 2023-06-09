/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.exceptions;

public class InvalidTimestampException extends Exception {

  public InvalidTimestampException(String errorMessage) {
    super(errorMessage);
  }
}
