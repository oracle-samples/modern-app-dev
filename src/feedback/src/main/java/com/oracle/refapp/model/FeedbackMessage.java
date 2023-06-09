/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "FeedbackMessage", description = "POJO that represents a Feedback Message.")
public class FeedbackMessage {

  @JsonProperty("messageType")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, readOnly = true, example = "feedback")
  private final String messageType = "feedback";

  @JsonProperty("emailSubject")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, readOnly = true, example = "Feedback email subject!")
  private String emailSubject;

  @JsonProperty("emailBody")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, readOnly = true, example = "This is a message for you to feedback!")
  private String emailBody;

  @JsonProperty("patientEmail")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, example = "patient@uho.org")
  private String patientEmail;

  @JsonProperty("providerName")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, example = "Dr. Provider")
  private String providerName;

  @JsonProperty("patientName")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(required = true, example = "provider@uho.org")
  private String patientName;

  @JsonIgnore
  private String encounterId;
}
