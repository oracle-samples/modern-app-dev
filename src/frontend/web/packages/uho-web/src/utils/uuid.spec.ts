/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { generateGuid } from './uuid';

describe('UUID Tests', () => {
  it('should generate ids', () => {
    expect(generateGuid()).not.toBeNull();
  });
});
