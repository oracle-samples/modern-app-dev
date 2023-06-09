/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Legend } from '.';
import { h } from 'preact';
import { snapshot } from '../../../test/jetHelper';

snapshot(
  'Legend',
  <Legend
    elements={[
      { color: 'red', text: 'Foo' },
      { color: 'green', text: 'Bar' }
    ]}
    maxHeight={120}
    maxWidth={100}
  />
);
