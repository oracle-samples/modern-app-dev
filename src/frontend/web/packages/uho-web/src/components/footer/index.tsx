/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojnavigationlist';
import { ojNavigationList } from '@oracle/oraclejet/ojnavigationlist';
import { useArrayDataProvider } from 'hooks/useDataProvider';
import { h } from 'preact';
import { useCallback, useMemo } from 'preact/hooks';
import { useLocation, useNavigate } from 'react-router-dom';

export interface FooterElement {
  id: string;
  title: string;
  path: string;
  iconClass?: string;
}

export type Props = {
  elements: FooterElement[];
};

export function Footer({ elements }: Props) {
  const routesDataProvider = useArrayDataProvider(elements, 'path');
  const { pathname } = useLocation();
  const navigate = useNavigate();

  const selection = useMemo(() => {
    let currentPath = pathname;
    let foundElement = null;
    while (currentPath && !foundElement) {
      foundElement = elements.filter((e) => e.path === currentPath)[0];
      currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
    }
    return foundElement?.path || location.pathname;
  }, [elements, pathname]);

  const pageChangeHandler = useCallback(
    (event: ojNavigationList.selectionChanged<string, FooterElement>) => {
      if (event.detail.updatedFrom === 'internal') {
        navigate(event.detail.value);
      }
    },
    [navigate]
  );

  const renderNavList = (item: ojNavigationList.ItemContext<string, FooterElement>) => {
    const itemData = item.data as FooterElement;
    return (
      <li id={itemData.path}>
        <a href="#">
          <span class={`oj-navigationlist-item-icon ${itemData.iconClass}`} />
          <span class="oj-navigationlist-item-label">{itemData.title}</span>
        </a>
      </li>
    );
  };

  return (
    <div class="footer">
      <oj-navigation-list
        class="oj-sm-condense oj-sm-margin-8x-horizontal"
        edge="bottom"
        display="all"
        drillMode="none"
        selection={selection}
        onselectionChanged={pageChangeHandler}
        data={routesDataProvider}
      >
        <template slot="itemTemplate" render={renderNavList} />
      </oj-navigation-list>
    </div>
  );
}
