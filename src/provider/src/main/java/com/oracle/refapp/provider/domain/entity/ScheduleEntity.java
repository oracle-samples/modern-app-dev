/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.entity;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.SerdeImport;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedEntity(value = "schedule")
@Table(
  uniqueConstraints = {
    @UniqueConstraint(
      name = "unique constraint on provider, his start time and end time",
      columnNames = { "provider_id", "startTime", "endTime" }
    ),
  }
)
@SerdeImport(ScheduleEntity.class)
public class ScheduleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "provider_id", nullable = false)
  private ProviderEntity provider;

  private ZonedDateTime startTime;

  private ZonedDateTime endTime;
}
