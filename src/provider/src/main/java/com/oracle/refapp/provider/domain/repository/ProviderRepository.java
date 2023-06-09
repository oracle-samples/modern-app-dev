/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.domain.repository;

import com.oracle.refapp.provider.domain.entity.ProviderEntity;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.ORACLE)
public interface ProviderRepository extends CrudRepository<ProviderEntity, Integer> {
  Optional<ProviderEntity> findByUsername(@NonNull String username);

  @Query(
    value = "select * from provider provider_entity_  where \n" +
    "(:speciality IS NULL or speciality = :speciality ) and\n" +
    " (:city is NULL or city = :city )  and \n" +
    "(:name is NULL or lower(concat(concat(first_name,middle_name),last_name)) like lower(concat(concat('%',REPLACE(:name ,' ','%')),'%')))",
    countQuery = "select count(*) from provider provider_entity_  where \n" +
    "(:speciality IS NULL or speciality = :speciality ) and\n" +
    " (:city is NULL or city = :city )  and \n" +
    "(:name is NULL or lower(concat(concat(first_name,middle_name),last_name)) like lower(concat(concat('%',REPLACE(:name ,' ','%')),'%')))"
  )
  Page<ProviderEntity> filterByQueries(
    @Nullable String speciality,
    @Nullable String city,
    @Nullable String name,
    @NonNull Pageable pageable
  );
}
