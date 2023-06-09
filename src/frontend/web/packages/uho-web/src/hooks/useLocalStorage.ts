/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { error, info } from 'ojs/ojlogger';
import { StateUpdater, useState } from 'preact/hooks';

// based on https://github.com/gragland/usehooks/blob/master/src/pages/useLocalStorage.md
export function useLocalStorage<T>(key: string, initialValue?: T) {
  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (err) {
      error(err);
      return initialValue;
    }
  });

  const setValue = (value: T) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      if (valueToStore) {
        localStorage.setItem(key, JSON.stringify(valueToStore));
      } else {
        localStorage.removeItem(key);
      }
    } catch (error) {
      info(error);
    }
  };

  return [storedValue, setValue] as [T, StateUpdater<T>];
}
