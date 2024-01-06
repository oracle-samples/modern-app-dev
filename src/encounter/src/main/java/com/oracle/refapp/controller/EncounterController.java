/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.controller;

import static com.oracle.refapp.constants.ErrorCodes.ERROR_CODE_INTERNAL_SERVER_ERROR;
import static com.oracle.refapp.constants.ErrorCodes.ERROR_CODE_NOT_FOUND;

import com.oracle.refapp.exceptions.EncounterServiceException;
import com.oracle.refapp.model.CodeCollection;
import com.oracle.refapp.model.CodeType;
import com.oracle.refapp.model.Encounter;
import com.oracle.refapp.model.EncounterCollection;
import com.oracle.refapp.model.ErrorResponse;
import com.oracle.refapp.search.SearchCriteria;
import com.oracle.refapp.service.EncounterService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import reactor.core.publisher.Mono;

@Controller
@ExecuteOn(TaskExecutors.BLOCKING)
public class EncounterController implements EncounterApi {

  private final EncounterService encounterService;

  public EncounterController(EncounterService encounterService) {
    this.encounterService = encounterService;
  }

  @Override
  public Mono<Encounter> createEncounter(Encounter encounter) {
    try {
      return Mono.just(encounterService.createEncounter(encounter));
    } catch (EncounterServiceException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ERROR_CODE_INTERNAL_SERVER_ERROR).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<HttpResponse<Void>> deleteEncounter(String encounterId) {
    try {
      encounterService.deleteEncounter(encounterId);
      return Mono.just(HttpResponse.ok());
    } catch (EncounterServiceException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code("NotDeleted").message(exception.toString())
      );
    }
  }

  @Override
  public Mono<Encounter> getEncounter(String encounterId) {
    try {
      return Mono.just(encounterService.getEncounter(encounterId));
    } catch (EncounterServiceException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<CodeCollection> listCodes(CodeType type, Integer limit, Integer page) {
    try {
      return Mono.just(
        encounterService.listCodes(com.oracle.refapp.constants.CodeType.valueOf(type.name()), limit, page)
      );
    } catch (Exception exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ERROR_CODE_INTERNAL_SERVER_ERROR).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<EncounterCollection> listEncounters(
    Integer patientId,
    Integer providerId,
    Integer appointmentId,
    Integer limit,
    Integer page
  ) {
    try {
      SearchCriteria searchCriteria = new SearchCriteria();
      searchCriteria.setPatientId(patientId);
      searchCriteria.setProviderId(providerId);
      searchCriteria.setAppointmentId(appointmentId);
      searchCriteria.setLimit(limit);
      searchCriteria.setPage(page);
      return Mono.just(encounterService.listEncounters(searchCriteria));
    } catch (Exception exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ERROR_CODE_INTERNAL_SERVER_ERROR).message(exception.toString())
      );
    }
  }

  @Override
  public Mono<Encounter> updateEncounter(String encounterId, Encounter encounter) {
    try {
      return Mono.just(encounterService.updateEncounter(encounterId, encounter));
    } catch (EncounterServiceException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code("NotUpdated").message(exception.toString())
      );
    }
  }
}
