/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useI18n } from 'hooks/useI18n';
import { useMemo } from 'preact/hooks';
import { Footer, FooterElement } from 'components/footer';

export function PatientFooter() {
  const { patient } = useI18n();
  const elements = useMemo<FooterElement[]>(() => {
    return [
      {
        id: 'home',
        title: patient.header.home(),
        path: '/patient/',
        iconClass: 'oj-ux-ico-home'
      },
      {
        id: 'providers',
        title: patient.header.providers(),
        path: '/patient/providers',
        iconClass: 'oj-ux-ico-doctor'
      },
      {
        id: 'appointments',
        title: patient.header.appointments(),
        path: '/patient/appointments',
        iconClass: 'oj-ux-ico-calendar-clock'
      },
      {
        id: 'encounters',
        title: patient.header.encounters(),
        path: '/patient/encounters',
        iconClass: 'oj-ux-ico-clock-history'
      }
    ];
  }, [patient]);

  return <Footer elements={elements} />;
}
