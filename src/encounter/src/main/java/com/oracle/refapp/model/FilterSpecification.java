/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.model;

import lombok.Data;

@Data
public class FilterSpecification<T> {

  private String key;
  private T value;

  public FilterSpecification(String key, T val) {
    this.key = key;
    this.value = val;
  }
}
