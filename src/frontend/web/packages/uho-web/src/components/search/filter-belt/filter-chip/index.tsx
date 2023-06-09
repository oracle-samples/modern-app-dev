/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import './styles.scss';
import { Filter } from '../..';
import { useCallback } from 'preact/hooks';

type FilterChipProps = {
  filter: Filter;
  onClick: (key: Filter['key']) => void;
};

export function FilterChip({ filter, onClick }: FilterChipProps) {
  const clickFilterCallback = useCallback(() => {
    onClick(filter.key);
  }, [onClick, filter.key]);

  const value = () => {
    switch (filter.type) {
      case 'boolean':
        return filter.value === true ? 'Yes' : 'No';
      case 'enum':
        return filter.values.find((val) => val.value == filter.value)?.label || filter.value;
      case 'text':
      default:
        return filter.value;
    }
  };

  return (
    <div onClick={clickFilterCallback} class="chip oj-flex oj-sm-align-items-center oj-sm-margin-2x-end">
      <div class="oj-flex-item filter-chip-label oj-sm-flex-wrap-nowrap oj-sm-justify-content-center oj-typography-body-sm oj-text-color-secondary">
        {filter.label}
      </div>
      <div class="oj-flex-item oj-sm-justify-content-center oj-sm-justify-content-center oj-sm-padding-1x-start oj-typography-body-sm oj-typography-semi-bold oj-text-color-primary">
        {value()}
      </div>
    </div>
  );
}
