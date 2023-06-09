/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojlistview';
import 'ojs/ojlistitemlayout';
import { ItemContext } from 'ojs/ojcommontypes';
import { ojListView } from 'ojs/ojlistview';
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import { EncounterSummary } from '@uho/encounter-api-client/dist/api-client';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { encounterApi } from 'api';
import { UserRole } from '../../../utils/authProvider';
import { Button } from 'oj-c/button';

type Props = {
  selected?: string | undefined;
  onSelection?: (encounterId: string) => void;
  providerId?: number;
  patientId?: number;
  appointmentId?: number;
  userRole: UserRole;
  refreshIndex?: number;
  showDelete?: boolean;
};

export function EncounterList({
  onSelection,
  selected,
  providerId,
  patientId,
  appointmentId,
  userRole,
  refreshIndex,
  showDelete = false
}: Props) {
  const { dataProvider, error, isError, refetch } = useQueryDataProvider<
    EncounterSummary['encounterId'],
    EncounterSummary
  >(
    ['encounters', providerId, patientId, appointmentId, refreshIndex],
    async () => {
      return (await encounterApi.listEncounters({ providerId, patientId, appointmentId })).items || [];
    },
    'encounterId',
    {
      keepPreviousData: false
    }
  );

  const handleSelectedChanged = useCallback(
    (event: ojListView.selectionChanged<EncounterSummary['encounterId'], EncounterSummary>) => {
      if (event.detail.updatedFrom !== 'external') {
        // to prevent endless calls
        onSelection?.(event.detail.value[0]!);
      }
    },
    [onSelection]
  );

  const deleteEncounter = useCallback(
    (encounterId: string) => {
      encounterApi.deleteEncounter({ encounterId }).then(() => refetch());
    },
    [refetch]
  );

  const renderItem = useCallback(
    ({ data }: ItemContext<string, EncounterSummary>) => {
      return (
        <oj-list-item-layout>
          <div class="oj-typography-body-md">{data.reasonCode}</div>
          <div slot="secondary" class="oj-typography-body-sm">
            {data.type || 'no type'}
          </div>
          <div slot="tertiary" class="oj-typography-body-xs oj-text-color-secondary">
            {userRole == 'PATIENT' ? data.providerName || 'Provider XYZ' : data.patientName || 'Patient XYZ'}
          </div>
          <div slot="action">
            {showDelete && (
              <Button
                chroming="outlined"
                display="icons"
                startIcon={<span class="oj-ux-ico-trash" />}
                // eslint-disable-next-line react/jsx-no-bind
                onOjAction={deleteEncounter.bind(undefined, data.encounterId)}
              />
            )}
          </div>
        </oj-list-item-layout>
      );
    },
    [userRole, showDelete, deleteEncounter]
  );

  if (isError || !dataProvider) {
    return <div>An error occured: {JSON.stringify(error)}</div>;
  }

  return (
    <oj-list-view
      onselectionChanged={handleSelectedChanged}
      selectionMode="single"
      aria-label="list of encounters"
      data={dataProvider}
    >
      <template slot="itemTemplate" render={renderItem} />
    </oj-list-view>
  );
}
