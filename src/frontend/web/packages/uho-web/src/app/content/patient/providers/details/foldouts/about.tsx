/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojformlayout';
import 'ojs/ojlabelvalue';
import { h, Fragment } from 'preact';
import { Button } from 'oj-c/button';
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { useNavigate } from 'react-router';
import { CreateAppointmentDialog } from 'components/appointments/appointment-schedule';
import { useState } from 'preact/hooks';
import { Confirmation } from 'components/dialogs/confirmation';
import { Appointment } from '@uho/appointment-api-client/dist/api-client';
import { useI18n } from 'hooks/useI18n';
import { Foldout } from 'components/layouts/foldout/card';

type Props = Readonly<{
  loading: boolean;
  provider?: Provider;
  providerId: number;
}>;

export function About({ provider, providerId }: Props) {
  const i18n = useI18n().patient.provider.about;
  const [showAppointment, setShowAppointment] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [appointment, setAppointment] = useState<Appointment | undefined>(undefined);
  const navigate = useNavigate();

  function showTags(tags: string[]) {
    const tagComponent = tags.map((tag) => (
      <span key={tag} class="oj-badge oj-sm-margin-1x">
        {tag}
      </span>
    ));
    return (
      <oj-label-value>
        <oj-label slot="label">{i18n.tags()}</oj-label>
        <p slot="value">{tagComponent}</p>
      </oj-label-value>
    );
  }
  return (
    <>
      <Foldout title={i18n.title()}>
        <span style={{ display: 'block' }} class="oj-typography-body-lg">
          {provider?.professionalSummary}
        </span>
        <br />
        <span style={{ display: 'block' }} class="oj-typography-body-lg">
          {provider?.designation}
        </span>
        <br />
        <oj-form-layout>
          <oj-label-value>
            <oj-label slot="label">{i18n.interests()}</oj-label>
            <span class="oj-typography-body-md" slot="value">
              {provider?.interests}
            </span>
          </oj-label-value>
          {provider?.expertise && (
            <oj-label-value>
              <oj-label slot="label">{i18n.expertise()}</oj-label>
              <span class="oj-typography-body-md" slot="value">
                {provider.expertise}
              </span>
            </oj-label-value>
          )}
          {provider?.tags && showTags(provider.tags)}
          <Button
            label={i18n.bookAppointment()}
            onOjAction={() => {
              setShowAppointment(true);
            }}
          />
        </oj-form-layout>
      </Foldout>
      <CreateAppointmentDialog
        show={showAppointment}
        onClose={(appointment) => {
          setShowAppointment(false);
          if (appointment) {
            setAppointment(appointment);
            setTimeout(() => {
              setShowConfirmation(true);
            }, 500);
          }
        }}
        providerId={providerId}
      />
      <Confirmation
        dialogTitle={i18n.appointmentScheduledTitle()}
        show={showConfirmation}
        onClose={() => {
          setShowConfirmation(false);
          navigate(`/patient/appointments/${appointment?.id}`);
        }}
      >
        {i18n.appointmentScheduledDescription()}
        <oj-form-layout>
          <oj-label-value>
            <oj-label slot="label" for="starttime">
              {i18n.appointmentStartTime()}
            </oj-label>
            <div id="starttime" slot="value">
              {appointment?.startTime?.toLocaleString('en-us', {
                hour: '2-digit',
                minute: '2-digit'
              }) || '/'}
            </div>
          </oj-label-value>
          <oj-label-value>
            <oj-label slot="label" for="endtime">
              {i18n.appointmentEndTime()}
            </oj-label>
            <div id="endtime" slot="value">
              {appointment?.endTime?.toLocaleString('en-us', {
                hour: '2-digit',
                minute: '2-digit'
              }) || '/'}
            </div>
          </oj-label-value>
        </oj-form-layout>
      </Confirmation>
    </>
  );
}
