/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useI18n } from 'hooks/useI18n';
import { h } from 'preact';
import { useAuth } from '../../utils/authProvider';
import { useCallback } from 'preact/hooks';
import { ojMenu } from 'ojs/ojmenu';
import 'ojs/ojbutton';

export function UserMenu() {
  const { user, signout } = useAuth();
  const { header } = useI18n();

  const menuListener = useCallback(
    (event: ojMenu.ojMenuAction) => {
      if (event.detail.selectedValue === 'logout') {
        signout();
      }
    },
    [signout]
  );

  return (
    <oj-menu-button chroming="borderless" display="all">
      {user?.email}
      <oj-menu slot="menu" onojMenuAction={menuListener}>
        <oj-option value="logout">
          <span class="oj-ux-ico-log-out" slot="startIcon" />
          {header.logout()}
        </oj-option>
      </oj-menu>
    </oj-menu-button>
  );
}
