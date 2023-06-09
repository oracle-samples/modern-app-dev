/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojselectsingle';
import { ojSelectSingle } from 'ojs/ojselectsingle';
import 'ojs/ojhighlighttext';
import 'ojs/ojlistitemlayout';
import { h } from 'preact';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { patientApi } from 'api';
import { useMemo } from 'preact/hooks';
import { ItemContext } from 'ojs/ojcommontypes';
import { PatientSummary } from '@uho/patient-api-client/dist/api-client';

type Props = Readonly<{
  patientId?: number;
  onvalueChanged?: (patientId?: number) => void;
}>;

export function PatientDropdown({ patientId, onvalueChanged }: Props) {
  const { dataProvider, error } = useQueryDataProvider<string, PatientSummary>(
    ['patient_dropdown'],
    async () => (await patientApi.listPatients({})).items || [],
    'id'
  );

  const patientRender = useMemo(
    () => (context: ojSelectSingle.ItemTemplateContext<string, PatientSummary>) => {
      return (
        <oj-list-item-layout class="oj-listitemlayout-padding-off">
          <span class="oj-typography-body-md oj-text-color-primary">
            <oj-highlight-text text={context.data.name} matchText={context.searchText || ''} />
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

  const itemTextFn = (context: ItemContext<string, PatientSummary>) => {
    return context.data.name!;
  };

  if (error || !dataProvider) {
    return <div>Cannot find patients</div>;
  }

  return (
    <oj-select-single
      labelHint="Select Patient"
      itemText={itemTextFn}
      onojValueAction={(evt) => {
        onvalueChanged?.(evt.detail.value);
      }}
      value={patientId}
      data={dataProvider}
    >
      <template slot="itemTemplate" render={patientRender} />
    </oj-select-single>
  );
}
