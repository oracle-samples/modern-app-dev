/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import {
  ContainsKeysResults,
  DataProvider,
  FetchByKeysParameters,
  FetchByKeysResults,
  FetchByOffsetParameters,
  FetchByOffsetResults,
  FetchListParameters,
  FetchListResult
} from 'ojs/ojdataprovider';

const wrapPromise = <T>(promise: Promise<T>, delay: number) => {
  return new Promise<T>((resolve, reject) => {
    setTimeout(() => {
      promise.then(
        (result) => {
          resolve(result);
        },
        (reason) => {
          reject(reason);
        }
      );
    }, delay);
  });
};

class WrappingAsyncIterator<T> {
  constructor(public asyncIterator: AsyncIterator<T>, public delay: number) {}
  public next() {
    return wrapPromise(this.asyncIterator.next(), this.delay);
  }
}
export class DelayDataProvider<K, D> implements DataProvider<K, D> {
  constructor(public dataProvider: DataProvider<K, D>, public delay: number) {}

  addEventListener(eventType: string, listener: EventListener): void {
    return this.dataProvider.addEventListener(eventType, listener);
  }
  containsKeys(parameters: FetchByKeysParameters<K>): Promise<ContainsKeysResults<K>> {
    return wrapPromise(this.dataProvider.containsKeys(parameters), this.delay);
  }
  dispatchEvent(evt: Event): boolean {
    return this.dataProvider.dispatchEvent(evt);
  }
  fetchByKeys(parameters: FetchByKeysParameters<K>): Promise<FetchByKeysResults<K, D>> {
    return wrapPromise(this.dataProvider.fetchByKeys(parameters), this.delay);
  }
  fetchByOffset(parameters: FetchByOffsetParameters<D>): Promise<FetchByOffsetResults<K, D>> {
    return wrapPromise(this.dataProvider.fetchByOffset(parameters), this.delay);
  }
  fetchFirst(parameters?: FetchListParameters<D>): AsyncIterable<FetchListResult<K, D>> {
    const asyncIterable = this.dataProvider.fetchFirst(parameters);
    const asyncIterator = asyncIterable[Symbol.asyncIterator]();
    const wrappingAsyncIterator = new WrappingAsyncIterator(asyncIterator, this.delay);
    asyncIterable[Symbol.asyncIterator] = () => {
      return wrappingAsyncIterator;
    };
    return asyncIterable;
  }
  getCapability(capabilityName: string) {
    return this.dataProvider.getCapability(capabilityName);
  }
  getTotalSize(): Promise<number> {
    return this.dataProvider.getTotalSize();
  }
  isEmpty(): 'yes' | 'no' | 'unknown' {
    return this.dataProvider.isEmpty();
  }
  removeEventListener(eventType: string, listener: EventListener): void {
    return this.dataProvider.removeEventListener(eventType, listener);
  }
}
