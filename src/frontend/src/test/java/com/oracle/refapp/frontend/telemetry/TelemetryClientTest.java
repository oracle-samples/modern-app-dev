/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.telemetry;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.oracle.bmc.monitoring.MonitoringClient;
import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.responses.PostMetricDataResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

@MicronautTest
public class TelemetryClientTest {

  @Inject
  TelemetryClient telemetryClient;

  String testMetricName = "dummy-metric";

  @Test
  void testPostMetricData() throws NoSuchFieldException, IllegalAccessException {
    Field clientField = TelemetryClient.class.getDeclaredField("client");
    clientField.setAccessible(true);
    MonitoringClient monitoringClient = mock(MonitoringClient.class);
    clientField.set(telemetryClient, monitoringClient);
    PostMetricDataResponse response = PostMetricDataResponse.builder().build();
    when(monitoringClient.postMetricData(any())).thenReturn(response);
    List<Datapoint> datapoints = List.of(Datapoint.builder().build());
    telemetryClient.postMetricData(testMetricName, datapoints);
    verify(monitoringClient, times(1)).postMetricData(any());
    verifyNoMoreInteractions(monitoringClient);
  }
}
