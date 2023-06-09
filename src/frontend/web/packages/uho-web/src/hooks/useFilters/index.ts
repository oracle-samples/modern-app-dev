/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { StateUpdater, useEffect, useMemo, useState } from 'preact/hooks';
import { useSearchParams } from 'react-router-dom';
import { Filter } from 'components/search';

export const SEARCH_KEY = 'search';

export default function useFilters(
  filters: Filter[]
): [Filter[], StateUpdater<Filter[]>, string, StateUpdater<string>, Record<string, string | boolean>] {
  const [searchParams] = useSearchParams();
  const [searchFilter, setSearchFilter] = useState<string>(searchParams.get(SEARCH_KEY) || '');
  const [filterState, setFilterState] = useState<Filter[]>(() => {
    return filters.map((filter) => {
      const param = searchParams.get(filter.key);
      if (filter.type === 'boolean') {
        return {
          ...filter,
          value: param !== undefined ? param === 'true' : filter.value,
          selected: !!param
        };
      }
      return {
        ...filter,
        value: param || filter.value,
        selected: !!param
      };
    });
  });
  useUpdateSearchParams(filterState, searchFilter);

  const filter = useMemo(() => {
    const selectedFilters = filterState.filter((f) => f.selected);
    const params: Record<string, string | boolean> = {};
    selectedFilters.forEach((filter) => {
      if (filter.value === undefined) {
        return;
      }
      if (filter.type === 'boolean' && filter.negate) {
        // the filter is negated - we need to make sure to pass the opposite
        params[filter.field] = !filter.value;
      } else {
        params[filter.field] = filter.value;
      }
    });
    if (searchFilter) {
      params[SEARCH_KEY] = searchFilter;
    }
    return params;
  }, [filterState, searchFilter]);

  return [filterState, setFilterState, searchFilter, setSearchFilter, filter];
}

function useUpdateSearchParams(filters: Filter[], searchFilter: string) {
  const [_, setSearchParams] = useSearchParams();
  useEffect(() => {
    const params: Record<string, string> = {};
    if (searchFilter.length) {
      params[SEARCH_KEY] = searchFilter;
    }
    for (const k in filters) {
      const filter = filters[k];
      if (filter.selected) {
        params[filter.key] = String(filter.value);
      }
    }
    setSearchParams(params);
  }, [searchFilter, setSearchParams, filters]);
}
