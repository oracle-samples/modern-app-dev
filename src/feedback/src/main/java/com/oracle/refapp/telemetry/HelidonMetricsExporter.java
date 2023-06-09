/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.telemetry;

import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import com.oracle.refapp.service.TelemetryService;
import io.helidon.metrics.api.RegistryFactory;
import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.metrics.ConcurrentGauge;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.Metric;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.SimpleTimer;
import org.eclipse.microprofile.metrics.Snapshot;
import org.eclipse.microprofile.metrics.Timer;

@Slf4j
@ApplicationScoped
public class HelidonMetricsExporter {

  private static final UnitConverter STORAGE_UNIT_CONVERTER = UnitConverter.storageUnitConverter();
  private static final UnitConverter TIME_UNIT_CONVERTER = UnitConverter.timeUnitConverter();
  private static final List<UnitConverter> UNIT_CONVERTERS = List.of(STORAGE_UNIT_CONVERTER, TIME_UNIT_CONVERTER);
  private static final NameFormatter DEFAULT_NAME_FORMATTER = new NameFormatter() {};
  private final TelemetryService telemetryService;
  private final Map<MetricRegistry, MetricRegistry.Type> metricRegistries = new HashMap<>();

  @Inject
  public HelidonMetricsExporter(TelemetryService telemetryService) {
    this.telemetryService = telemetryService;

    RegistryFactory rf = RegistryFactory.getInstance();
    this.metricRegistries.put(rf.getRegistry(Type.BASE), Type.BASE);
    this.metricRegistries.put(rf.getRegistry(Type.VENDOR), Type.VENDOR);
  }

  @FixedRate(initialDelay = 10, value = 45, timeUnit = TimeUnit.SECONDS)
  public void pushMetrics() {
    if (log.isDebugEnabled()) log.debug("Starting HelidonMetricsExporter#pushMetrics");

    List<MetricDataDetails> allMetricDataDetails = new ArrayList<>();

    for (MetricRegistry metricRegistry : metricRegistries.keySet()) {
      metricRegistry
        .getMetrics()
        .entrySet()
        .stream()
        .flatMap(entry -> metricDataDetails(metricRegistry, entry.getKey(), entry.getValue()))
        .forEach(allMetricDataDetails::add);
    }

    PostMetricDataDetails postMetricDataDetails = PostMetricDataDetails
      .builder()
      .metricData(allMetricDataDetails)
      .build();

    PostMetricDataRequest postMetricDataRequest = PostMetricDataRequest
      .builder()
      .postMetricDataDetails(postMetricDataDetails)
      .build();

    if (allMetricDataDetails.size() > 0) {
      if (log.isDebugEnabled()) log.debug("Pushing {} metrics to OCI", allMetricDataDetails.size());
      this.telemetryService.postMetricData(postMetricDataRequest);
    }
  }

  private Stream<MetricDataDetails> metricDataDetails(MetricRegistry metricRegistry, MetricID metricId, Metric metric) {
    if (metric instanceof Counter) {
      return forCounter(metricRegistry, metricId, ((Counter) metric));
    } else if (metric instanceof ConcurrentGauge) {
      return forConcurrentGauge(metricRegistry, metricId, ((ConcurrentGauge) metric));
    } else if (metric instanceof Meter) {
      return forMeter(metricRegistry, metricId, ((Meter) metric));
    } else if (metric instanceof Gauge<?>) {
      return forGauge(metricRegistry, metricId, ((Gauge<? extends Number>) metric));
    } else if (metric instanceof SimpleTimer) {
      return forSimpleTimer(metricRegistry, metricId, ((SimpleTimer) metric));
    } else if (metric instanceof Timer) {
      return forTimer(metricRegistry, metricId, ((Timer) metric));
    } else if (metric instanceof Histogram) {
      return forHistogram(metricRegistry, metricId, ((Histogram) metric));
    } else {
      return Stream.empty();
    }
  }

  private Stream<MetricDataDetails> forCounter(MetricRegistry metricRegistry, MetricID metricId, Counter counter) {
    return Stream.of(metricDataDetails(metricRegistry, metricId, null, counter.getCount()));
  }

  private Stream<MetricDataDetails> forConcurrentGauge(
    MetricRegistry metricRegistry,
    MetricID metricId,
    ConcurrentGauge concurrentGauge
  ) {
    Stream.Builder<MetricDataDetails> result = Stream.builder();
    long count = concurrentGauge.getCount();
    result.add(metricDataDetails(metricRegistry, metricId, null, count));
    if (count > 0) {
      result.add(metricDataDetails(metricRegistry, metricId, "min", concurrentGauge.getMin()));
      result.add(metricDataDetails(metricRegistry, metricId, "max", concurrentGauge.getMax()));
    }
    return result.build();
  }

  private Stream<MetricDataDetails> forMeter(MetricRegistry metricRegistry, MetricID metricId, Meter meter) {
    Stream.Builder<MetricDataDetails> result = Stream.builder();
    long count = meter.getCount();
    result.add(metricDataDetails(metricRegistry, metricId, "total", count));
    if (count > 0) {
      result.add(metricDataDetails(metricRegistry, metricId, "gauge", meter.getMeanRate()));
    }
    return result.build();
  }

  private Stream<MetricDataDetails> forGauge(
    MetricRegistry metricRegistry,
    MetricID metricId,
    Gauge<? extends Number> gauge
  ) {
    return Stream.of(metricDataDetails(metricRegistry, metricId, null, gauge.getValue().doubleValue()));
  }

  private Stream<MetricDataDetails> forSimpleTimer(
    MetricRegistry metricRegistry,
    MetricID metricId,
    SimpleTimer simpleTimer
  ) {
    Stream.Builder<MetricDataDetails> result = Stream.builder();
    long count = simpleTimer.getCount();
    result.add(metricDataDetails(metricRegistry, metricId, "total", count));
    if (count > 0) {
      result.add(
        metricDataDetails(metricRegistry, metricId, "elapsedTime_seconds", simpleTimer.getElapsedTime().toSeconds())
      );
    }
    return result.build();
  }

  private Stream<MetricDataDetails> forTimer(MetricRegistry metricRegistry, MetricID metricId, Timer timer) {
    Stream.Builder<MetricDataDetails> result = Stream.builder();
    long count = timer.getCount();
    result.add(metricDataDetails(metricRegistry, metricId, "seconds_count", count));
    if (count > 0) {
      Snapshot snapshot = timer.getSnapshot();
      result.add(metricDataDetails(metricRegistry, metricId, "mean_seconds", snapshot.getMean()));
      result.add(metricDataDetails(metricRegistry, metricId, "min_seconds", snapshot.getMin()));
      metricDataDetails(metricRegistry, metricId, "max_seconds", snapshot.getMax());
    }
    return result.build();
  }

  private Stream<MetricDataDetails> forHistogram(
    MetricRegistry metricRegistry,
    MetricID metricId,
    Histogram histogram
  ) {
    Stream.Builder<MetricDataDetails> result = Stream.builder();
    long count = histogram.getCount();
    Metadata metadata = metricRegistry.getMetadata().get(metricId.getName());
    String units = metadata.getUnit();
    String unitsPrefix = units != null && !Objects.equals(units, MetricUnits.NONE) ? units + "_" : "";
    String unitsSuffix = units != null && !Objects.equals(units, MetricUnits.NONE) ? "_" + units : "";
    result.add(metricDataDetails(metricRegistry, metricId, unitsPrefix + "count", count));
    if (count > 0) {
      Snapshot snapshot = histogram.getSnapshot();
      result.add(metricDataDetails(metricRegistry, metricId, "mean" + unitsSuffix, snapshot.getMean()));
      result.add(metricDataDetails(metricRegistry, metricId, "min" + unitsSuffix, snapshot.getMin()));
      metricDataDetails(metricRegistry, metricId, "max" + unitsSuffix, snapshot.getMax());
    }
    return result.build();
  }

  private MetricDataDetails metricDataDetails(
    MetricRegistry metricRegistry,
    MetricID metricId,
    String suffix,
    double value
  ) {
    if (Double.isNaN(value)) {
      return null;
    }

    Metadata metadata = metricRegistry.getMetadata().get(metricId.getName());
    Map<String, String> dimensions = dimensions(metricId, metricRegistry);
    List<Datapoint> datapoints = datapoints(metadata, value);
    if (log.isDebugEnabled()) {
      log.debug(
        "Metric data details will be sent with the following values: name={} , dimensions={}, " +
        "datapoints.timestamp={} datapoints.value={}",
        DEFAULT_NAME_FORMATTER.format(metricId, suffix, metadata),
        dimensions,
        datapoints.get(0).getTimestamp(),
        datapoints.get(0).getValue()
      );
    }
    return MetricDataDetails
      .builder()
      .name(DEFAULT_NAME_FORMATTER.format(metricId, suffix, metadata))
      .metadata(ociMetadata(metadata))
      .datapoints(datapoints)
      .dimensions(dimensions)
      .namespace(telemetryService.getNamespace())
      .resourceGroup(telemetryService.getResourceGroup())
      .compartmentId(telemetryService.getCompartmentId())
      .build();
  }

  private Map<String, String> dimensions(MetricID metricId, MetricRegistry metricRegistry) {
    String registryType = metricRegistries.get(metricRegistry).getName();
    Map<String, String> result = new HashMap<>(metricId.getTags());
    result.put("scope", registryType);
    return result;
  }

  private List<Datapoint> datapoints(Metadata metadata, double value) {
    return Collections.singletonList(
      Datapoint.builder().value(convertUnits(metadata.getUnit(), value)).timestamp(new Date()).build()
    );
  }

  private double convertUnits(String metricUnits, double value) {
    for (UnitConverter converter : UNIT_CONVERTERS) {
      if (converter.handles(metricUnits)) {
        return converter.convert(metricUnits, value);
      }
    }
    return value;
  }

  private Map<String, String> ociMetadata(Metadata metadata) {
    return (metadata.getDescription() != null && !metadata.getDescription().isEmpty())
      ? Collections.singletonMap(
        "description",
        metadata.getDescription().length() <= 256
          ? metadata.getDescription()
          // trim metadata value as oci metadata.value has a maximum of 256 characters
          : metadata.getDescription().substring(0, 256)
      )
      : null;
  }

  public interface NameFormatter {
    default String format(MetricID metricId, String suffix, Metadata metadata) {
      MetricType metricType = metadata.getTypeRaw();

      StringBuilder result = new StringBuilder(metricId.getName());
      if (suffix != null) {
        result.append("_").append(suffix);
      }
      result.append("_").append(metricType);

      String units = formattedBaseUnits(metadata.getUnit());
      if (units != null && !units.isBlank()) {
        result.append("_").append(units);
      }
      return result.toString();
    }
  }

  private static String formattedBaseUnits(String metricUnits) {
    String baseUnits = baseMetricUnits(metricUnits);
    return baseUnits == null ? "" : baseUnits;
  }

  private static String baseMetricUnits(String metricUnits) {
    if (!MetricUnits.NONE.equals(metricUnits) && !metricUnits.isEmpty()) {
      for (UnitConverter converter : UNIT_CONVERTERS) {
        if (converter.handles(metricUnits)) {
          return converter.baseUnits();
        }
      }
    }
    return null;
  }
}
