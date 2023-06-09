/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojformlayout';
import 'ojs/ojlabelvalue';
import 'ojs/ojinputtext';
import 'ojs/ojlabel';
import 'ojs/ojselectcombobox';
import 'ojs/ojcheckboxset';
import { Button } from 'oj-c/button';
import { h } from 'preact';
import { ExtendGlobalProps } from 'ojs/ojvcomponent';
import { useQuery } from '@tanstack/react-query';
import { Appointment } from '@uho/appointment-api-client/dist/api-client';
import { appointmentApi } from 'api';
import { EncounterList } from 'components/encounters/encounter-list';
import { useState } from 'preact/hooks';
import { CreateEncounter } from 'components/encounters/create-encounter';
import { Empty } from 'components/empty-screen';
import { UserRole } from '../../../utils/authProvider';
import { LoadingWrapper } from '../../loading-wrapper';
import { Confirmation } from '../../dialogs/confirmation';
import { info } from 'ojs/ojlogger';
import { useI18n } from 'hooks/useI18n';

type Props = {
  appointmentId?: number;
  userRole: UserRole;
  onSelectEncounter: (encounterId: string) => void;
};

export function AppointmentDetails({ appointmentId, userRole, onSelectEncounter }: ExtendGlobalProps<Props>) {
  const [showEncounterDialog, setShowEncounterDialog] = useState(false);
  const [showConfirmationDialog, setShowConfirmationDialog] = useState(false);
  const [refreshIndex, setRefreshIndex] = useState(0);
  const i18n = useI18n().appointments;

  const appointmentResult = useQuery<Appointment, number>(
    ['appointment', appointmentId],
    () => appointmentApi.getAppointment({ appointmentId: appointmentId! }),
    {
      enabled: !!appointmentId,
      keepPreviousData: false
    }
  );
  if (!appointmentId) {
    if (userRole === 'PATIENT') {
      return <Empty title={i18n.noAppointmentSelectedTitle()} description={i18n.noAppointmentSelectedDescription()} />;
    }
    return <Empty title={i18n.noAppointmentSelectedTitle()} />;
  }

  if (appointmentResult.isError) {
    return <div>Error: {JSON.stringify(appointmentResult.error)}</div>;
  }
  const appointment = appointmentResult.data;

  return (
    <div class="appointment-container">
      <div class="oj-flex-bar">
        <div class="oj-flex-bar-start">
          <LoadingWrapper loading={appointmentResult.isLoading}>
            {appointment?.startTime?.toDateString()}
          </LoadingWrapper>
        </div>
        <div class="oj-flex-bar-end">
          {userRole === 'PROVIDER' && (
            <Button
              label={i18n.addEncounter()}
              chroming="callToAction"
              onOjAction={() => setShowEncounterDialog(true)}
            />
          )}
        </div>
      </div>
      <div class="appointment-body">
        <h3 class="oj-typography-subheading-sm">Appointment</h3>
        <oj-form-layout columns={2}>
          <oj-label-value>
            <oj-label slot="label" for="status">
              Status
            </oj-label>
            <div id="status" slot="value">
              <LoadingWrapper loading={appointmentResult.isLoading}>{appointment?.status}</LoadingWrapper>
            </div>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" for="previsit">
              Previsit Data
            </oj-label>
            <div id="previsit" slot="value">
              <LoadingWrapper loading={appointmentResult.isLoading}>{appointment?.preVisitData || '/'}</LoadingWrapper>
            </div>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" for="starttime">
              {i18n.startTime()}
            </oj-label>
            <div id="starttime" slot="value">
              <LoadingWrapper loading={appointmentResult.isLoading}>
                {appointment?.startTime?.toLocaleString('en-us', {
                  hour: '2-digit',
                  minute: '2-digit'
                }) || '/'}
              </LoadingWrapper>
            </div>
          </oj-label-value>

          <oj-label-value>
            <oj-label slot="label" for="endtime">
              {i18n.endTime()}
            </oj-label>
            <div id="endtime" slot="value">
              <LoadingWrapper loading={appointmentResult.isLoading}>
                {appointment?.endTime?.toLocaleString('en-us', {
                  hour: '2-digit',
                  minute: '2-digit'
                }) || '/'}
              </LoadingWrapper>
            </div>
          </oj-label-value>
        </oj-form-layout>

        <h3 class="oj-typography-subheading-sm">{i18n.encountersTitle()}</h3>
        <hr />
        {appointment && (
          <div class="encounter-list">
            <EncounterList
              userRole={userRole}
              refreshIndex={refreshIndex} // temporary to force a reload
              patientId={appointment.patientId!}
              providerId={appointment.providerId!}
              appointmentId={appointmentId}
              onSelection={(encounterId) => {
                onSelectEncounter?.(encounterId);
              }}
              showDelete={true}
            />
          </div>
        )}
        <div>
          <CreateEncounter
            appointmentId={appointmentId}
            patientId={appointment?.patientId!}
            providerId={appointment?.providerId!}
            onClose={(result) => {
              setShowEncounterDialog(false);
              if (result) {
                setShowConfirmationDialog(true);
                // encounterResult.refetch();
                info('success');
              }
            }}
            show={showEncounterDialog}
          />
        </div>
        <div>
          <Confirmation
            dialogTitle={i18n.encounterCreatedTitle()}
            onClose={() => {
              setShowConfirmationDialog(false);
              setRefreshIndex(refreshIndex + 1);
            }}
            show={showConfirmationDialog}
          >
            {i18n.encounterCreatedDescription()}
          </Confirmation>
        </div>
      </div>
    </div>
  );
}
