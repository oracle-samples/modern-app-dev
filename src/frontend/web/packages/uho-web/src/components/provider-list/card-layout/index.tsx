/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { Avatar } from 'oj-c/avatar';
import 'ojs/ojactioncard';
import { h } from 'preact';
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { getInitialsFromProvider } from '../../../utils/nameUtils';
import { Rating } from 'components/rating';
import { data } from 'components/rating/mock';
import { useMemo } from 'preact/hooks';
import { getBackgroundByName } from '../../../utils/backgroundHelper';

type Props = Readonly<{
  provider: Provider;
}>;

export function CardLayout({ provider }: Props) {
  const name = `${provider.firstName} ${provider.lastName}`;
  const initials = getInitialsFromProvider(provider);
  const background = useMemo(() => getBackgroundByName(name), [name]);

  return (
    <oj-action-card class="provider-card">
      <div class="container oj-flex oj-sm-flex-direction-column oj-sm-align-items-center oj-sm-justify-content-center oj-sm-padding-6x-vertical">
        <Avatar
          class="avatar oj-flex-item"
          background={background}
          role="img"
          size="2xl"
          initials={initials}
          aria-label={`Avatar of ${name}`}
        />
        <div class="oj-flex-item oj-sm-margin-1x-vertical">
          <Rating {...data} />
        </div>
        <div class="oj-flex-item oj-text-primary-color oj-typography-subheading-xs">{name}</div>
        <div class="oj-flex-item oj-typography-body-xs oj-text-color-secondary">{provider.email}</div>
        <div class="oj-flex-item oj-text-tertiary-color oj-typography-body-md">{provider.title}</div>
      </div>
    </oj-action-card>
  );
}
