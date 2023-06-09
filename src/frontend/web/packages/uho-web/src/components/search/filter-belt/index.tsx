/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import { Filter } from '..';
import { FilterChip } from './filter-chip';
import 'ojs/ojconveyorbelt';

type Props = {
  filters: Filter[];
  updateFilter: (key: string, value: boolean) => void;
};

export function FilterBelt({ filters, updateFilter }: Props) {
  const onFilterAdded = useCallback(
    (key: string) => {
      updateFilter(key, true);
    },
    [updateFilter]
  );
  const key = filters.map((f) => f.key).join('_');
  return (
    <oj-conveyor-belt key={key}>
      {filters.map((filter) => {
        return <FilterChip key={filter.key} filter={filter} onClick={onFilterAdded} />;
      })}
    </oj-conveyor-belt>
  );
}
