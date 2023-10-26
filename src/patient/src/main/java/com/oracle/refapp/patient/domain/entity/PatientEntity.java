/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oracle.refapp.patient.models.Gender;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@MappedEntity(value = "patient")
@NoArgsConstructor
public class PatientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;
  private String username;
  private String phone;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date dob;

  @Email
  private String email;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private String zip;
  private String city;
  private String country;
  private Integer primaryCareProviderId;
  private String insuranceProvider;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Date getDob() {
    return dob;
  }

  public void setDob(Date dob) {
    this.dob = dob;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Integer getPrimaryCareProviderId() {
    return primaryCareProviderId;
  }

  public void setPrimaryCareProviderId(Integer primaryCareProviderId) {
    this.primaryCareProviderId = primaryCareProviderId;
  }

  public String getInsuranceProvider() {
    return insuranceProvider;
  }

  public void setInsuranceProvider(String insuranceProvider) {
    this.insuranceProvider = insuranceProvider;
  }
}
