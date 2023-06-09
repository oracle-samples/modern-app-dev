/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { ProviderProfile } from 'components/provider-profile';

type Props = Readonly<{
  provider?: Provider;
}>;

export function InfoPanel({ provider }: Props) {
  return (
    <div class="oj-panel oj-panel-shadow-sm">
      <div class="oj-sm-margin-3x">
        <ProviderProfile provider={provider} />
      </div>
    </div>
  );
}
