/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { render, cleanup } from '@testing-library/preact';
import userEvent from '@testing-library/user-event';
import { h, ComponentChildren } from 'preact';
import { getContext } from 'ojs/ojcontext';
import { QueryClient } from '@tanstack/react-query';

import bundle from '@oracle/oraclejet-preact/resources/nls/bundle';
import appBundle from '../src/nls/en-US';
import {
  RootEnvironment,
  RootEnvironmentProvider,
  TranslationBundle
} from '@oracle/oraclejet-preact/UNSAFE_Environment';

const environment: Partial<RootEnvironment> = {
  translations: {
    '@oracle/oraclejet-preact': bundle,
    app: appBundle as unknown as TranslationBundle
  }
};

/* eslint-disable jest/no-export */
export function renderWithContext(component: ComponentChildren) {
  return render(
    <RootEnvironmentProvider environment={environment}>
      <div data-oj-binding-provider="preact">{component}</div>
    </RootEnvironmentProvider>
  );
}

export async function setup(component: ComponentChildren) {
  const result = {
    user: userEvent.setup(),
    ...renderWithContext(component)
  };
  await getContext(result.container).getBusyContext().whenReady();
  return result;
}

export function snapshot(name: string, component: ComponentChildren, delay = 1000) {
  describe(`Snapshot tests for ${name}`, () => {
    afterEach(cleanup);
    test(`Snapshot: Component - ${name}`, async () => {
      const { asFragment } = await setup(component);
      await sleep(delay);
      expect(asFragment()).toMatchSnapshot();
    });
  });
}

export async function flush() {
  return sleep(0);
}

export async function sleep(delay = 0) {
  return new Promise((res) => setTimeout(res, delay));
}

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      keepPreviousData: true,
      refetchOnWindowFocus: false,
      retry: 0
    }
  }
});
