/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.helpers;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micronaut.security.token.config.TokenConfiguration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CustomRoleFinderTest {

  @Test
  @DisplayName("test CustomRoleFinder resolveRoles")
  void testCustomRoleFinderResolveRoles() {
    TokenConfiguration tokenConfiguration = mock(TokenConfiguration.class);
    when(tokenConfiguration.getRolesName()).thenReturn("roles");
    CustomRoleFinder customRoleFinder = new CustomRoleFinder(tokenConfiguration);

    // When attributes == null
    assert (customRoleFinder.resolveRoles(null).isEmpty());

    List<String> expected = List.of("Provider", "Patient", "Admin");

    // When rolesObject instanceof Iterable
    Map<String, Object> attributes1 = Map.of("roles", List.of("Provider", "Patient", "Admin"));
    List<String> actual1 = customRoleFinder.resolveRoles(attributes1);
    assertIterableEquals(expected, actual1);
    // Else
    Map<String, Object> attributes2 = Map.of("roles", "Provider Patient Admin");
    List<String> actual2 = customRoleFinder.resolveRoles(attributes2);
    assertIterableEquals(expected, actual2);
  }
}
