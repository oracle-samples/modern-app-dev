/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { renderHook, waitFor } from '@testing-library/preact';
import { useApmRum } from '.';

describe('useApmRum hook', () => {
  it('should add apm rum to the dom', async () => {
    expect(document.body.querySelector('script')).toBeFalsy();
    const { result, unmount, rerender } = renderHook(() => useApmRum());
    rerender();
    await waitFor(() => {
      expect(result.current?.appended).toBe(true);
    });
    expect(document.body.querySelector('script')).toBeTruthy();
    unmount();
    expect(document.body.querySelector('script')).toBeFalsy();
  });
});
