/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.controllers;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.file.FileSystemResourceLoader;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class ClientForwardController {

  private final String defaultPath;
  private final ResourceLoader resourceLoader;

  public ClientForwardController(
    @Value("${micronaut.router.static-resources.default.paths}") List<String> defaultPaths
  ) {
    this.defaultPath = defaultPaths.get(0);
    ResourceResolver resourceResolver = new ResourceResolver();
    if (this.defaultPath.startsWith("file:")) {
      resourceLoader = resourceResolver.getLoader(FileSystemResourceLoader.class).orElseThrow();
    } else {
      resourceLoader = resourceResolver.getLoader(ClassPathResourceLoader.class).orElseThrow();
    }
    log.info("ClientForwardController initialized for defaultPath: {}", this.defaultPath);
  }

  /**
   * Forwards any unmapped paths (except those containing a period) to the client {@code index.html}.
   * @return forward to client {@code index.html}.
   */
  @Get("/{path:[^.]*}")
  @Secured(SecurityRule.IS_ANONYMOUS)
  public Optional<StreamedFile> forward(String path) {
    log.trace("Redirecting from {} to /index.html", path);
    return resourceLoader.getResource(defaultPath + "/index.html").map(StreamedFile::new);
  }
}
