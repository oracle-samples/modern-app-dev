/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Introspected
public class EncounterEntity {

  @JsonProperty("encounter_id")
  private String encounterId;

  @JsonProperty("provider_id")
  private Integer providerId;

  @JsonProperty("patient_id")
  private Integer patientId;

  @JsonProperty("appointment_id")
  private Integer appointmentId;

  private String status;

  @JsonProperty("followup_requested")
  private Boolean followUpRequested;

  @JsonProperty("reason_code")
  private String reasonCode;

  private List<ObservationEntity> observations;

  private List<ConditionEntity> conditions;

  private RecommendationEntity recommendation;

  private ParticipantEntity participant;

  @JsonProperty("patient_name")
  private String patientName;

  private String location;

  @JsonProperty("service_provider")
  private String serviceProvider;

  private PeriodEntity period;

  private String type;
}
