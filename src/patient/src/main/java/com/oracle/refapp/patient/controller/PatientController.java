/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.controller;

import com.oracle.refapp.patient.constants.ErrorCodes;
import com.oracle.refapp.patient.exceptions.NoSuchPatientFoundException;
import com.oracle.refapp.patient.exceptions.UniqueUserNameViolationException;
import com.oracle.refapp.patient.mapper.PatientMapper;
import com.oracle.refapp.patient.models.CreatePatientDetailsRequest;
import com.oracle.refapp.patient.models.ErrorResponse;
import com.oracle.refapp.patient.models.Patient;
import com.oracle.refapp.patient.models.PatientCollection;
import com.oracle.refapp.patient.models.UpdatePatientDetailsRequest;
import com.oracle.refapp.patient.service.PatientService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@Controller
@ExecuteOn(TaskExecutors.BLOCKING)
public class PatientController implements PatientApi {

  private static final Logger LOG = LoggerFactory.getLogger(PatientController.class);

  private final PatientService patientService;

  private final PatientMapper patientMapper;

  public PatientController(PatientService patientService, PatientMapper patientMapper) {
    this.patientService = patientService;
    this.patientMapper = patientMapper;
  }

  @Override
  public Mono<String> authorizeDevice(Integer patientId) {
    patientService.authorizeDeviceToCollectData(patientId);
    return Mono.just("{\"message\":\"Authorized Successfully\"}");
  }

  @Override
  public Mono<Patient> createPatient(CreatePatientDetailsRequest createPatientDetailsRequest) {
    try {
      return Mono.just(
        patientMapper.mapDomainToApiModels(
          patientService.createPatient(patientMapper.mapApiToDomainModels(createPatientDetailsRequest))
        )
      );
    } catch (UniqueUserNameViolationException exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse()
          .code(ErrorCodes.ERROR_CODE_BAD_REQUEST)
          .message("Invalid input: User with this username already exists")
      );
    } catch (Exception exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_BAD_REQUEST).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<HttpResponse<Void>> deletePatient(Integer patientId) {
    try {
      patientService.deletePatient(patientId);
      return Mono.just(HttpResponse.ok());
    } catch (NoSuchPatientFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<Patient> getPatient(Integer patientId, String accessToken) {
    try {
      LOG.info("Retrieving patient with id: {}", patientId);
      return Mono.just(patientService.getPatient(patientId, accessToken));
    } catch (NoSuchPatientFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    } catch (IOException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ErrorCodes.INTERNAL_ERROR).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<Patient> getPatientByUsername(String username) {
    try {
      return Mono.just(patientMapper.mapDomainToApiModels(patientService.getPatientByUsername(username)));
    } catch (NoSuchPatientFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<PatientCollection> listPatients(Integer limit, Integer page) {
    return Mono.just(patientService.listPatients(limit, page));
  }

  @Override
  public Mono<Patient> updatePatient(Integer patientId, UpdatePatientDetailsRequest updatePatientDetailsRequest) {
    try {
      return Mono.just(
        patientMapper.mapDomainToApiModels(
          patientService.updatePatient(patientId, patientMapper.mapApiToDomainModels(updatePatientDetailsRequest))
        )
      );
    } catch (NoSuchPatientFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    }
  }
}
