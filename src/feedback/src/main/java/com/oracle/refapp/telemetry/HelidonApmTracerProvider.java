/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.telemetry;

import io.helidon.common.Prioritized;
import io.helidon.tracing.opentracing.spi.OpenTracingProvider;
import io.helidon.tracing.zipkin.ZipkinTracerBuilder;
import io.helidon.tracing.zipkin.ZipkinTracerProvider;
import jakarta.annotation.Priority;
import java.net.MalformedURLException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import zipkin2.reporter.urlconnection.URLConnectionSender;

/**
 * HelidonApmTracerProvider builds on provided ZipkinTracerProvider and allows to sender without port information
 * on ZipkinTracerBuilder before build is called.
 */
@Priority(Prioritized.DEFAULT_PRIORITY - 1)
@Slf4j
public class HelidonApmTracerProvider extends ZipkinTracerProvider implements OpenTracingProvider {

  @Override
  public ZipkinTracerBuilder createBuilder() {
    Config config = ConfigProvider.getConfig();
    //create() method is package level and create(Config) needs Helidon SE config so using forService
    ZipkinTracerBuilder zipkinTracerBuilder = ZipkinTracerBuilder.forService(
      config.getValue("tracing.service", String.class)
    );
    if (config.getValue("tracing.enabled", Boolean.class)) {
      //creating senderURI without port
      URI uri = URI.create(
        config.getValue("tracing.host", String.class) + config.getValue("tracing.path", String.class)
      );
      try {
        zipkinTracerBuilder.sender(URLConnectionSender.newBuilder().endpoint(uri.toURL()).build());
      } catch (MalformedURLException e) {
        log.error("Unable to send metrics to OCI: {}", e.getMessage());
        throw new IllegalArgumentException("Cannot set sender for zipkin: " + uri, e);
      }
    }
    return zipkinTracerBuilder;
  }
}
