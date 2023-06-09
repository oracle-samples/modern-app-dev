/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.repository;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.ScheduleEntity;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.time.ZonedDateTime;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface ScheduleRepository extends CrudRepository<ScheduleEntity, Integer> {
  Optional<ScheduleEntity> findByProviderAndStartTimeAndEndTime(
    ProviderEntity providerEntity,
    ZonedDateTime startTime,
    ZonedDateTime endTime
  );
  Page<ScheduleEntity> findByProvider(ProviderEntity providerEntity, Pageable page);
}
