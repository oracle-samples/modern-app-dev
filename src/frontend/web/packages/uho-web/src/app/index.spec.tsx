/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { App } from '.';
import { h } from 'preact';
import { snapshot } from '../../test/jetHelper';
import { MemoryRouter } from 'react-router-dom';

snapshot(
  'App',
  <MemoryRouter>
    <App />
  </MemoryRouter>
);
