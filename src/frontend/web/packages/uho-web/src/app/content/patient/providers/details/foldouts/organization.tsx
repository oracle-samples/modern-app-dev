/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { h } from 'preact';
import { useI18n } from 'hooks/useI18n';
import { Foldout } from 'components/layouts/foldout/card';

type Props = Readonly<{
  loading: boolean;
  provider?: Provider;
}>;

export function Organization({ loading, provider }: Props) {
  const i18n = useI18n().patient.provider.organization;
  return <Foldout title={i18n.title()}>Hello</Foldout>;
}
