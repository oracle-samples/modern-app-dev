/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EncounterCloudEvent {

  @JsonProperty("data")
  private Data data;

  @Getter
  @Setter
  public static class Data {

    @JsonProperty("resourceName")
    private String resourceName;

    public String getEncounterId() {
      return this.getResourceName().split("_")[0];
    }
  }
}
