/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterContextException extends RuntimeException {

  private String encounterId;

  public EncounterContextException(String message) {
    super(message);
  }
}
