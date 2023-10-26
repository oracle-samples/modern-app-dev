/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.entity;

import com.oracle.refapp.provider.models.Gender;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.SerdeImport;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedEntity(value = "provider")
@SerdeImport(ProviderEntity.class)
public class ProviderEntity {

  @Id
  @GeneratedValue(GeneratedValue.Type.IDENTITY)
  private Integer id;

  private String tags;
  private String username;
  private String firstName;
  private String middleName;
  private String lastName;
  private String title;
  private String phone;

  @Email
  private String email;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String zip;
  private String city;
  private String country;
  private String speciality;

  private String qualification;
  private String designation;
  private String professionalSummary;
  private String interests;
  private String expertise;
  private String hospitalName;
  private String hospitalAddress;
  private String hospitalPhone;
}
