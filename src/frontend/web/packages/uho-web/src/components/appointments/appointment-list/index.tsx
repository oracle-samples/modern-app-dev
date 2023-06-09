/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojlistview';
import 'ojs/ojactioncard';
import { ListItemLayout } from 'oj-c/list-item-layout';
import { ojListView } from 'ojs/ojlistview';
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { AppointmentSummary } from '@uho/appointment-api-client/dist/api-client';
import { appointmentApi } from 'api';
import { useI18n } from 'hooks/useI18n';

type Props = {
  onSelection?: (appointment: number) => void;
  selected?: number;
  refreshIndex?: number;
};

export function AppointmentList({ onSelection, selected, refreshIndex }: Props) {
  const i18n = useI18n().appointments;

  const { dataProvider, error, isError, data } = useQueryDataProvider<AppointmentSummary['id'], AppointmentSummary>(
    ['appointments', refreshIndex],
    async () => {
      return (await appointmentApi.listAppointments({})).items || [];
    },
    'id'
  );

  const handleSelectedChanged = useCallback(
    (event: ojListView.selectionChanged<AppointmentSummary['id'], AppointmentSummary>) => {
      if (event.detail.updatedFrom !== 'external') {
        // to prevent endless calls
        onSelection?.(event.detail.value[0]!);
      }
    },
    [onSelection]
  );

  const renderItem = useCallback(
    (item: ojListView.ItemContext<number, AppointmentSummary>) => {
      return (
        <ListItemLayout>
          <div class="oj-typography-body-md oj-typography-bold">{item.data.startTime?.toDateString()}</div>
          <div slot="secondary" class="oj-typography-body-sm">
            {item.data.preVisitData}
          </div>
          <div slot="tertiary" class="oj-typography-body-xs oj-text-color-secondary">
            {i18n.provider(String(item.data.providerId))}
          </div>
          <div slot="metadata" class="oj-typography-body-xs oj-text-color-secondary">
            {item.data.status}
          </div>
        </ListItemLayout>
      );
    },
    [i18n]
  );

  if (isError || !dataProvider) {
    return <div>An error occured: {JSON.stringify(error)}</div>;
  }
  return (
    <oj-list-view
      onselectionChanged={handleSelectedChanged}
      selection={[selected]}
      selectionMode="single"
      aria-label="list of appointments"
      data={dataProvider}
    >
      <template slot="itemTemplate" render={renderItem} />
    </oj-list-view>
  );
}
