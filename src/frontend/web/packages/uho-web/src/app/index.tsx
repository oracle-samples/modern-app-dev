/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import Context from 'ojs/ojcontext';
import { h } from 'preact';
import { Landing } from './landing';
import { useCallback, useEffect, useState } from 'preact/hooks';
import { Navigate, Route, Routes } from 'react-router-dom';
import { Patient } from './content/patient';
import { Provider } from './content/provider';
import { AuthProvider, RequireAuth, UserRole } from '../utils/authProvider';
import { NotFound } from './404';
import { useApmRum } from 'hooks/useApmRum';
import { frontendApi } from 'api';

export function App() {
  const [loaded, setLoaded] = useState(false);
  useApmRum();
  useEffect(() => {
    const init = () => {
      Context.getPageContext().getBusyContext().applicationBootstrapComplete();
      setLoaded(true);
    };
    init();
  }, []);

  const onSelection = useCallback((selection: UserRole) => {
    // eslint-disable-next-line no-undef
    frontendApi.login(selection);
  }, []);

  return (
    <div id="app" class="oj-web-applayout-page" style={{ display: loaded ? undefined : 'none' }}>
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Landing onSelection={onSelection} />} />
          <Route path="/404" element={<NotFound />} />
          <Route
            path="patient/*"
            element={
              <RequireAuth role="PATIENT">
                <Patient />
              </RequireAuth>
            }
          />
          <Route
            path="provider/*"
            element={
              <RequireAuth role="PROVIDER">
                <Provider />
              </RequireAuth>
            }
          />
          <Route path="*" element={<Navigate to="/404" />} />
        </Routes>
      </AuthProvider>
    </div>
  );
}
