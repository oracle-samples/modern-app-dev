/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { ExtendGlobalProps } from 'ojs/ojvcomponent';
import { Encounter as EncounterModel } from '@uho/encounter-api-client/dist/api-client';
import { encounterApi } from 'api';
import { useQuery } from '@tanstack/react-query';
import { Encounter } from './encounter';
import { Empty } from '../../empty-screen';
import { UserRole } from '../../../utils/authProvider';
import { useI18n } from 'hooks/useI18n';

type Props = {
  encounterId: string | undefined;
  readonly: boolean;
  userRole: UserRole;
};

export function EncounterDetails({ encounterId, readonly, userRole }: ExtendGlobalProps<Props>) {
  const i18n = useI18n().encounters;
  const encounterResult = useQuery<EncounterModel, number>(
    ['encounter', encounterId],
    () => encounterApi.getEncounter({ encounterId: encounterId! }),
    {
      enabled: !!encounterId
    }
  );
  if (!encounterId) {
    return <Empty title={i18n.noneSelectedTitle()} description={i18n.noneSelectedDescription()} />;
  }
  if (encounterResult.isLoading) {
    return <div>Loading</div>;
  }
  if (encounterResult.isError) {
    <div>Something went wrong.</div>;
  }
  if (!encounterResult.data) {
    return <div>No encounter found</div>;
  }

  return <Encounter encounter={encounterResult.data!} readonly={readonly} userRole={userRole} />;
}
