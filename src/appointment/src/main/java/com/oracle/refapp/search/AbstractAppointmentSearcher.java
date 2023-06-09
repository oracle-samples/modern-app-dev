/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.mappers.AppointmentMapper;
import com.oracle.refapp.model.AppointmentCollection;
import com.oracle.refapp.model.AppointmentSearchCriteria;
import com.oracle.refapp.model.AppointmentSummary;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Sort;
import java.time.ZonedDateTime;
import java.util.List;

public abstract class AbstractAppointmentSearcher implements AppointmentSearcher {

  protected static final Sort.Order SORT_ORDER = new Sort.Order("startTime", Sort.Order.Direction.DESC, true);

  private final AppointmentRepository appointmentRepository;
  private final AppointmentMapper appointmentMapper;

  AbstractAppointmentSearcher(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper) {
    this.appointmentRepository = appointmentRepository;
    this.appointmentMapper = appointmentMapper;
  }

  @Override
  public AppointmentCollection search(AppointmentSearchCriteria searchCriteria) {
    Page<AppointmentEntity> appointmentEntityPage;

    ZonedDateTime startTime = searchCriteria.getStartTime();
    ZonedDateTime endTime = searchCriteria.getEndTime();

    if (startTime != null && endTime != null) {
      appointmentEntityPage = searchBetweenStartAndEndTime(searchCriteria);
    } else if (startTime != null) {
      appointmentEntityPage = searchByStartTime(searchCriteria);
    } else if (endTime != null) {
      appointmentEntityPage = searchByEndTime(searchCriteria);
    } else {
      appointmentEntityPage = unboundedSearch(searchCriteria);
    }

    return prepareSearchResponse(appointmentEntityPage);
  }

  protected abstract Page<AppointmentEntity> searchBetweenStartAndEndTime(AppointmentSearchCriteria searchCriteria);

  protected abstract Page<AppointmentEntity> searchByStartTime(AppointmentSearchCriteria searchCriteria);

  protected abstract Page<AppointmentEntity> searchByEndTime(AppointmentSearchCriteria searchCriteria);

  protected abstract Page<AppointmentEntity> unboundedSearch(AppointmentSearchCriteria searchCriteria);

  protected AppointmentCollection prepareSearchResponse(Page<AppointmentEntity> appointmentEntityPage) {
    List<AppointmentEntity> appointmentEntities = appointmentEntityPage.getContent();
    List<AppointmentSummary> appointments = appointmentMapper.mapDomainToApiModels(appointmentEntities);
    Integer nextPage = (appointmentEntityPage.getTotalPages() > appointmentEntityPage.nextPageable().getNumber())
      ? appointmentEntityPage.nextPageable().getNumber()
      : null;
    return new AppointmentCollection().items(appointments).nextPage(nextPage);
  }

  protected AppointmentRepository getAppointmentRepository() {
    return this.appointmentRepository;
  }
}
