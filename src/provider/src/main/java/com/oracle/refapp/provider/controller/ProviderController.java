/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.controller;

import static com.oracle.refapp.provider.constants.ErrorCodes.ERROR_CODE_BAD_REQUEST;
import static com.oracle.refapp.provider.constants.ErrorCodes.ERROR_CODE_NOT_FOUND;
import static com.oracle.refapp.provider.constants.ErrorCodes.PROVIDER_NOT_FOUND;

import com.oracle.refapp.provider.exceptions.ProviderNotFoundException;
import com.oracle.refapp.provider.exceptions.ScheduleNotFoundException;
import com.oracle.refapp.provider.exceptions.UsernameAlreadyTakenException;
import com.oracle.refapp.provider.mappers.ProviderMapper;
import com.oracle.refapp.provider.models.CreateFeedbackDetailsRequest;
import com.oracle.refapp.provider.models.CreateProviderDetailsRequest;
import com.oracle.refapp.provider.models.CreateScheduleDetailsRequest;
import com.oracle.refapp.provider.models.ErrorResponse;
import com.oracle.refapp.provider.models.Feedback;
import com.oracle.refapp.provider.models.FeedbackCollection;
import com.oracle.refapp.provider.models.Provider;
import com.oracle.refapp.provider.models.ProviderCollection;
import com.oracle.refapp.provider.models.Schedule;
import com.oracle.refapp.provider.models.ScheduleCollection;
import com.oracle.refapp.provider.models.SlotCollection;
import com.oracle.refapp.provider.service.FeedbackService;
import com.oracle.refapp.provider.service.ProviderService;
import com.oracle.refapp.provider.service.ScheduleService;
import com.oracle.refapp.provider.service.SlotService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import java.time.ZonedDateTime;
import reactor.core.publisher.Mono;

@Controller
@ExecuteOn(TaskExecutors.BLOCKING)
public class ProviderController implements ProviderApi {

  private final ProviderService providerService;
  private final SlotService slotService;
  private final ScheduleService scheduleService;
  private final FeedbackService feedbackService;
  private final ProviderMapper mapper;

  public ProviderController(
    ProviderService providerService,
    SlotService slotService,
    ScheduleService scheduleService,
    FeedbackService feedbackService,
    ProviderMapper mapper
  ) {
    this.providerService = providerService;
    this.slotService = slotService;
    this.scheduleService = scheduleService;
    this.feedbackService = feedbackService;
    this.mapper = mapper;
  }

  @Override
  public Mono<Feedback> createFeedback(Integer providerId, CreateFeedbackDetailsRequest createFeedbackDetailsRequest) {
    return Mono.just(
      mapper.mapDomainToApiModels(feedbackService.createFeedback(providerId, createFeedbackDetailsRequest))
    );
  }

  @Override
  public Mono<Provider> createProvider(CreateProviderDetailsRequest createProviderDetailsRequest) {
    try {
      return Mono.just(
        mapper.mapProviderEntityToProvider(
          providerService.createProvider(mapper.mapCreateProviderDetailsToProviderEntity(createProviderDetailsRequest))
        )
      );
    } catch (UsernameAlreadyTakenException exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ERROR_CODE_BAD_REQUEST).message(exception.getLocalizedMessage())
      );
    }
  }

  @Override
  public Mono<Schedule> createSchedule(Integer providerId, CreateScheduleDetailsRequest createScheduleDetailsRequest) {
    try {
      return Mono.just(
        mapper.mapDomainToApiModels(
          scheduleService.createSchedule(mapper.mapApiToDomainModels(createScheduleDetailsRequest), providerId)
        )
      );
    } catch (ProviderNotFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(PROVIDER_NOT_FOUND)
      );
    } catch (Exception exception) {
      throw new HttpStatusException(
        HttpStatus.BAD_REQUEST,
        new ErrorResponse().code(ERROR_CODE_BAD_REQUEST).message(exception.getMessage())
      );
    }
  }

  @Override
  public Mono<HttpResponse<Void>> deleteProvider(Integer providerId) {
    try {
      providerService.deleteProvider(providerId);
      return Mono.just(HttpResponse.ok());
    } catch (ProviderNotFoundException e) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(e.getMessage())
      );
    }
  }

  @Override
  public Mono<HttpResponse<Void>> deleteSchedule(Integer providerId, Integer scheduleId) {
    try {
      scheduleService.deleteSchedule(scheduleId);
      return Mono.just(HttpResponse.ok());
    } catch (ScheduleNotFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(exception.getMessage())
      );
    }
  }

  @Override
  public Mono<Provider> getProvider(Integer providerId) {
    try {
      return Mono.just(mapper.mapProviderEntityToProvider(providerService.findProviderById(providerId)));
    } catch (ProviderNotFoundException e) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(PROVIDER_NOT_FOUND)
      );
    }
  }

  @Override
  public Mono<Provider> getProviderByUsername(String username) {
    try {
      return Mono.just(mapper.mapProviderEntityToProvider(providerService.findProviderByUsername(username)));
    } catch (ProviderNotFoundException e) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(PROVIDER_NOT_FOUND)
      );
    }
  }

  @Override
  public Mono<Schedule> getSchedule(Integer providerId, Integer scheduleId) {
    try {
      return Mono.just(mapper.mapDomainToApiModels(scheduleService.getSchedule(scheduleId)));
    } catch (ScheduleNotFoundException e) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(e.getLocalizedMessage())
      );
    }
  }

  @Override
  public Mono<FeedbackCollection> listFeedbacks(
    Integer providerId,
    Integer patientId,
    Integer rating,
    Integer limit,
    Integer page
  ) {
    return Mono.just(feedbackService.search(providerId, patientId, rating, limit, page));
  }

  @Override
  public Mono<ProviderCollection> listProviders(
    String speciality,
    String city,
    String name,
    Integer limit,
    Integer page
  ) {
    return Mono.just(providerService.search(speciality, city, name, limit, page));
  }

  @Override
  public Mono<ScheduleCollection> listSchedules(Integer providerId, Integer limit, Integer page) {
    try {
      return Mono.just(scheduleService.search(providerId, limit, page));
    } catch (ProviderNotFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(exception.getMessage())
      );
    }
  }

  @Override
  public Mono<SlotCollection> listSlots(
    Integer providerId,
    ZonedDateTime startTime,
    ZonedDateTime endTime,
    Integer limit,
    Integer page
  ) {
    try {
      return Mono.just(slotService.search(providerId, startTime, endTime, limit, page));
    } catch (ProviderNotFoundException exception) {
      throw new HttpStatusException(
        HttpStatus.NOT_FOUND,
        new ErrorResponse().code(ERROR_CODE_NOT_FOUND).message(exception.toString())
      );
    }
  }
}
