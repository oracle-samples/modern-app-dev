/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { Avatar } from 'oj-c/avatar';
import 'ojs/ojactioncard';
import { h } from 'preact';
import { Patient } from '@uho/patient-api-client/dist/api-client';
import { getInitialsFromPatient } from '../../../utils/nameUtils';
import { useMemo } from 'preact/hooks';
import { getBackgroundByName } from '../../../utils/backgroundHelper';

type Props = Readonly<{
  patient: Patient;
}>;

export function CardLayout({ patient }: Props) {
  const name = patient.name!;
  const initials = getInitialsFromPatient(patient);
  const background = useMemo(() => getBackgroundByName(name), [name]);

  return (
    <oj-action-card class="patient-card">
      <div class="container">
        <div class="inner-container">
          <Avatar
            class="avatar"
            background={background}
            role="img"
            size="2xl"
            initials={initials}
            aria-label={`Avatar of ${name}`}
          />
          <span className="name oj-text-primary-color oj-typography-subheading-xs">{name}</span>
          <div class="oj-typography-body-xs oj-text-color-secondary">{patient.email}</div>
          <div class="spacer" />
          <span className="title oj-text-tertiary-color oj-typography-body-md">{patient.phone}</span>
        </div>
      </div>
    </oj-action-card>
  );
}
