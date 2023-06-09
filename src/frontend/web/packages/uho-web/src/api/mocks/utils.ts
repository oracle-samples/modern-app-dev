/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */

type Options = {
  delay?: number;
  error?: Error;
};
const defaultDelay = 0;

export function mock<T>(data: T, options?: Options): Promise<T> {
  return new Promise<T>((resolve, reject) => {
    setTimeout(() => {
      options?.error ? reject(options.error) : resolve(data);
    }, options?.delay || defaultDelay);
  });
}
export function reject<T>(options?: Options): Promise<T> {
  return new Promise<T>((_, reject) => {
    setTimeout(() => {
      reject(options?.error);
    }, options?.delay || defaultDelay);
  });
}

export function mockDate() {
  return new Date(1654733487565);
}
