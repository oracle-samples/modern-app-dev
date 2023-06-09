/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class ObservationEntity {

  @JsonProperty("observation_id")
  private String observationId;

  private String status;
  private String category;

  @JsonProperty("parameter_type")
  private String parameterType;

  @JsonProperty("parameter_value")
  private ParameterEntity parameterValue;

  @JsonProperty("date_recorded")
  private String dateRecorded;
}
