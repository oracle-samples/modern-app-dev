/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import 'ojs/ojinputtext';
import { ojInputText } from 'ojs/ojinputtext';
import { useCallback, useRef } from 'preact/hooks';
import { InputSearchElementEventMap } from 'ojs/ojinputsearch';
import { Filter } from '..';
import { SearchChip } from './search-chip';
import './styles.scss';

type Props = {
  placeholder: string;
  onSearchUpdated?: (search: string) => void;
  search?: string;
  filters: Filter[];
  updateFilter: (key: string, value: boolean) => void;
};

export function SearchBar({ placeholder, search, onSearchUpdated, filters, updateFilter }: Props) {
  const inputTextRef = useRef<ojInputText>(null);
  const onSearchIconClicked = useCallback(() => {
    inputTextRef.current?.focus();
  }, []);

  const onFilterValueChanged = useCallback(
    (key: string, value: string | boolean) => {
      const filter = filters.find((filter) => filter.key === key);
      if (filter) {
        filter.value = value;
      }
      updateFilter(key, true);
    },
    [filters, updateFilter]
  );
  const onrawValueChanged = useCallback(
    (evt: InputSearchElementEventMap<string, string>['rawValueChanged']) => {
      onSearchUpdated?.(evt.detail.value || '');
    },
    [onSearchUpdated]
  );

  const onFilterRemoved = useCallback(
    (key: string) => {
      updateFilter(key, false);
    },
    [updateFilter]
  );

  return (
    <div class="oj-flex oj-sm-align-items-center search-bar oj-sm-padding-4x-horizontal">
      <div class="oj-sm-flex-0 oj-flex oj-sm-align-items-center oj-sm-margin-3x-end">
        <div
          aria-hidden="true"
          class="oj-text-color-primary smart-filters-icon-anchor"
          onClick={onSearchIconClicked}
          tabIndex={-1}
        >
          <span class="oj-ux-ico-search icon" />
        </div>
      </div>
      <div class="oj-sm-flex-1 search-bar-content">
        <div class="oj-flex oj-sm-align-items-center chips">
          <div class="oj-flex oj-sm-align-items-center">
            {filters.map((filter) => {
              return (
                <SearchChip
                  key={filter.key}
                  filter={filter}
                  onClose={onFilterRemoved}
                  onValueChanged={onFilterValueChanged}
                />
              );
            })}
          </div>
          <div class="oj-sm-flex-1 search-bar-input">
            <oj-input-text
              ref={inputTextRef}
              placeholder={placeholder}
              value={search}
              onrawValueChanged={onrawValueChanged}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
