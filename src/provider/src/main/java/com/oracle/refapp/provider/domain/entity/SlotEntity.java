/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.entity;

import com.oracle.refapp.provider.models.Status;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedEntity(value = "slot")
@Introspected
public class SlotEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(optional = false)
  private ProviderEntity provider;

  private ZonedDateTime startTime;

  private ZonedDateTime endTime;

  @Enumerated(EnumType.STRING)
  private Status status;
}
