/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.service;

import com.oracle.refapp.patient.domain.entity.PatientEntity;
import com.oracle.refapp.patient.domain.repository.PatientRepository;
import com.oracle.refapp.patient.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.patient.exceptions.NoSuchProviderFoundException;
import com.oracle.refapp.patient.exceptions.UniqueUserNameViolationException;
import com.oracle.refapp.patient.mapper.PatientMapper;
import com.oracle.refapp.patient.models.Patient;
import com.oracle.refapp.patient.models.PatientCollection;
import com.oracle.refapp.patient.models.PatientSummary;
import com.oracle.refapp.patient.models.Provider;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PatientService {

  private static final Logger LOG = LoggerFactory.getLogger(PatientService.class);
  private static final Sort.Order DEFAULT_SORT_ORDER = new Sort.Order("id", Sort.Order.Direction.ASC, true);

  private final PatientRepository patientRepository;
  private final PatientMessageProducer patientMessageProducer;
  private final ProviderServiceClient providerServiceClient;
  private final PatientMapper patientMapper;

  public PatientService(
    PatientRepository patientRepository,
    PatientMessageProducer patientMessageProducer,
    ProviderServiceClient providerServiceClient,
    PatientMapper patientMapper
  ) {
    this.patientRepository = patientRepository;
    this.patientMessageProducer = patientMessageProducer;
    this.providerServiceClient = providerServiceClient;
    this.patientMapper = patientMapper;
  }

  @Transactional
  public Patient getPatient(Integer id, String accessToken) throws NoSuchPatientFoundException, IOException {
    PatientEntity patientEntity = patientRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchPatientFoundException("No patient with id " + id + " found."));
    Patient patient = patientMapper.mapDomainToApiModels(patientEntity);
    Provider provider = null;
    try {
      provider = providerServiceClient.getProvider(patientEntity.getPrimaryCareProviderId(), accessToken);
    } catch (NoSuchProviderFoundException e) {
      LOG.warn("No primary care provider found for patient {} : {}", id, e.toString());
    }
    patient.setPrimaryCareProvider(provider);
    LOG.info("Returning patient: {}", patient);
    return patient;
  }

  @Transactional
  public PatientEntity getPatientByUsername(String username) throws NoSuchPatientFoundException {
    Optional<PatientEntity> patientEntity = patientRepository.findByUsername(username);
    if (patientEntity.isEmpty()) {
      throw new NoSuchPatientFoundException("No patient with username " + username + " found.");
    }
    return patientEntity.get();
  }

  @Transactional
  public PatientEntity updatePatient(Integer id, PatientEntity updatedPatientEntity)
    throws NoSuchPatientFoundException {
    PatientEntity dbPatientEntity = patientRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchPatientFoundException("No patient with id " + id + " found."));
    updateDbPatientEntity(dbPatientEntity, updatedPatientEntity);
    patientRepository.update(dbPatientEntity);
    return dbPatientEntity;
  }

  @Transactional
  public PatientEntity createPatient(PatientEntity patientEntity) throws UniqueUserNameViolationException {
    try {
      return patientRepository.save(patientEntity);
    } catch (DataAccessException exception) {
      if (exception.getMessage().contains("ORA-00001")) {
        throw new UniqueUserNameViolationException("User with this username already exists");
      }
      throw exception;
    }
  }

  @Transactional
  public void deletePatient(Integer id) throws NoSuchPatientFoundException {
    PatientEntity patientEntity = patientRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchPatientFoundException("No patient with id " + id + " found."));
    patientRepository.delete(patientEntity);
  }

  @Transactional
  public PatientCollection listPatients(Integer limit, Integer page) {
    Page<PatientEntity> patientPage = patientRepository.findAll(
      Pageable.from(page, limit, Sort.of(DEFAULT_SORT_ORDER))
    );
    Integer nextPage = (patientPage.getTotalPages() > patientPage.nextPageable().getNumber())
      ? patientPage.nextPageable().getNumber()
      : null;
    List<PatientEntity> patientsList = patientPage.getContent();
    List<PatientSummary> patientsSummaryList = new ArrayList<>();
    patientsList.forEach(patientEntity -> {
      PatientSummary patientSummary = new PatientSummary();
      patientSummary.setId(patientEntity.getId());
      patientSummary.setEmail(patientEntity.getEmail());
      patientSummary.setGender(patientEntity.getGender());
      patientSummary.setUsername(patientEntity.getUsername());
      patientSummary.setPhone(patientEntity.getPhone());
      patientsSummaryList.add(patientSummary);
    });
    return new PatientCollection().items(patientsSummaryList).nextPage(nextPage);
  }

  public void authorizeDeviceToCollectData(Integer patientId) {
    patientMessageProducer.sendMessage(patientId.toString(), "AuthorizeDeviceToCollectData");
  }

  private void updateDbPatientEntity(PatientEntity dbPatientEntity, PatientEntity updatedPatientEntity) {
    dbPatientEntity.setName(updatedPatientEntity.getName());
    dbPatientEntity.setPhone(updatedPatientEntity.getPhone());
    dbPatientEntity.setDob(updatedPatientEntity.getDob());
    dbPatientEntity.setEmail(updatedPatientEntity.getEmail());
    dbPatientEntity.setGender(updatedPatientEntity.getGender());
    dbPatientEntity.setZip(updatedPatientEntity.getZip());
    dbPatientEntity.setCity(updatedPatientEntity.getCity());
    dbPatientEntity.setCountry(updatedPatientEntity.getCountry());
  }
}
