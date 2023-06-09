/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.telemetry;

import java.util.Map;
import org.eclipse.microprofile.metrics.MetricUnits;

abstract class UnitConverter {

  private final String baseUnits;
  private final Map<String, Double> conversions;

  UnitConverter(String baseUnits, Map<String, Double> conversions) {
    this.baseUnits = baseUnits;
    this.conversions = conversions;
  }

  static StorageUnitConverter storageUnitConverter() {
    return new StorageUnitConverter();
  }

  static TimeUnitConverter timeUnitConverter() {
    return new TimeUnitConverter();
  }

  String baseUnits() {
    return baseUnits;
  }

  double convert(String metricUnits, double value) {
    return conversions.get(metricUnits) * value;
  }

  boolean handles(String metricUnits) {
    return conversions.containsKey(metricUnits);
  }

  private static class StorageUnitConverter extends UnitConverter {

    private static final Map<String, Double> CONVERSIONS = Map.of(
      MetricUnits.BITS,
      1.0 / 8.0,
      MetricUnits.KILOBITS,
      1.0D / 8.0 * 1000.0,
      MetricUnits.MEGABITS,
      1.0 / 8.0 * 1000.0 * 1000.0,
      MetricUnits.GIGABITS,
      1.0 / 8.0 * 1000.0 * 1000.0 * 1000.0,
      MetricUnits.BYTES,
      1.0,
      MetricUnits.KILOBYTES,
      1000.0,
      MetricUnits.MEGABYTES,
      1000.0 * 1000.0,
      MetricUnits.GIGABYTES,
      1000.0 * 1000.0 * 1000.0
    );

    StorageUnitConverter() {
      super(MetricUnits.BYTES, CONVERSIONS);
    }
  }

  private static class TimeUnitConverter extends UnitConverter {

    private static final Map<String, Double> CONVERSIONS = Map.of(
      MetricUnits.NANOSECONDS,
      1.0 / 1000.0 / 1000.0 / 1000.0,
      MetricUnits.MICROSECONDS,
      1.0 / 1000.0 / 1000.0,
      MetricUnits.MILLISECONDS,
      1.0 / 1000.0,
      MetricUnits.SECONDS,
      1.0,
      MetricUnits.MINUTES,
      60.0,
      MetricUnits.HOURS,
      60.0 * 60.0,
      MetricUnits.DAYS,
      60.0 * 60.0 * 24.0
    );

    TimeUnitConverter() {
      super(MetricUnits.SECONDS, CONVERSIONS);
    }
  }
}
