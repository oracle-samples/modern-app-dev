/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.telemetry;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.model.MetricDataDetails;
import com.oracle.bmc.monitoring.model.PostMetricDataDetails;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.ReflectiveAccess;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TelemetryClient {

  @ReflectiveAccess
  @Value("${micronaut.metrics.export.oraclecloud.compartment-id}")
  private String compartmentId;

  @ReflectiveAccess
  @Value("${micronaut.metrics.export.oraclecloud.namespace}")
  private String namespace;

  @ReflectiveAccess
  @Value("${micronaut.metrics.export.oraclecloud.resourceGroup}")
  private String resourceGroup;

  private MonitoringClient client;

  private static final Logger LOG = LoggerFactory.getLogger(TelemetryClient.class);

  public TelemetryClient(Environment environment) throws IOException {
    Region region;
    if (!environment.getActiveNames().contains("test")) {
      if (environment.getActiveNames().contains("dev")) {
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(
          configFile
        );
        client = new MonitoringClient(provider);
        region = provider.getRegion();
      } else {
        InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider
          .builder()
          .build();
        client = new MonitoringClient(provider);
        region = provider.getRegion();
      }
      String regionId = region.getRegionId();
      client.setEndpoint("https://telemetry-ingestion." + regionId + ".oraclecloud.com");
    }
  }

  public void postMetricData(String metricName, List<Datapoint> datapoints) {
    HashMap<String, String> metricHashMap = new HashMap<>();
    metricHashMap.put("app_name", "uho-appointment");

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
              .datapoints(datapoints)
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

    try {
      client.postMetricData(postMetricDataRequest);
    } catch (Exception e) {
      LOG.error("Error posting metrics ", e);
    }
  }
}
