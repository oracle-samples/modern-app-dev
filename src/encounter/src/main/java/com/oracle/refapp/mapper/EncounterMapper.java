/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mapper;

import com.oracle.refapp.domain.entity.CodeEntity;
import com.oracle.refapp.domain.entity.ConditionEntity;
import com.oracle.refapp.domain.entity.EncounterEntity;
import com.oracle.refapp.model.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jsr330")
public interface EncounterMapper {
  default LocalDate fromDate(Date date) {
    return new java.sql.Date(date.getTime()).toLocalDate();
  }

  List<CodeSummary> mapDomainToApiModels(List<CodeEntity> codeEntity);

  Encounter mapDomainToApiModels(EncounterEntity encounterEntity);

  EncounterEntity mapApiToDomainModels(Encounter encounter);

  Condition mapDomainToApiModels(ConditionEntity conditionEntity);

  ConditionEntity mapApiToDomainModels(Condition condition);

  @Mapping(target = "providerName", source = "participant.name")
  @Mapping(target = "recommendationText", source = "recommendation.instruction")
  EncounterSummary mapEncounterEntityToEncounterSummary(EncounterEntity encounterEntity);
}
