/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.domain.repository;

import com.oracle.refapp.domain.entity.AppointmentEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.time.ZonedDateTime;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface AppointmentRepository extends CrudRepository<AppointmentEntity, Integer> {
  Page<AppointmentEntity> findByPatientId(@NonNull Integer patientId, @NonNull Pageable pageable);

  Page<AppointmentEntity> findByProviderId(@NonNull Integer providerId, @NonNull Pageable pageable);

  Page<AppointmentEntity> findByPatientIdAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
    @NonNull Integer patientId,
    @NonNull ZonedDateTime startTime,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByPatientIdAndStartTimeGreaterThanEquals(
    @NonNull Integer patientId,
    @NonNull ZonedDateTime startTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByPatientIdAndEndTimeLessThanEquals(
    @NonNull Integer patientId,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByProviderIdAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
    @NonNull Integer providerId,
    @NonNull ZonedDateTime startTime,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByProviderIdAndStartTimeGreaterThanEquals(
    @NonNull Integer providerId,
    @NonNull ZonedDateTime startTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByProviderIdAndEndTimeLessThanEquals(
    @NonNull Integer providerId,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
    @NonNull ZonedDateTime startTime,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByStartTimeGreaterThanEquals(
    @NonNull ZonedDateTime startTime,
    @NonNull Pageable pageable
  );

  Page<AppointmentEntity> findByEndTimeLessThanEquals(@NonNull ZonedDateTime endTime, @NonNull Pageable pageable);

  Page<AppointmentEntity> findAll(@NonNull Pageable pageable);
}
