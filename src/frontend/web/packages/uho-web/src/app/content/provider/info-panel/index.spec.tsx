/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { InfoPanel } from '.';
import { h } from 'preact';
import { snapshot } from '../../../../../test/jetHelper';
import { MemoryRouter } from 'react-router-dom';
import { AuthContext } from '../../../../utils/authProvider';
import { User } from 'api/frontendApi';

const user: User = {
  role: 'PROVIDER',
  username: 'jsmith'
};

snapshot(
  'Provider: InfoPanel',
  <AuthContext.Provider value={{ user, signin: () => Promise.resolve(user), signout: () => console.log('signout') }}>
    <MemoryRouter>
      <InfoPanel />
    </MemoryRouter>
  </AuthContext.Provider>
);
