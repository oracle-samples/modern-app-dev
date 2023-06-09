/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.exceptions;

public class DatabaseConnectionException extends Exception {

  public DatabaseConnectionException(String msg) {
    super(msg);
  }
}
