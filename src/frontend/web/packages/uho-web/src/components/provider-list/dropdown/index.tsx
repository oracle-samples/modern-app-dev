/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ojSelectSingle } from 'ojs/ojselectsingle';
import 'ojs/ojselectsingle';
import 'ojs/ojlistitemlayout';
import 'ojs/ojhighlighttext';
import { h } from 'preact';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { providerApi } from 'api';
import { ProviderSummary } from '@uho/provider-api-client/dist/api-client';
import { useMemo } from 'preact/hooks';
import { ItemContext } from 'ojs/ojcommontypes';

type Props = Readonly<{
  providerId?: number;
  onvalueChanged?: (providerId?: number) => void;
}>;

export function ProviderDropdown({ providerId, onvalueChanged }: Props) {
  const { dataProvider, error } = useQueryDataProvider<string, ProviderSummary>(
    ['provider_dropdown'],
    async () => {
      return (await providerApi.listProviders({})).items || [];
    },
    'id',
    { enabled: true },
    false
  );

  const providerRender = useMemo(
    () => (context: ojSelectSingle.ItemTemplateContext<string, ProviderSummary>) => {
      const name = `${context.data.firstName} ${context.data.lastName}`;
      return (
        <oj-list-item-layout class="oj-listitemlayout-padding-off">
          <span class="oj-typography-body-md oj-text-color-primary">
            <oj-highlight-text text={name} matchText={context.searchText || ''} />
          </span>
          <span slot="secondary" class="oj-typography-body-sm oj-text-color-secondary">
            <oj-highlight-text text={context.data.email} matchText={context.searchText || ''} />
          </span>
          <span slot="metadata" class="oj-typography-body-sm oj-text-color-secondary">
            <oj-highlight-text text={context.data.phone} matchText={context.searchText || ''} />
          </span>
        </oj-list-item-layout>
      );
    },
    []
  );

  const itemTextFn = useMemo(
    () => (context: ItemContext<string, ProviderSummary>) => {
      return `${context.data.firstName} ${context.data.lastName}`;
    },
    []
  );

  if (error || !dataProvider) {
    return <div>Cannot find providers</div>;
  }

  return (
    <oj-select-single
      labelHint="Select Provider"
      itemText={itemTextFn}
      onojValueAction={(evt) => {
        onvalueChanged?.(evt.detail.value);
      }}
      value={providerId}
      data={dataProvider}
    >
      <template slot="itemTemplate" render={providerRender} />
    </oj-select-single>
  );
}
