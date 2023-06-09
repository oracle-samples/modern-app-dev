/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.controller.search;

import static org.junit.jupiter.api.Assertions.*;

import com.oracle.refapp.search.SearchCriteria;
import com.oracle.refapp.search.SearchFilterBuilder;
import org.junit.jupiter.api.Test;

public class SearchFilterBuilderTest {

  private static final Integer TEST_PATIENT_ID = 1;
  private static final Integer TEST_PROVIDER_ID = 2;
  private static final Integer TEST_APPOINTMENT_ID = 3;

  private final SearchFilterBuilder searchFilterBuilder = new SearchFilterBuilder();

  @Test
  public void testFilters() {
    String built = searchFilterBuilder.build(new SearchCriteria(TEST_PATIENT_ID, null, null, null, null));
    assertEquals("{ \"patient_id\": " + TEST_PATIENT_ID + " }", built);
    built = searchFilterBuilder.build(new SearchCriteria(null, TEST_PROVIDER_ID, null, null, null));
    assertEquals("{ \"provider_id\": " + TEST_PROVIDER_ID + " }", built);
    built = searchFilterBuilder.build(new SearchCriteria(null, null, TEST_APPOINTMENT_ID, null, null));
    assertEquals("{ \"appointment_id\": " + TEST_APPOINTMENT_ID + " }", built);
    built =
      searchFilterBuilder.build(new SearchCriteria(TEST_PATIENT_ID, TEST_PROVIDER_ID, TEST_APPOINTMENT_ID, null, null));
    assertEquals(
      "{ \"patient_id\": " +
      TEST_PATIENT_ID +
      ",\"provider_id\": " +
      TEST_PROVIDER_ID +
      ",\"appointment_id\": " +
      TEST_APPOINTMENT_ID +
      " }",
      built
    );
  }
}
