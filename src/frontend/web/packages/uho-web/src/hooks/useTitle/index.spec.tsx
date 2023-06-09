/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import useTitle, { DELIMITER } from '.';
import { renderHook } from '@testing-library/preact';
import { RootEnvironment, RootEnvironmentProvider } from '@oracle/oraclejet-preact/UNSAFE_Environment';

const appName = 'My App';
const environment: Partial<RootEnvironment> = {
  translations: {
    app: {
      appName: () => appName
    }
  }
};

function testRender(...title: string[]) {
  renderHook(() => useTitle(...title), {
    wrapper: ({ children }) => <RootEnvironmentProvider environment={environment}>{children}</RootEnvironmentProvider>
  });
}

describe('useTitle tests', () => {
  it('should update the title', () => {
    const title = 'Foo';
    expect(document.title).not.toBe(title);
    testRender(title);
    expect(document.title).toContain(title);
    expect(document.title).toContain(appName);
  });

  it('should list all titles', () => {
    const titles = ['Foo', 'Bar', 'Blub'];
    const joinedString = titles.join(DELIMITER);
    expect(document.title).not.toBe(titles);
    testRender(...titles);
    expect(document.title).toContain(joinedString);
    expect(document.title).toContain(appName);
  });
});
