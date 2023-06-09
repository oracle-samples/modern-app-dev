/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
package com.oracle.refapp.frontend.controllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micronaut.http.server.types.files.StreamedFile;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class ClientForwardControllerTest {

  @Test
  void testClientForwardControllerConstructorWithClasspathLoader() {
    List<String> defaultPaths = mock(List.class);
    when(defaultPaths.get(0)).thenReturn("static");
    new ClientForwardController(defaultPaths);
    verify(defaultPaths, times(1)).get(0);
  }

  @Test
  void testClientForwardControllerConstructorWithFileLoader() {
    List<String> defaultPaths = mock(List.class);
    when(defaultPaths.get(0)).thenReturn("file:static");
    new ClientForwardController(defaultPaths);
    verify(defaultPaths, times(1)).get(0);
  }

  @Test
  void testForward() {
    ClientForwardController clientForwardController = new ClientForwardController(List.of("static"));
    Optional<StreamedFile> response = clientForwardController.forward("test");
    assertNotNull(response);
  }
}
