/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useEffect, useMemo, useState } from 'preact/hooks';
import { FrameworkQueryKey, getFrameworkQuery } from 'ojs/ojresponsiveutils';
import { isAPISupported } from './isApiSupported';

export const useMediaQuery = (query: FrameworkQueryKey) => {
  /* eslint-disable react-hooks/rules-of-hooks */
  if (!isAPISupported('matchMedia')) {
    return null;
  }
  const frameworkQuery = useMemo(() => getFrameworkQuery(query) || '', [query]);
  const [match, setMatch] = useState(!!window.matchMedia(frameworkQuery).matches);

  useEffect(() => {
    const mediaQueryChangeListener = (event: MediaQueryListEvent) => {
      setMatch(event.matches);
    };
    const mediaQueryList = window.matchMedia(frameworkQuery);
    mediaQueryList.addEventListener('change', mediaQueryChangeListener);
    setMatch(mediaQueryList.matches);
    return () => mediaQueryList.removeEventListener('change', mediaQueryChangeListener);
  }, [frameworkQuery]);
  return match;
};
