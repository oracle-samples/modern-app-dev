/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend;

import io.micronaut.core.annotation.TypeHint;
import io.micronaut.runtime.Micronaut;

@TypeHint(typeNames = { "com.fasterxml.jackson.databind.PropertyNamingStrategy$SnakeCaseStrategy" })
public class Application {

  public static void main(String[] args) {
    Micronaut.build(args).eagerInitSingletons(true).mainClass(Application.class).start();
  }
}
