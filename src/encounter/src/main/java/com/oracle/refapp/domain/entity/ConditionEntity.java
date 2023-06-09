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
public class ConditionEntity {

  @JsonProperty("condition_id")
  private String conditionId;

  private String code;

  @JsonProperty("clinical_status")
  private String clinicalStatus;

  @JsonProperty("verification_status")
  private String verificationStatus;

  private String category;

  @JsonProperty("recorded_date")
  private String recordedDate;
}
