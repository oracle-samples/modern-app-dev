/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojlistitemlayout';
import 'ojs/ojbutton';
import 'ojs/ojlistview';
import { ojListView } from 'ojs/ojlistview';
import { h, Fragment } from 'preact';
import { useCallback, useEffect, useState } from 'preact/hooks';
import { CardLayout } from './card-layout';
import { ListLayout } from './list-layout';
import { patientApi } from 'api';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { PatientSummary } from '@uho/patient-api-client/dist/api-client';
import { useI18n } from 'hooks/useI18n';

type Props = {
  onSelection?: (patient: number) => void;
};

const gridlinesItemVisible: { item: 'visible' | 'visibleExceptLast' | 'hidden' } = { item: 'visible' };

export function PatientList({ onSelection }: Props) {
  const i18n = useI18n().patientList;
  const { dataProvider, error, isError } = useQueryDataProvider<PatientSummary['id'], PatientSummary>(
    ['patients'],
    async () => (await patientApi.listPatients({})).items || [],
    'id'
  );

  const handleSelectedChanged = useCallback(
    (event: ojListView.selectionChanged<PatientSummary['id'], PatientSummary>) => {
      onSelection?.(event.detail.value[0]!);
    },
    [onSelection]
  );
  const [totalSize, setTotalSize] = useState(0);
  const [display, setDisplay] = useState('list' as 'list' | 'card');
  const renderItem = useCallback(
    (item: ojListView.ItemContext<string, PatientSummary>) => {
      return <li>{display == 'list' ? <ListLayout patient={item.data} /> : <CardLayout patient={item.data} />}</li>;
    },
    [display]
  );

  useEffect(() => {
    if (dataProvider != null) {
      dataProvider.getTotalSize().then((size) => setTotalSize(size));
    }
  }, [dataProvider]);

  let content;
  if (isError || !dataProvider) {
    content = <div>An error occured: {JSON.stringify(error)}</div>;
  } else {
    content = (
      <oj-list-view
        onselectionChanged={handleSelectedChanged}
        display={display}
        selectionMode="single"
        aria-label="list of patients"
        data={dataProvider}
        gridlines={gridlinesItemVisible}
      >
        <template slot="itemTemplate" render={renderItem} />
      </oj-list-view>
    );
  }
  return (
    <>
      <div className="patient-list-header oj-sm-padding-2x oj-flex oj-sm-flex-items-initial oj-sm-justify-content-space-between">
        <div class="oj-flex-item">{i18n.results(totalSize)}</div>
        <div class="oj-flex-item">
          <oj-buttonset-one
            onvalueChanged={(event) => {
              setDisplay(event.detail.value);
            }}
            value={display}
          >
            <oj-option value="card">{i18n.cardView()}</oj-option>
            <oj-option value="list">{i18n.listView()}</oj-option>
          </oj-buttonset-one>
        </div>
      </div>
      {content}
    </>
  );
}
