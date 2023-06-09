/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useI18n } from 'hooks/useI18n';
import { useMemo } from 'preact/hooks';
import { Footer, FooterElement } from 'components/footer';

export function ProviderFooter() {
  const { provider } = useI18n();
  const elements = useMemo<FooterElement[]>(
    () => [
      {
        id: 'home',
        title: provider.header.home(),
        path: '/provider/',
        iconClass: 'oj-ux-ico-home'
      },
      {
        id: 'appointments',
        title: provider.header.appointments(),
        path: '/provider/appointments',
        iconClass: 'oj-ux-ico-calendar-clock'
      },
      {
        id: 'encounters',
        title: provider.header.encounters(),
        path: '/provider/encounters',
        iconClass: 'oj-ux-ico-clock-history'
      }
    ],
    [provider]
  );

  return <Footer elements={elements} />;
}
