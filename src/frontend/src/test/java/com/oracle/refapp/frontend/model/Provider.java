/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Introspected
@Serdeable
public class Provider {

  private Integer id;
  private String name;
  private String phone;
  private String email;
  private String gender;
  private Integer zip;
  private String city;
  private String country;
  private String speciality;
}
