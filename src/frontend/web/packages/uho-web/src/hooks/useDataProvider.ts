/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import CollectionDataProvider from 'ojs/ojcollectiondataprovider';
import MutableArrayDataProvider from 'ojs/ojmutablearraydataprovider';
import { useMemo } from 'preact/hooks';
import { DelayDataProvider } from '../utils/delayDataProvider';
import { Collection } from 'ojs/ojmodel';
import { QueryKey, RefetchOptions, RefetchQueryFilters, useQuery, UseQueryOptions } from '@tanstack/react-query';
import { DataProvider } from 'ojs/ojdataprovider';

interface URLOptions {
  fetchSize: number;
  startIndex: number;
}

export function useArrayDataProvider<T>(data: T[], keyAttributes: keyof T) {
  return useMemo(
    () => new MutableArrayDataProvider<string, T>(data, { keyAttributes: keyAttributes as string }),
    [data, keyAttributes]
  );
}

export function useCollectionDataProvider<K, T>(url: string) {
  return useMemo(() => {
    const collection = new Collection(undefined, {
      fetchSize: 25,
      customURL: (_operation: string, _collection: Collection, options: URLOptions) => {
        const params: {
          limit?: number;
          offset?: number;
        } = {};
        if (options.fetchSize > 0) {
          params.limit = options.fetchSize;
        }
        if (options.startIndex > 0) {
          const offset = Math.floor(options.startIndex / options.fetchSize) * options.fetchSize;
          params.offset = offset;
        }
        return generateGetQuery(url, params);
      }
    });
    return new CollectionDataProvider<K, T>(collection);
  }, [url]);
}

export function generateGetQuery<T extends object>(url: string, params: T) {
  const paramsMap = Object.entries(params).map(([key, val]) => {
    return `${key}=${encodeURIComponent(val as unknown as string | number | boolean)}`;
  });
  return `${url}?${paramsMap.join('&')}`;
}

type QueryResult<K, D> = {
  dataProvider: DataProvider<K, D> | null;
  error: Error | null;
  isLoading: boolean;
  isError: boolean;
  refetch: (options?: RefetchOptions & RefetchQueryFilters<D>) => void;
  data: D[] | undefined;
};

export function useDataProvider<K, D>(
  data: D[] | undefined,
  keyAttributes: keyof D | [keyof D],
  isLoading: boolean,
  isError: boolean,
  showLoading = true
) {
  const dataProvider = useMemo(
    () => new MutableArrayDataProvider<K, D>(data || [], { keyAttributes: keyAttributes as string }),
    [data, keyAttributes]
  );
  return useMemo(() => {
    if (showLoading && isLoading) {
      return new DelayDataProvider(dataProvider, 1000000);
    }
    if (isError) {
      return null;
    }
    return dataProvider;
  }, [dataProvider, showLoading, isLoading, isError]);
}

export function useQueryDataProvider<K, D>(
  queryKey: QueryKey,
  promise: () => Promise<D[]>,
  keyAttributes: keyof D,
  options?: Omit<UseQueryOptions<D[], Error, D[], QueryKey>, 'queryKey'>,
  showLoading = true
): QueryResult<K, D> {
  const { data, error, isError, isLoading, refetch } = useQuery<D[], Error>(queryKey, promise, options);
  return {
    dataProvider: useDataProvider(data, keyAttributes, isLoading, isError, showLoading),
    error,
    isError,
    isLoading,
    refetch,
    data
  };
}
