/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.repository;

import com.oracle.refapp.provider.domain.entity.FeedbackEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface FeedbackRepository extends CrudRepository<FeedbackEntity, Integer> {
  @Query("select coalesce(avg(f.rating),0) from feedback f, provider p where f.provider_id = :providerId")
  Double getAggregateRatingByProviderId(@NonNull Integer providerId);

  @Query(
    value = "select * from feedback feedback_entity_ " +
    "where provider_id = :providerId " +
    "and (:patientId is NULL or patient_id = :patientId) " +
    "and (:rating is NULL or rating = :rating)",
    countQuery = "select count(*) from feedback feedback_entity_ " +
    "where provider_id = :providerId " +
    "and (:patientId is NULL or patient_id = :patientId) " +
    "and (:rating is NULL or rating = :rating)"
  )
  Page<FeedbackEntity> filterByQueries(
    @NonNull Integer providerId,
    @Nullable Integer patientId,
    @Nullable Integer rating,
    @NonNull Pageable pageable
  );
}
