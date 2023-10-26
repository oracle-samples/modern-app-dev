/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.search;

import com.oracle.refapp.domain.entity.AppointmentEntity;
import com.oracle.refapp.domain.repository.AppointmentRepository;
import com.oracle.refapp.mappers.AppointmentMapper;
import com.oracle.refapp.model.AppointmentSearchCriteria;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import jakarta.inject.Singleton;

@Singleton
public class TimeRangeAppointmentSearcher extends AbstractAppointmentSearcher {

  public TimeRangeAppointmentSearcher(
    AppointmentRepository appointmentRepository,
    AppointmentMapper appointmentMapper
  ) {
    super(appointmentRepository, appointmentMapper);
  }

  @Override
  protected Page<AppointmentEntity> searchBetweenStartAndEndTime(AppointmentSearchCriteria searchCriteria) {
    return getAppointmentRepository()
      .findByStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
        searchCriteria.getStartTime(),
        searchCriteria.getEndTime(),
        Pageable.from(searchCriteria.getPage(), searchCriteria.getLimit(), Sort.of(SORT_ORDER))
      );
  }

  @Override
  protected Page<AppointmentEntity> searchByStartTime(AppointmentSearchCriteria searchCriteria) {
    return getAppointmentRepository()
      .findByStartTimeGreaterThanEquals(
        searchCriteria.getStartTime(),
        Pageable.from(searchCriteria.getPage(), searchCriteria.getLimit(), Sort.of(SORT_ORDER))
      );
  }

  @Override
  protected Page<AppointmentEntity> searchByEndTime(AppointmentSearchCriteria searchCriteria) {
    return getAppointmentRepository()
      .findByEndTimeLessThanEquals(
        searchCriteria.getEndTime(),
        Pageable.from(searchCriteria.getPage(), searchCriteria.getLimit(), Sort.of(SORT_ORDER))
      );
  }

  @Override
  protected Page<AppointmentEntity> unboundedSearch(AppointmentSearchCriteria searchCriteria) {
    return getAppointmentRepository()
      .findAll(Pageable.from(searchCriteria.getPage(), searchCriteria.getLimit(), Sort.of(SORT_ORDER)));
  }
}
