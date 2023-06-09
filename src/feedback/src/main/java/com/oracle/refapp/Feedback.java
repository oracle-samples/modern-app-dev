/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp;

import io.helidon.microprofile.server.Server;
import java.io.IOException;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Feedback {

  public static void main(final String[] args) throws IOException {
    setupLogging();
    startServer();
  }

  // use slf4j for JUL as well
  private static void setupLogging() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  private static Server startServer() {
    return Server.create().start();
  }
}
