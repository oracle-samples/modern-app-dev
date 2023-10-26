/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.provider.config;

import io.micronaut.core.bind.ArgumentBinder.BindingResult;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.convert.ConversionError;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.http.bind.binders.QueryValueArgumentBinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author graemerocher
 */
public class CustomQueryValueBinder<T> extends QueryValueArgumentBinder<T> {

  public CustomQueryValueBinder(ConversionService conversionService) {
    super(conversionService);
  }

  @Override
  protected BindingResult<T> doConvert(
    Object value,
    ArgumentConversionContext<T> context,
    BindingResult<T> defaultResult
  ) {
    if (value == null && context.hasErrors()) {
      return new BindingResult<T>() {
        @Override
        public Optional<T> getValue() {
          return Optional.empty();
        }

        @Override
        public boolean isSatisfied() {
          return false;
        }

        @Override
        public List<ConversionError> getConversionErrors() {
          List<ConversionError> errors = new ArrayList<>();
          for (ConversionError error : context) {
            errors.add(error);
          }
          return errors;
        }
      };
    } else {
      return super.doConvert(value, context, defaultResult);
    }
  }
}
