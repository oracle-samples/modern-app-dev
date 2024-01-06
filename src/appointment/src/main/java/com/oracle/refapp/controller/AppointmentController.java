/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.refapp.constants.ErrorCodes;
import com.oracle.refapp.constants.Status;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.exceptions.*;
import com.oracle.refapp.mappers.AppointmentMapper;
import com.oracle.refapp.model.*;
import com.oracle.refapp.service.AppointmentService;
import com.oracle.refapp.telemetry.TelemetryClient;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import reactor.core.publisher.Mono;

@Controller
@ExecuteOn(TaskExecutors.BLOCKING)
public class AppointmentController implements AppointmentApi {

  private final AppointmentService appointmentService;

  private final AppointmentMapper mapper;

  private final TelemetryClient telemetryClient;

  public AppointmentController(
    AppointmentService appointmentService,
    AppointmentMapper mapper,
    TelemetryClient telemetryClient
  ) {
    this.appointmentService = appointmentService;
    this.mapper = mapper;
    this.telemetryClient = telemetryClient;
  }

  @Override
  public Mono<Appointment> createAppointment(CreateAppointmentRequest createAppointmentRequest, String accessToken) {
    try {
      AppointmentEntity prePersistenceAppointmentEntity = mapper.mapApiToDomainModels(createAppointmentRequest);
      AppointmentEntity postPersistenceAppointmentEntity = appointmentService.createAppointment(
        prePersistenceAppointmentEntity,
        accessToken
      );
      postMetricData();
      return Mono.just(mapper.mapDomainToApiModels(postPersistenceAppointmentEntity));
    } catch (JsonProcessingException exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code("InvalidParameter").message(exception.getMessage())
      );
    } catch (
      MultipleAppointmentNotAllowedException
      | NoSuchPatientFoundException
      | NoSuchProviderFoundException
      | DateTimeParseException exception
    ) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_BAD_REQUEST).message(exception.getMessage())
      );
    } catch (IOException | DataAccessException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ErrorCodes.INTERNAL_SERVER_ERROR).message(exception.getMessage())
      );
    }
  }

  @Override
  public Mono<HttpResponse<Void>> deleteAppointment(Integer appointmentId) {
    try {
      appointmentService.deleteAppointment(appointmentId);
      return Mono.just(HttpResponse.ok());
    } catch (NoSuchAppointmentFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message("Appointment not found")
      );
    }
  }

  @Override
  public Mono<Appointment> getAppointment(Integer appointmentId) {
    try {
      AppointmentEntity appointmentEntity = appointmentService.getAppointment(appointmentId);
      return Mono.just(mapper.mapDomainToApiModels(appointmentEntity));
    } catch (NoSuchAppointmentFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message("Appointment not found")
      );
    }
  }

  @Override
  public Mono<AppointmentCollection> listAppointments(
    Integer patientId,
    Integer providerId,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    Integer limit,
    Integer page
  ) {
    try {
      if (patientId != null && providerId != null) {
        throw new HttpStatusException(
          HttpStatus.BAD_REQUEST,
          new ErrorResponse()
            .code(ErrorCodes.ERROR_CODE_BAD_REQUEST)
            .message("Search with either patientId or providerId")
        );
      }
      ZonedDateTime zonedStartTime = startTime != null ? startTime : null;
      ZonedDateTime zonedEndTime = endTime != null ? endTime : null;
      AppointmentSearchCriteria searchCriteria = new AppointmentSearchCriteria(
        zonedStartTime,
        zonedEndTime,
        patientId,
        providerId,
        page,
        limit
      );
      return Mono.just(appointmentService.searchAppointment(searchCriteria));
    } catch (DateTimeParseException exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_BAD_REQUEST).message(exception.getMessage())
      );
    }
  }

  @Override
  public Mono<Appointment> updateAppointment(
    Integer appointmentId,
    UpdateAppointmentRequest updateAppointmentRequest,
    String accessToken
  ) {
    try {
      if (updateAppointmentRequest.getStatus() == null) {
        throw new AppointmentUpdateFailedException("Appointment status can't be null");
      }
      AppointmentEntity appointmentEntity = appointmentService.updateAppointment(
        appointmentId,
        Status.valueOf(updateAppointmentRequest.getStatus().name()),
        accessToken
      );
      return Mono.just(mapper.mapDomainToApiModels(appointmentEntity));
    } catch (NoSuchAppointmentFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_NOT_FOUND).message(exception.getMessage())
      );
    } catch (JsonProcessingException | NoSuchProviderFoundException | NoSuchPatientFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ErrorCodes.ERROR_CODE_BAD_REQUEST).message(exception.getMessage())
      );
    } catch (AppointmentUpdateFailedException | IOException exception) {
      throw new HttpStatusException(
        HttpStatus.INTERNAL_SERVER_ERROR,
        new ErrorResponse().code(ErrorCodes.INTERNAL_SERVER_ERROR).message(exception.getMessage())
      );
    }
  }

  private void postMetricData() {
    List<Datapoint> datapoints = new ArrayList<>(
      List.of(Datapoint.builder().timestamp(new Date(System.currentTimeMillis())).value(1.0).count(1).build())
    );
    telemetryClient.postMetricData("appointment-booked", datapoints);
  }
}
