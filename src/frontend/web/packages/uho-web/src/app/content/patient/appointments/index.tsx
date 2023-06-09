/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Button } from 'oj-c/button';
import { h, Fragment } from 'preact';
import { useState } from 'preact/hooks';
import { useNavigate, useParams } from 'react-router-dom';
import { AppointmentList } from 'components/appointments/appointment-list';
import { AppointmentDetails } from 'components/appointments/appointment-details';
import { CreateAppointmentDialog } from 'components/appointments/appointment-schedule';
import { Confirmation } from 'components/dialogs/confirmation';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';
import { GeneralOverviewLayout } from 'components/layouts/general-overview';
import { PatientFooter } from '../patientFooter';

export type Route = { appointmentId: string };

export function Appointments() {
  const appointmentsI18n = useI18n().patient.appointments;
  useTitle(appointmentsI18n.title());
  const { appointmentId } = useParams<Route>();
  const navigate = useNavigate();
  const [showAppointmentDialog, setShowAppointmentDialog] = useState(false);
  const [showConfirmationDialog, setShowConfirmationDialog] = useState(false);
  const [createdAppointmentId, setCreatedAppointmentId] = useState<number | undefined>();
  const [refreshIndex, setRefreshIndex] = useState(0);

  return (
    <Fragment>
      <GeneralOverviewLayout
        direction="rtl"
        title={appointmentsI18n.title()}
        subtitle={appointmentsI18n.description()}
        actions={
          <Button
            label={appointmentsI18n.requestAppointment()}
            chroming="callToAction"
            onOjAction={() => {
              setShowAppointmentDialog(true);
            }}
          />
        }
        primary={
          <AppointmentDetails
            appointmentId={Number(appointmentId)}
            userRole="PATIENT"
            onSelectEncounter={(encounterId) => {
              navigate(`/patient/encounters/${encounterId}`);
            }}
          />
        }
        secondary={
          <AppointmentList
            selected={Number(appointmentId)}
            refreshIndex={refreshIndex} // temporary to force a reload
            onSelection={(selection) => {
              if (Number(selection)) {
                navigate(`/patient/appointments/${selection}`);
              } else {
                navigate(`/patient/appointments/`);
              }
            }}
          />
        }
        footer={<PatientFooter />}
      />
      <div>
        <CreateAppointmentDialog
          onClose={(result) => {
            setShowAppointmentDialog(false);
            if (result) {
              setCreatedAppointmentId(result.id);
              setShowConfirmationDialog(true);
            }
          }}
          show={showAppointmentDialog}
        />
      </div>
      <div>
        <Confirmation
          dialogTitle={appointmentsI18n.scheduledConfirmationTitle()}
          onClose={() => {
            setShowConfirmationDialog(false);
            setRefreshIndex(refreshIndex + 1);
            navigate(`/patient/appointments/${createdAppointmentId}`);
          }}
          show={showConfirmationDialog}
        >
          {appointmentsI18n.scheduledConfirmationDescription()}
        </Confirmation>
      </div>
    </Fragment>
  );
}
