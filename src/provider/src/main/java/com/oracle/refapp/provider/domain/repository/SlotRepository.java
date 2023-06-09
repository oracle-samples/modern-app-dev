/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.repository;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import com.oracle.refapp.provider.domain.entity.SlotEntity;
import com.oracle.refapp.provider.models.Status;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface SlotRepository extends CrudRepository<SlotEntity, Integer> {
  List<SlotEntity> findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
    ProviderEntity provider,
    ZonedDateTime startTime,
    ZonedDateTime endTime
  );
  Page<SlotEntity> findByProviderAndStartTimeGreaterThanEqualsAndEndTimeLessThanEquals(
    @NonNull ProviderEntity provider,
    @NonNull ZonedDateTime startTime,
    @NonNull ZonedDateTime endTime,
    @NonNull Pageable pageable
  );

  SlotEntity findByProviderAndStartTimeAndEndTime(
    ProviderEntity provider,
    ZonedDateTime startTime,
    ZonedDateTime endTime
  );

  Optional<SlotEntity> findByProviderAndStatusAndStartTimeGreaterThanEqualsOrderByStartTime(
    ProviderEntity provider,
    Status status,
    ZonedDateTime startTime
  );
}
