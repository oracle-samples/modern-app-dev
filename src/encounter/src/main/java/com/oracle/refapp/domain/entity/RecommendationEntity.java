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
public class RecommendationEntity {

  @JsonProperty("recommendation_id")
  private String recommendationId;

  @JsonProperty("recommendation_date")
  private String recommendationDate;

  @JsonProperty("recommended_by")
  private String recommendedBy;

  @JsonProperty("instruction")
  private String instruction;

  @JsonProperty("additional_instructions")
  private String additionalInstructions;
}
