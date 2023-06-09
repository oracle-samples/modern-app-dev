/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useEffect } from 'preact/hooks';
import { useI18n } from 'hooks/useI18n';

export const DELIMITER = ' · ';

export default function useTitle(...titleParts: (string | undefined)[]) {
  const appName = useI18n().appName();
  useEffect(() => {
    const prevTitle = document.title;
    document.title = [...titleParts, appName].filter((elem) => !!elem && elem.length).join(DELIMITER);
    return () => {
      document.title = prevTitle;
    };
  }, [titleParts, appName]);
}
