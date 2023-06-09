/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojbutton';
import { h } from 'preact';
import { useNavigate, useParams } from 'react-router-dom';
import { AppointmentList } from 'components/appointments/appointment-list';
import { AppointmentDetails } from 'components/appointments/appointment-details';
import { GeneralOverviewLayout } from 'components/layouts/general-overview';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';
import { ProviderFooter } from '../providerFooter';

export type Route = { appointmentId: string };

export function Appointments() {
  const navigate = useNavigate();
  const appointmentsI18n = useI18n().provider.appointments;
  useTitle(appointmentsI18n.title());
  const { appointmentId } = useParams<Route>();

  return (
    <GeneralOverviewLayout
      direction="rtl"
      title={appointmentsI18n.title()}
      subtitle={appointmentsI18n.description()}
      primary={
        <AppointmentDetails
          appointmentId={Number(appointmentId)}
          userRole="PROVIDER"
          onSelectEncounter={(encounterId) => {
            navigate(`/provider/encounters/${encounterId}`);
          }}
        />
      }
      secondary={
        <AppointmentList
          selected={Number(appointmentId)}
          onSelection={(selection) => {
            if (Number(selection)) {
              navigate(`/provider/appointments/${selection}`);
            } else {
              navigate(`/provider/appointments/`);
            }
          }}
        />
      }
      footer={<ProviderFooter />}
    />
  );
}
