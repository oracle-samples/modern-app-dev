/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojlistitemlayout';
import 'ojs/ojbutton';
import 'ojs/ojactioncard';
import 'ojs/ojlistview';
import { ojListView } from 'ojs/ojlistview';

import { h, Fragment } from 'preact';
import { useCallback, useEffect, useState } from 'preact/hooks';
import { CardLayout } from './card-layout';
import { ListLayout } from './list-layout';
import { providerApi } from 'api';
import { useQueryDataProvider } from 'hooks/useDataProvider';
import { useI18n } from 'hooks/useI18n';
import { Provider } from '@uho/provider-api-client/dist/api-client';

type Props = {
  nameSearch: string;
  filter?: {
    speciality?: string;
    location?: string;
  };
  onSelection: (providerId: number) => void;
};

const gridlinesItemVisible: { item: 'visible' | 'visibleExceptLast' | 'hidden' } = { item: 'visible' };

async function searchProvider(name: string, speciality?: string, location?: string | undefined) {
  const response = await providerApi.listProviders({ speciality, city: location, name });
  return response.items || [];
}

export function ProviderList({ onSelection, filter, nameSearch }: Props) {
  const i18n = useI18n().providerList;
  const { dataProvider, error, isError } = useQueryDataProvider<Provider['id'], Provider>(
    ['providers', filter?.location, nameSearch, filter?.speciality],
    searchProvider.bind(undefined, nameSearch, filter?.speciality, filter?.location),
    'id'
  );

  const handleSelectedChanged = useCallback(
    (event: ojListView.selectionChanged<Provider['id'], Provider>) => {
      onSelection(event.detail.value[0]!);
    },
    [onSelection]
  );
  const [totalSize, setTotalSize] = useState(0);
  const [display, setDisplay] = useState('list' as 'list' | 'card');
  const renderItem = useCallback(
    (item: ojListView.ItemContext<string, Provider>) => {
      return <li>{display == 'list' ? <ListLayout provider={item.data} /> : <CardLayout provider={item.data} />}</li>;
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
        class="list-view"
        onselectionChanged={handleSelectedChanged}
        display={display}
        selectionMode="single"
        aria-label="list of providers"
        data={dataProvider}
        gridlines={gridlinesItemVisible}
      >
        <template slot="itemTemplate" render={renderItem} />
      </oj-list-view>
    );
  }
  return (
    <Fragment>
      <div className="provider-list-panel oj-flex oj-sm-padding-2x-bottom oj-sm-flex-items-initial oj-sm-justify-content-space-between">
        <div class="oj-flex-item">{i18n.results(totalSize)}</div>
        <div class="oj-flex-item">
          <oj-buttonset-one
            onvalueChanged={(event) => {
              setDisplay(event.detail.value);
            }}
            value={display}
            class="oj-button-sm"
            display="icons"
          >
            <oj-option value="card">
              <span slot="startIcon" class="oj-ux-ico-grid-view-large" />
              {i18n.cardView()}
            </oj-option>
            <oj-option value="list">
              <span slot="startIcon" class="oj-ux-ico-list-item-layout" />
              {i18n.listView()}
            </oj-option>
          </oj-buttonset-one>
        </div>
      </div>
      {content}
    </Fragment>
  );
}
