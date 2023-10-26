/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oracle.refapp.constants.Status;
import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.exceptions.*;
import com.oracle.refapp.model.AppointmentCollection;
import com.oracle.refapp.model.AppointmentMessage;
import com.oracle.refapp.model.AppointmentSearchCriteria;
import com.oracle.refapp.search.AppointmentSearcher;
import com.oracle.refapp.search.AppointmentSearcherFactory;
import io.micronaut.data.exceptions.DataAccessException;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final AppointmentMessageProducer appointmentMessageProducer;
  private final AppointmentSearcherFactory appointmentSearcherFactory;
  private final ServiceClient serviceClient;
  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentService.class);

  public AppointmentService(
    AppointmentRepository appointmentRepository,
    AppointmentMessageProducer appointmentMessageProducer,
    AppointmentSearcherFactory appointmentSearcherFactory,
    ServiceClient serviceClient
  ) {
    this.appointmentRepository = appointmentRepository;
    this.appointmentMessageProducer = appointmentMessageProducer;
    this.appointmentSearcherFactory = appointmentSearcherFactory;
    this.serviceClient = serviceClient;
  }

  @Transactional
  public AppointmentEntity getAppointment(Integer id) throws NoSuchAppointmentFoundException {
    return appointmentRepository.findById(id).orElseThrow(() -> new NoSuchAppointmentFoundException(id));
  }

  @Transactional
  public AppointmentEntity createAppointment(AppointmentEntity appointmentParam, String accessToken)
    throws IOException, NoSuchPatientFoundException, NoSuchProviderFoundException, MultipleAppointmentNotAllowedException {
    LOGGER.debug("Access token received {}", accessToken);
    AppointmentEntity appointment;
    try {
      appointment = saveAppointment(appointmentParam);
    } catch (DataAccessException exception) {
      if (exception.getMessage().contains("ORA-00001")) {
        LOGGER.error("Cannot book multiple appointments for the same timeslot", exception);
        throw new MultipleAppointmentNotAllowedException(
          "Invalid input: Cannot book multiple appointments for the same timeslot"
        );
      }
      LOGGER.error("internal error", exception);
      throw exception;
    }
    sendAppointmentMessage(appointment, accessToken);
    return appointment;
  }

  @Transactional
  public AppointmentEntity updateAppointment(Integer id, Status status, String accessToken)
    throws NoSuchAppointmentFoundException, IOException, AppointmentUpdateFailedException, NoSuchPatientFoundException, NoSuchProviderFoundException {
    AppointmentEntity currentAppointment = appointmentRepository.findById(id).orElse(null);
    if (currentAppointment == null) {
      throw new NoSuchAppointmentFoundException(id);
    } else if (status.equals(Status.CONFIRMED)) {
      throw new AppointmentUpdateFailedException("Appointment + " + id + " cannot be updated.");
    } else if (status.equals(Status.CANCELLED)) {
      currentAppointment.setStatus(status);
      currentAppointment.setUniqueString(
        currentAppointment.getUniqueString().replace("confirmed", "cancelled" + currentAppointment.getId())
      );
    }
    appointmentRepository.update(currentAppointment);
    sendAppointmentMessage(currentAppointment, accessToken);
    return currentAppointment;
  }

  @Transactional
  public void deleteAppointment(Integer id) throws NoSuchAppointmentFoundException {
    AppointmentEntity appointment = appointmentRepository
      .findById(id)
      .orElseThrow(() -> new NoSuchAppointmentFoundException(id));
    appointmentRepository.delete(appointment);
  }

  @Transactional
  public AppointmentCollection searchAppointment(AppointmentSearchCriteria appointmentSearchCriteria) {
    AppointmentSearcher appointmentSearcher = appointmentSearcherFactory.getSearcher(appointmentSearchCriteria);
    return appointmentSearcher.search(appointmentSearchCriteria);
  }

  private void sendAppointmentMessage(AppointmentEntity appointment, String accessToken)
    throws NoSuchPatientFoundException, IOException, NoSuchProviderFoundException {
    Map<String, Object> patientDetails = serviceClient.getPatientDetails(appointment.getPatientId(), accessToken);
    Map<String, Object> providerDetails = serviceClient.getProviderDetails(appointment.getProviderId(), accessToken);
    String message = buildAppointmentMessage(appointment, patientDetails, providerDetails);
    appointmentMessageProducer.sendMessage(appointment.getProviderId().toString(), message);
  }

  private String buildAppointmentMessage(
    AppointmentEntity appointment,
    Map<String, Object> patientDetails,
    Map<String, Object> providerDetails
  ) throws JsonProcessingException {
    AppointmentMessage appointmentMessage = AppointmentMessage
      .builder()
      .startTime(appointment.getStartTime())
      .endTime(appointment.getEndTime())
      .status(appointment.getStatus())
      .patientEmail(patientDetails.get("email").toString())
      .patientName(patientDetails.get("name").toString())
      .providerEmail(providerDetails.get("email").toString())
      .providerName(providerDetails.get("firstName").toString())
      .build();
    return mapper.writeValueAsString(appointmentMessage);
  }

  private AppointmentEntity saveAppointment(AppointmentEntity appointmentParam) {
    appointmentParam.setStatus(Status.CONFIRMED);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
      .append(appointmentParam.getProviderId().toString())
      .append(appointmentParam.getStartTime().toString())
      .append(appointmentParam.getEndTime().toString())
      .append(Status.CONFIRMED.name().toLowerCase());
    appointmentParam.setUniqueString(stringBuilder.toString());
    return appointmentRepository.save(appointmentParam);
  }
}
