/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
// Must be the first import
// https://preactjs.com/guide/v10/debugging/
if (process.env.NODE_ENV === 'development') {
  // Must use require here as import statements are only allowed
  // to exist at top-level.
  require('preact/debug');
}

import '../node_modules/@oracle/oraclejet/dist/css/redwood/oj-redwood.css';
import '../node_modules/@oracle/oraclejet-preact/es/Theme-redwood/theme.css';
import '../node_modules/@oracle/oraclejet-core-pack/oj-c/min/corepackbundle.css';
import './styles/themes/light.scss';

import { h, render } from 'preact';
import { App } from './app';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import {
  RootEnvironment,
  RootEnvironmentProvider,
  TranslationBundle
} from '@oracle/oraclejet-preact/UNSAFE_Environment';
import bundle from '@oracle/oraclejet-preact/resources/nls/bundle';
import appBundle from './nls/en-US';

const environment: Partial<RootEnvironment> = {
  translations: {
    '@oracle/oraclejet-preact': bundle,
    app: appBundle as unknown as TranslationBundle
  }
};

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      keepPreviousData: true,
      refetchOnWindowFocus: false,
      retry: 0
    }
  }
});

export const BASE_NAME = '/home';

render(
  <RootEnvironmentProvider environment={environment}>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter basename={BASE_NAME}>
        <App />
      </BrowserRouter>
    </QueryClientProvider>
  </RootEnvironmentProvider>,
  document.body
);
