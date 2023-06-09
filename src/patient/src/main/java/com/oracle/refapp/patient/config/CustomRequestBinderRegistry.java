/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.patient.config;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.bind.DefaultRequestBinderRegistry;
import io.micronaut.http.bind.binders.RequestArgumentBinder;
import jakarta.inject.Singleton;
import java.util.List;

/**
 *
 * @author graemerocher
 */
@Singleton
@Replaces(DefaultRequestBinderRegistry.class)
public class CustomRequestBinderRegistry extends DefaultRequestBinderRegistry {

  public CustomRequestBinderRegistry(ConversionService conversionService, List<RequestArgumentBinder> binders) {
    super(conversionService, binders);
    addRequestArgumentBinder(new CustomQueryValueBinder<>(conversionService));
  }
}
