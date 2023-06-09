/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useNavigate, useParams } from 'react-router-dom';
import { EncounterDetails } from 'components/encounters/encounter-details';
import { EncounterList } from 'components/encounters/encounter-list';
import { useAuth } from '../../../../utils/authProvider';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';
import { GeneralOverviewLayout } from 'components/layouts/general-overview';
import { PatientFooter } from '../patientFooter';

export type Route = { encounterId: string };

export function Encounters() {
  const { encounterId } = useParams<Route>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const encountersI18n = useI18n().patient.encounters;
  useTitle(encountersI18n.title());

  return (
    <GeneralOverviewLayout
      direction="rtl"
      title={encountersI18n.title()}
      subtitle={encountersI18n.description()}
      primary={<EncounterDetails encounterId={encounterId} readonly={true} userRole="PATIENT" />}
      secondary={
        <EncounterList
          userRole={user!.role}
          patientId={user!.id}
          selected={encounterId}
          onSelection={(selection) => {
            if (selection) {
              navigate(`/patient/encounters/${selection}`);
            } else {
              navigate(`/patient/encounters/`);
            }
          }}
        />
      }
      footer={<PatientFooter />}
    />
  );
}
