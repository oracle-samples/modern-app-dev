/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Provider } from '.';
import { h } from 'preact';
import { queryClient, snapshot } from '../../../../test/jetHelper';
import { MemoryRouter } from 'react-router-dom';
import { AuthContext } from '../../../utils/authProvider';
import { QueryClientProvider } from '@tanstack/react-query';
import { User } from 'api/frontendApi';

const user: User = {
  role: 'PROVIDER',
  username: 'jsmith'
};

snapshot(
  'Provider',
  <QueryClientProvider client={queryClient}>
    <AuthContext.Provider value={{ user, signin: () => Promise.resolve(user), signout: () => console.log('signout') }}>
      <MemoryRouter>
        <Provider />
      </MemoryRouter>
    </AuthContext.Provider>
  </QueryClientProvider>
);
