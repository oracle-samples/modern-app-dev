/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.service;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@ApplicationScoped
@Slf4j
@Getter
@Setter
public class TelemetryService {

  private final MonitoringClient client;
  private final String compartmentId;
  private final String appName;
  private final String namespace;
  private final String resourceGroup;

  @Inject
  public TelemetryService(
    @ConfigProperty(name = "oci.monitoring.enabled", defaultValue = "true") boolean monitoringEnabled,
    @ConfigProperty(
      name = "oci.config.instance-principal.enabled",
      defaultValue = "false"
    ) boolean ociInstancePrincipalAuth,
    @ConfigProperty(name = "oci.monitoring.compartment-id") String compartmentId,
    @ConfigProperty(name = "oci.monitoring.appName") String appName,
    @ConfigProperty(name = "oci.monitoring.namespace") String namespace,
    @ConfigProperty(name = "oci.monitoring.resourceGroup") String resourceGroup
  ) throws IOException {
    this.compartmentId = compartmentId;
    this.resourceGroup = resourceGroup;
    this.namespace = namespace;
    this.appName = appName;

    if (log.isDebugEnabled()) {
      log.debug(
        "TelemetryClient initialized with compartmentId {}, namespace {} and resourceGroup {}",
        compartmentId,
        namespace,
        resourceGroup
      );
    }

    if (monitoringEnabled) {
      if (ociInstancePrincipalAuth) {
        InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider
          .builder()
          .build();
        client = new MonitoringClient(provider);
      } else {
        ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        client = new MonitoringClient(provider);
      }

      client.setEndpoint(client.getEndpoint().replace("telemetry.", "telemetry-ingestion."));

      if (log.isDebugEnabled()) log.debug("Monitoring endpoint is set to {} ", client.getEndpoint());
    } else {
      client = null;
      if (log.isDebugEnabled()) log.debug("Monitoring is not enabled, so MonitoringClient is not set!");
    }
  }

  @Traced
  @Asynchronous
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public CompletionStage<Void> postMetricData(String metricName, List<Datapoint> dataPoints, String encounterId) {
    HashMap<String, String> metricHashMap = new HashMap<>();
    metricHashMap.put("app_name", appName);
    metricHashMap.put("encounter_id", encounterId);

    PostMetricDataDetails postMetricDataDetails = PostMetricDataDetails
      .builder()
      .metricData(
        new ArrayList<>(
          List.of(
            MetricDataDetails
              .builder()
              .namespace(namespace)
              .resourceGroup(resourceGroup)
              .compartmentId(compartmentId)
              .name(metricName)
              .dimensions(metricHashMap)
              .datapoints(dataPoints)
              .build()
          )
        )
      )
      .batchAtomicity(PostMetricDataDetails.BatchAtomicity.Atomic)
      .build();

    PostMetricDataRequest postMetricDataRequest = PostMetricDataRequest
      .builder()
      .postMetricDataDetails(postMetricDataDetails)
      .opcRequestId(UUID.randomUUID().toString())
      .build();

    client.postMetricData(postMetricDataRequest);
    return CompletableFuture.completedFuture(null);
  }

  @Traced
  @Asynchronous
  @Retry(delay = 400, maxDuration = 5000, jitter = 200, maxRetries = 3)
  public CompletionStage<Void> postMetricData(PostMetricDataRequest postMetricDataRequest) {
    client.postMetricData(postMetricDataRequest);
    return CompletableFuture.completedFuture(null);
  }

  public void postFeedbackProcessedMetric(Double value, String encounterId) {
    List<Datapoint> dataPoints = new ArrayList<>(
      List.of(Datapoint.builder().timestamp(new Date(System.currentTimeMillis())).value(value).count(1).build())
    );
    this.postMetricData("feedback-processed", dataPoints, encounterId);
  }
}
