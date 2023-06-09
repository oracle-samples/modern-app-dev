/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Landing } from '.';
import { h } from 'preact';
import { AuthProvider } from '../../utils/authProvider';
import { snapshot } from '../../../test/jetHelper';

jest.mock('react-router', () => ({
  useHistory: () => ({
    push: jest.fn()
  })
}));

snapshot(
  'Landing',
  <AuthProvider>
    <Landing onSelection={console.log} />
  </AuthProvider>
);
