/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Avatar } from 'oj-c/avatar';
import 'ojs/ojpictochart';
import { ListItemLayout } from 'oj-c/list-item-layout';
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { h } from 'preact';
import { getInitialsFromProvider } from '../../../utils/nameUtils';
import { Rating } from '../../rating';
import { data } from '../../rating/mock';
import { useMemo } from 'preact/hooks';
import { getBackgroundByName } from '../../../utils/backgroundHelper';

type Props = Readonly<{
  provider: Provider;
}>;

export function ListLayout({ provider }: Props) {
  const name = `${provider.firstName} ${provider.lastName}`;
  const initials = getInitialsFromProvider(provider);
  const background = useMemo(() => getBackgroundByName(name), [name]);

  return (
    <ListItemLayout
      leading={
        <Avatar
          class="profile-card-layout-image"
          role="img"
          size="xs"
          background={background}
          initials={initials}
          aria-label={`Avatar of ${name}`}
        />
      }
      secondary={<div class="oj-typography-body-sm">{provider.hospitalName}</div>}
      tertiary={<div class="oj-typography-body-xs oj-text-color-secondary">{provider.email}</div>}
      metadata={
        <div class="oj-typography-body-xs oj-text-color-secondary">
          <Rating {...data} />
        </div>
      }
    >
      <div class="oj-typography-body-md oj-typography-bold">{name}</div>
    </ListItemLayout>
  );
}
