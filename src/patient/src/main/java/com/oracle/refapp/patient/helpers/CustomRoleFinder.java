/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.helpers;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.token.RolesFinder;
import io.micronaut.security.token.config.TokenConfiguration;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Replaces(RolesFinder.class)
@Singleton
public class CustomRoleFinder implements RolesFinder {

  private final TokenConfiguration tokenConfiguration;

  /**
   * Constructs a Roles Parser.
   * @param tokenConfiguration General Token Configuration
   */
  public CustomRoleFinder(TokenConfiguration tokenConfiguration) {
    this.tokenConfiguration = tokenConfiguration;
  }

  @NonNull
  private List<String> rolesAtObject(@Nullable Object rolesObject) {
    List<String> roles = new ArrayList<>();
    if (rolesObject != null) {
      if (rolesObject instanceof Iterable) {
        for (Object o : ((Iterable) rolesObject)) {
          roles.add(o.toString());
        }
      } else {
        roles.addAll(List.of(rolesObject.toString().split(" ")));
      }
    }
    return roles;
  }

  @Override
  @NonNull
  public List<String> resolveRoles(@Nullable Map<String, Object> attributes) {
    return rolesAtObject(attributes != null ? attributes.get(tokenConfiguration.getRolesName()) : null);
  }
}
