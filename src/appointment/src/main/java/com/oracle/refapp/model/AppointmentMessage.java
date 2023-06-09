/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oracle.refapp.constants.Status;
import io.micronaut.core.annotation.Introspected;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Introspected
@Builder
public class AppointmentMessage {

  private Status status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
  private ZonedDateTime startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
  private ZonedDateTime endTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private String patientEmail;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private String providerEmail;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private String providerName;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private String patientName;
}
