/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Avatar } from 'oj-c/avatar';
import { h } from 'preact';
import 'ojs/ojformlayout';
import 'ojs/ojlabelvalue';

import { Provider } from '@uho/provider-api-client/dist/api-client';
import { useI18n } from 'hooks/useI18n';
import { Foldout } from 'components/layouts/foldout/card';

type Props = Readonly<{
  loading: boolean;
  provider?: Provider;
}>;

/**
 * Mocked page to show case experiences of a provider
 */
export function Experience({ provider }: Props) {
  const i18n = useI18n().patient.provider.experience;
  return (
    <Foldout title="Experience">
      <div class="time">
        <span class="oj-typography-subheading-xs">{i18n.timeAt(provider?.hospitalName!)}</span>
        <div>
          <span class="oj-typography-heading-xl">2</span>{' '}
          <span class="oj-typography-body-xl oj-sm-padding-2x-end">{i18n.years()}</span>
          <span class="oj-typography-heading-xl">4</span> <span class="oj-typography-body-xl">{i18n.months()}</span>
        </div>
      </div>

      <div class="oj-sm-margin-10x-top career-element">
        <div class="oj-flex oj-sm-margin-8x-bottom">
          <div class="profile oj-sm-margin-2x-end">
            <Avatar initials="GS" background="teal" size="xs" />
          </div>
          <div class="oj-flex-item">
            <div class="oj-tyopgraphy-body-md oj-typography-bold">Chief of General Surgery</div>
            <div class="oj-tyopgraphy-body-sm">September 2018 - Present</div>
          </div>
        </div>

        <div class="oj-flex oj-sm-margin-8x-bottom">
          <div class="profile oj-sm-margin-2x-end">
            <Avatar initials="DM" background="lilac" size="xs" />
          </div>
          <div class="oj-flex-item">
            <div class="oj-tyopgraphy-body-md oj-typography-bold">Director Internal Medicine</div>
            <div class="oj-tyopgraphy-body-sm">September 2013 - September 2018</div>
          </div>
        </div>

        <div class="oj-flex oj-sm-margin-8x-bottom">
          <div class="profile oj-sm-margin-2x-end">
            <Avatar initials="IM" background="purple" size="xs" />
          </div>
          <div class="oj-flex-item">
            <div class="oj-tyopgraphy-body-md oj-typography-bold">Internal Medicine</div>
            <div class="oj-tyopgraphy-body-sm">August 2009 - September 2013</div>
          </div>
        </div>
      </div>
    </Foldout>
  );
}
