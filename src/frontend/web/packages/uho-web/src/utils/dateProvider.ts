/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

// extracted such that the method can be mocked in tests for consistent snapshot tests
export function now() {
  return new Date();
}
