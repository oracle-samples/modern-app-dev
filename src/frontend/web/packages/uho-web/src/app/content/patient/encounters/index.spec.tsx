/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Encounters } from '.';
import { h } from 'preact';
import { queryClient, snapshot } from '../../../../../test/jetHelper';
import { MemoryRouter } from 'react-router-dom';
import { AuthContext } from '../../../../utils/authProvider';
import { QueryClientProvider } from '@tanstack/react-query';
import { User } from 'api/frontendApi';

const user: User = {
  role: 'PATIENT'
};

snapshot(
  'Patient: Encounters',
  <QueryClientProvider client={queryClient}>
    <AuthContext.Provider value={{ user, signin: () => Promise.resolve(user), signout: () => console.log('signout') }}>
      <MemoryRouter>
        <Encounters
        // location={{} as unknown as Props['location']}
        // history={{} as unknown as Props['history']}
        // match={{ params: { encounterId: '1' }, isExact: true, path: '/encounters', url: 'url' }}
        />
      </MemoryRouter>
    </AuthContext.Provider>
  </QueryClientProvider>
);
