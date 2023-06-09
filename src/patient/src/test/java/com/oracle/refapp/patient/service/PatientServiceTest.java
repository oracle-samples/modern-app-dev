/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import static com.oracle.refapp.patient.TestUtils.TEST_LIMIT;
import static com.oracle.refapp.patient.TestUtils.TEST_PAGE;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_ENTITY;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_ID;
import static com.oracle.refapp.patient.TestUtils.TEST_PATIENT_USERNAME;
import static com.oracle.refapp.patient.TestUtils.TEST_PROVIDER;
import static com.oracle.refapp.patient.TestUtils.buildPatientEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.refapp.patient.domain.entity.PatientEntity;
import com.oracle.refapp.patient.domain.repository.PatientRepository;
import com.oracle.refapp.patient.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.patient.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.patient.exceptions.UniqueUserNameViolationException;
import com.oracle.refapp.patient.mapper.PatientMapper;
import com.oracle.refapp.patient.mapper.PatientMapperImpl;
import com.oracle.refapp.patient.models.Patient;
import com.oracle.refapp.patient.models.PatientCollection;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PatientServiceTest {

  private final PatientMessageProducer patientProducer = mock(PatientMessageProducer.class);
  private final PatientRepository patientRepository = mock(PatientRepository.class);
  private final PatientMapper patientMapper = new PatientMapperImpl();
  private final ProviderServiceClient providerServiceClient = mock(ProviderServiceClient.class);
  private final PatientService patientService = new PatientService(
    patientRepository,
    patientProducer,
    providerServiceClient,
    patientMapper
  );
  private static final String TEST_ACCESS_TOKEN = "testToken";

  @Test
  @DisplayName("test get patient by id.")
  void testGetPatient() throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.of(TEST_PATIENT_ENTITY));
    when(providerServiceClient.getProvider(TEST_PATIENT_ENTITY.getPrimaryCareProviderId(), TEST_ACCESS_TOKEN))
      .thenReturn(TEST_PROVIDER);
    Patient patient = patientService.getPatient(TEST_PATIENT_ID, TEST_ACCESS_TOKEN);
    verify(patientRepository, times(1)).findById(TEST_PATIENT_ID);
    verifyNoMoreInteractions(patientRepository);
    assertEquals(TEST_PROVIDER.getFirstName(), patient.getPrimaryCareProvider().getFirstName());
    assertEquals(TEST_PATIENT_ENTITY.getName(), patient.getName());
  }

  @Test
  @DisplayName("test get patient by id, provider not found.")
  void testGetPatientNoProviderFound() throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.of(TEST_PATIENT_ENTITY));
    when(providerServiceClient.getProvider(TEST_PATIENT_ENTITY.getPrimaryCareProviderId(), TEST_ACCESS_TOKEN))
      .thenReturn(null);
    Patient patient = patientService.getPatient(TEST_PATIENT_ID, TEST_ACCESS_TOKEN);
    verify(patientRepository, times(1)).findById(TEST_PATIENT_ID);
    verifyNoMoreInteractions(patientRepository);
    assertNull(patient.getPrimaryCareProvider());
  }

  @Test
  @DisplayName("test getPatient() exception")
  void testGetPatientException() {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.empty());
    assertThrows(NoSuchPatientFoundException.class, () -> patientService.getPatient(TEST_PATIENT_ID, ""));
  }

  @Test
  @DisplayName("test updatePatient()")
  void testUpdatePatient() throws NoSuchPatientFoundException {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.of(TEST_PATIENT_ENTITY));
    when(patientRepository.update(TEST_PATIENT_ENTITY)).thenReturn(TEST_PATIENT_ENTITY);
    patientService.updatePatient(TEST_PATIENT_ID, TEST_PATIENT_ENTITY);
    verify(patientRepository, times(1)).findById(TEST_PATIENT_ID);
    verify(patientRepository, times(1)).update(TEST_PATIENT_ENTITY);
    verifyNoMoreInteractions(patientRepository);
  }

  @Test
  @DisplayName("test authorizeDeviceToCollectData()")
  void testAuthorizeDeviceToCollectData() {
    doNothing().when(patientProducer).sendMessage(TEST_PATIENT_ID.toString(), "AuthorizeDeviceToCollectData");
    assertNotNull(patientProducer);
  }

  @Test
  @DisplayName("test create patient")
  void testCreatePatient() throws UniqueUserNameViolationException {
    PatientEntity testPatientWithNullId = buildPatientEntity();
    testPatientWithNullId.setId(null);
    PatientEntity testPatientCreated = buildPatientEntity();
    when(patientRepository.save(testPatientWithNullId)).thenReturn(testPatientCreated);
    patientService.createPatient(testPatientWithNullId);
    verify(patientRepository, times(1)).save(testPatientWithNullId);
    verifyNoMoreInteractions(patientRepository);
  }

  @Test
  @DisplayName("test create patient exception")
  void testCreatePatientException() {
    PatientEntity testPatientWithNullId = buildPatientEntity();
    testPatientWithNullId.setId(null);
    PatientEntity testPatientCreated = buildPatientEntity();
    when(patientRepository.save(testPatientWithNullId))
      .thenThrow(new DataAccessException("Error executing SQL UPDATE: ORA-00001: Unique Constraint Violated."));
    assertThrows(UniqueUserNameViolationException.class, () -> patientService.createPatient(testPatientWithNullId));
  }

  @Test
  @DisplayName("test getPatientByUsername()")
  void testGetPatientByUsername() throws NoSuchPatientFoundException {
    PatientEntity TEST_PATIENT_ENTITY = buildPatientEntity();
    when(patientRepository.findByUsername(TEST_PATIENT_USERNAME)).thenReturn(Optional.of(TEST_PATIENT_ENTITY));
    patientService.getPatientByUsername(TEST_PATIENT_USERNAME);
    verify(patientRepository, times(1)).findByUsername(TEST_PATIENT_USERNAME);
    verifyNoMoreInteractions(patientRepository);
  }

  @Test
  @DisplayName("test getPatient() exception")
  void testGetPatientByUsernameException() {
    when(patientRepository.findByUsername(TEST_PATIENT_USERNAME)).thenReturn(Optional.empty());
    assertThrows(NoSuchPatientFoundException.class, () -> patientService.getPatientByUsername(TEST_PATIENT_USERNAME));
  }

  @Test
  @DisplayName("test deletePatient()")
  void testDeletePatient() throws NoSuchPatientFoundException {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.of(TEST_PATIENT_ENTITY));
    patientService.deletePatient(TEST_PATIENT_ID);
    verify(patientRepository, times(1)).delete(TEST_PATIENT_ENTITY);
  }

  @Test
  @DisplayName("test deletePatient() exception")
  void testDeletePatientException() {
    when(patientRepository.findById(TEST_PATIENT_ID)).thenReturn(Optional.empty());
    assertThrows(NoSuchPatientFoundException.class, () -> patientService.deletePatient(TEST_PATIENT_ID));
  }

  @Test
  @DisplayName("test listPatients()")
  void testListPatient() {
    Page<PatientEntity> page = Page.of(List.of(TEST_PATIENT_ENTITY), Pageable.from(TEST_PAGE, TEST_LIMIT), 1);
    Sort.Order order = new Sort.Order("id", Sort.Order.Direction.ASC, true);
    when(patientRepository.findAll(Pageable.from(TEST_PAGE, TEST_LIMIT, Sort.of(order)))).thenReturn(page);
    PatientCollection actualResponse = patientService.listPatients(TEST_LIMIT, TEST_PAGE);
    assertEquals(1, actualResponse.getItems().size());
  }
}
