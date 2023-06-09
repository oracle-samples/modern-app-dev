/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes {

  public static final String ERROR_CODE_NOT_FOUND = "NotFound";
  public static final String PROVIDER_NOT_FOUND = "Provider Not Found";
  public static final String ERROR_CODE_BAD_REQUEST = "BadRequest";
}
