/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h, ComponentChildren, createContext } from 'preact';
import { useCallback, useContext } from 'preact/hooks';
import { RouteProps } from 'react-router';
import { Navigate } from 'react-router-dom';
import { Loading } from 'components/loading';
import { useLocalStorage } from 'hooks/useLocalStorage';
import { frontendApi } from 'api';
import { User } from 'api/frontendApi';
import { useQuery } from '@tanstack/react-query';
import { warn } from 'ojs/ojlogger';

export type UserRole = 'PATIENT' | 'PROVIDER';

type Context = {
  user: User | null;
  signin: () => Promise<User | null>;
  signout: () => void;
};

function useProvideAuth(): Context {
  const [user, setUser] = useLocalStorage<User | null>('user');

  const signin = useCallback(async () => {
    let userInfo: User | null = null;
    try {
      userInfo = await frontendApi.getUserInformation();
    } catch {
      warn('No active user session found.');
    }
    setUser(userInfo);
    return userInfo;
  }, []);

  const signout = useCallback(async () => {
    setUser(null);
    return frontendApi.signout();
  }, []);

  return {
    user,
    signin,
    signout
  };
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
export const AuthContext = createContext<Context>(undefined!);

export function useAuth() {
  return useContext(AuthContext);
}

export function AuthProvider({ children }: { children: ComponentChildren }) {
  const auth = useProvideAuth();
  return <AuthContext.Provider value={auth}>{children}</AuthContext.Provider>;
}

export function RequireAuth({ children, role }: { children?: ComponentChildren; role: UserRole } & RouteProps) {
  const { signin } = useAuth();
  const { data, isLoading, isError } = useQuery<User | null, void>(['signin'], signin);

  if (isLoading) {
    return <Loading />;
  }
  if (!data || isError) {
    return <Navigate to="/" />;
  }
  if (data.role !== role) {
    return <Navigate to="/404" />;
  }
  return children;
}
