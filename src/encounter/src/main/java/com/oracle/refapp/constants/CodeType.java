/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.constants;

import io.micronaut.core.annotation.Introspected;

@Introspected
public enum CodeType {
  CONDITION,
  ENCOUNTER,
  OBSERVATION,
  REASON,
}
