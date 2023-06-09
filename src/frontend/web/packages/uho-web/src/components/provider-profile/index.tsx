/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { Avatar } from 'oj-c/avatar';
import 'ojs/ojbutton';
import 'ojs/ojformlayout';
import 'ojs/ojlabelvalue';
import 'ojs/ojlabel';

import { Provider } from '@uho/provider-api-client/dist/api-client';
import { Rating } from '../rating';
import { data } from '../rating/mock';
import { useAuth } from '../../utils/authProvider';
import { getInitialsFromProvider } from '../../utils/nameUtils';
import { useI18n } from 'hooks/useI18n';
import { useMemo } from 'preact/hooks';
import { getBackgroundByName } from '../../utils/backgroundHelper';

type Props = Readonly<{
  provider?: Provider;
  displayBadge?: boolean;
  loading?: boolean;
}>;

export function ProviderProfile({ provider, displayBadge = true }: Props) {
  const i18n = useI18n().providerProfile;
  const { user } = useAuth();
  const text = user?.role === 'PATIENT' ? i18n.patientRole() : i18n.providerRole();
  const background = getBackgroundByName(`${provider?.firstName} ${provider?.lastName}`);

  return (
    <div class="provider-profile">
      <div class="oj-flex-bar oj-sm-margin-8x-bottom">
        {displayBadge && (
          <span
            class={`oj-flex-bar-start oj-badge oj-badge-subtle ${
              user?.role === 'PATIENT' ? 'oj-badge-success' : 'oj-badge-info'
            }`}
          >
            {text}
          </span>
        )}
      </div>
      <div class="person oj-flex oj-sm-flex-direction-column oj-sm-margin-4x-bottom">
        <Avatar
          class="oj-flex-item oj-sm-margin-5x-bottom"
          background={background}
          size="2xl"
          role="img"
          initials={getInitialsFromProvider(provider)}
          aria-label={'Avatar of FirstName + LastName'}
        />
        <span class="oj-typography-heading-md oj-flex-item">
          {provider?.firstName} {provider?.lastName}
        </span>
        <span class="oj-typography-semi-bold oj-typography-body-md oj-text-color-secondary oj-flex-item">
          {provider?.title}
        </span>
        <div class="oj-flex-item">
          <Rating {...data} />
        </div>
      </div>
      <div class="contact">
        <oj-form-layout>
          <oj-label-value>
            <oj-label slot="label" label-id="f20">
              {i18n.emailLabel()}
            </oj-label>
            <a slot="value">{provider?.email}</a>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" label-id="f20">
              {i18n.phoneLabel()}
            </oj-label>
            <a slot="value">{provider?.phone}</a>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" label-id="f20">
              {i18n.facilityLabel()}
            </oj-label>
            <span class="oj-text-color-primary" slot="value">
              {provider?.hospitalName}
            </span>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" label-id="f20">
              {i18n.addressLabel()}
            </oj-label>
            <span class="oj-text-color-primary" slot="value">
              {provider?.hospitalAddress}
            </span>
          </oj-label-value>
        </oj-form-layout>
      </div>
    </div>
  );
}
