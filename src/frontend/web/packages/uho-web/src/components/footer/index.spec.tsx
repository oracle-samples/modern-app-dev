/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Footer, FooterElement } from '.';
import { h } from 'preact';
import { snapshot } from '../../../test/jetHelper';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '../../utils/authProvider';

const elements: FooterElement[] = [
  {
    id: '1',
    path: 'foo',
    title: 'foo',
    iconClass: 'icon'
  }
];
snapshot(
  'Footer',
  <AuthProvider>
    <MemoryRouter>
      <Footer elements={elements} />
    </MemoryRouter>
  </AuthProvider>
);
