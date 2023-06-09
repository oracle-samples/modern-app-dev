/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.mock;

import com.oracle.bmc.monitoring.model.Datapoint;
import com.oracle.bmc.monitoring.requests.PostMetricDataRequest;
import com.oracle.refapp.service.TelemetryService;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.opentracing.Traced;

@Alternative
@Priority(1)
@ApplicationScoped
public class MockTelemetryService extends TelemetryService {

  public MockTelemetryService() throws IOException {
    super(false, false, "ocid1.compartment.oc1..mock-comp-id", "uho-feedback", "uho_feedback", "helidon_test");
  }

  @Override
  @Traced
  @Asynchronous
  public CompletionStage<Void> postMetricData(String metricName, List<Datapoint> dataPoints, String encounterId) {
    //noop
    return CompletableFuture.completedFuture(null);
  }

  @Override
  @Traced
  @Asynchronous
  public CompletionStage<Void> postMetricData(PostMetricDataRequest postMetricDataRequest) {
    //noop
    return CompletableFuture.completedFuture(null);
  }
}
