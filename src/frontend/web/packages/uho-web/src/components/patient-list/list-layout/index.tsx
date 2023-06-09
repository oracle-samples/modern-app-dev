/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Avatar } from 'oj-c/avatar';
import { ListItemLayout } from 'oj-c/list-item-layout';
import 'ojs/ojpictochart';
import { h } from 'preact';
import { PatientSummary } from '@uho/patient-api-client/dist/api-client';
import { getInitialsFromPatient } from '../../../utils/nameUtils';
import { useMemo } from 'preact/hooks';
import { getBackgroundByName } from '../../../utils/backgroundHelper';

type Props = Readonly<{
  patient: PatientSummary;
}>;

export function ListLayout({ patient }: Props) {
  const name = patient.name!;
  const initials = getInitialsFromPatient(patient);
  const background = useMemo(() => getBackgroundByName(name), [name]);

  return (
    <ListItemLayout>
      <Avatar
        slot="leading"
        background={background}
        class="profile-card-layout-image"
        role="img"
        size="xs"
        initials={initials}
        aria-label={`Avatar of ${name}`}
      />
      <div class="oj-typography-body-md oj-typography-bold">{name}</div>
      <div slot="secondary" class="oj-typography-body-sm">
        {patient.username}
      </div>
      <div slot="tertiary" class="oj-typography-body-xs oj-text-color-secondary">
        {patient.email}
      </div>
      <div slot="metadata" class="oj-typography-body-xs oj-text-color-secondary">
        {patient.phone}
      </div>
    </ListItemLayout>
  );
}
