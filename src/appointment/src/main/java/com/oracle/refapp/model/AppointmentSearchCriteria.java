/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.model;

import io.micronaut.core.annotation.Introspected;
import java.time.ZonedDateTime;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Introspected
public class AppointmentSearchCriteria {

  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private Integer patientId;
  private Integer providerId;
  private Integer page;
  private Integer limit;
}
