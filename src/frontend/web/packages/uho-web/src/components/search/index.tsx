/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useCallback, useMemo } from 'preact/hooks';
import { SearchBar } from './search-bar';
import { FilterBelt } from './filter-belt';

export type BaseFilter = {
  key: string;
  label: string;
  field: string;
  value: string | boolean;
  type: 'text' | 'boolean' | 'enum';
  selected: boolean;
};

export type StringFilter = BaseFilter & {
  type: 'text';
  value: string;
};

export type BooleanFilter = BaseFilter & {
  type: 'boolean';
  negate?: boolean;
  value: boolean;
};

export type EnumFilter = BaseFilter & {
  type: 'enum';
  values: { value: string; label: string }[];
  value: string;
};

export type Filter = StringFilter | BooleanFilter | EnumFilter;

type Props = {
  placeholder: string;
  search?: string;
  filters: Filter[];
  onSearchUpdated?: (search: string) => void;
  onFiltersUpdated?: (filters: Filter[]) => void;
};

export function Search({ filters, placeholder, search, onSearchUpdated, onFiltersUpdated }: Props) {
  const [inactiveFilters, activeFilters] = useMemo(() => {
    const activeFilters: Filter[] = [];
    const inactiveFilters: Filter[] = [];
    filters.forEach((filter) => {
      filter.selected ? activeFilters.push(filter) : inactiveFilters.push(filter);
    });
    return [inactiveFilters, activeFilters];
  }, [filters]);

  const updateFilter = useCallback(
    (key: string, selected: boolean) => {
      const filter = filters.find((filter) => filter.key === key);
      if (filter) {
        filter.selected = selected;
        onFiltersUpdated?.([...filters]);
      }
    },
    [filters, onFiltersUpdated]
  );

  return (
    <div class="oj-sm-margin-2x-top oj-flex oj-sm-flex-direction-column search">
      <div class="oj-flex-item">
        <SearchBar
          filters={activeFilters}
          placeholder={placeholder}
          search={search}
          onSearchUpdated={onSearchUpdated}
          updateFilter={updateFilter}
        />
      </div>
      <div class="oj-flex-item oj-sm-padding-2x-top">
        <FilterBelt filters={inactiveFilters} updateFilter={updateFilter} />
      </div>
    </div>
  );
}
