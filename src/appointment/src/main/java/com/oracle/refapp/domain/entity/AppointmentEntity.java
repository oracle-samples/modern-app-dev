/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.entity;

import com.oracle.refapp.constants.Status;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.SerdeImport;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedEntity(value = "appointment")
@SerdeImport(AppointmentEntity.class)
public class AppointmentEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;

  private Integer patientId;

  private Integer providerId;

  private String preVisitData;

  private ZonedDateTime startTime;

  private ZonedDateTime endTime;

  @Enumerated(EnumType.STRING)
  private Status status;

  private String uniqueString;
}
