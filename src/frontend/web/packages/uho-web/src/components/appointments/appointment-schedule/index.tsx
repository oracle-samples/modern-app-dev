/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojformlayout';
import 'ojs/ojswitch';
import 'ojs/ojdialog';
import 'ojs/ojlabelvalue';
import { InputText } from 'oj-c/input-text';
import { TextArea } from 'oj-c/text-area';
import { Button } from 'oj-c/button';

import { h } from 'preact';
import { useCallback, useEffect, useRef, useState } from 'preact/hooks';
import { appointmentApi } from 'api';
import { useAuth } from '../../../utils/authProvider';
import { Patient } from '@uho/patient-api-client/dist/api-client';
import { ojDialog } from 'ojs/ojdialog';
import { SlotSummary } from '@uho/provider-api-client/dist/api-client';
import { AppointmentPicker } from '../appointment-picker';
import { Appointment } from '@uho/appointment-api-client/dist/api-client';
import { ProviderDropdown } from 'components/provider-list/dropdown';
import { error } from 'ojs/ojlogger';
import { getContext } from 'ojs/ojcontext';
import { useI18n } from 'hooks/useI18n';

export type SelectableSlot = SlotSummary & {
  selected?: boolean;
};
type Props = Readonly<{ providerId?: number; show: boolean; onClose: (appointment: Appointment | null) => void }>;

export type AppointmentFormData = {
  name: string | undefined;
  reason: string;
  email: string | undefined;
  phone: string | undefined;
  reminder: boolean;
  selected: SelectableSlot | undefined;
  provider: number | undefined;
};

export function CreateAppointmentDialog({ onClose, show, providerId }: Props) {
  const i18n = useI18n().appointments;
  const { user } = useAuth();
  const dialogRef = useRef<ojDialog>(null);

  const [formData, setFormData] = useState<AppointmentFormData>({
    name: (user as Patient).name || user?.username,
    reason: i18n.reasonForVisitHint(),
    email: user?.email,
    phone: user?.phone,
    reminder: false,
    selected: undefined,
    provider: providerId
  });

  useEffect(() => {
    if (!formData.provider && providerId) {
      setFormData({ ...formData, provider: providerId });
    }
  }, [formData, providerId]);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (dialog) {
      getContext(dialog)
        .getBusyContext()
        .whenReady(2000)
        .then(() => {
          show ? dialog.open() : dialog.close();
        })
        .catch((reason) => {
          error(`BusyContext timed out: ${reason}`);
        });
    }
  }, [show]);

  const onChange = useCallback(
    (id: string, value: string) => {
      setFormData({
        ...formData,
        [id]: value
      });
    },
    [formData, setFormData]
  );

  const bookAppointment = useCallback(async () => {
    if (formData.selected) {
      const appointment = await appointmentApi.createAppointment({
        createAppointmentRequest: {
          startTime: formData.selected.startTime!,
          endTime: formData.selected.endTime!,
          providerId: formData.provider!,
          patientId: user?.id!
        }
      });
      onClose?.(appointment);
    }
  }, [formData.selected, formData.provider, onClose, user?.id]);

  const disabled = !formData.name || !formData.email || !formData.phone || !formData.reason || !formData.selected;

  return (
    <oj-dialog
      ref={dialogRef}
      dialogTitle={i18n.scheduleAppointmentTitle()}
      cancelBehavior="escape"
      style={{ minWidth: '50vw', minHeight: '55vh' }}
    >
      <div slot="body" class="oj-flex">
        <div class="oj-flex-item oj-sm-padding-2x-start oj-sm-padding-2x-end">
          <oj-form-layout direction="row" maxColumns={2}>
            <oj-label-value colspan={2}>
              <TextArea
                slot="value"
                id="reason"
                required={true}
                labelHint={i18n.reasonForVisitLabel()}
                value={formData.reason}
                // eslint-disable-next-line react/jsx-no-bind
                onValueChanged={onChange.bind(undefined, 'reason')}
              />
            </oj-label-value>
            <InputText
              id="name"
              required={true}
              labelHint={i18n.nameLabel()}
              value={formData.name}
              // eslint-disable-next-line react/jsx-no-bind
              onValueChanged={onChange.bind(undefined, 'name')}
            />
            <InputText
              id="phone"
              required={true}
              labelHint={i18n.phoneLabel()}
              value={formData.phone}
              // eslint-disable-next-line react/jsx-no-bind
              onValueChanged={onChange.bind(undefined, 'phone')}
            />
            <oj-label-value colspan={2}>
              <InputText
                slot="value"
                id="email"
                required={true}
                labelHint={i18n.emailLabel()}
                value={formData.email}
                // eslint-disable-next-line react/jsx-no-bind
                onValueChanged={onChange.bind(undefined, 'email')}
              />
            </oj-label-value>

            <ProviderDropdown
              providerId={formData.provider}
              onvalueChanged={(provider) => {
                setFormData({
                  ...formData,
                  provider
                });
              }}
            />
          </oj-form-layout>
        </div>
        <div class="oj-flex-item oj-sm-padding-2x-start oj-sm-padding-2x-end">
          {formData.provider && (
            <AppointmentPicker
              selected={formData.selected}
              onSelection={(selected) => {
                setFormData({
                  ...formData,
                  selected
                });
              }}
              providerId={formData.provider}
            />
          )}
        </div>
      </div>
      <div slot="footer" class="oj-flex oj-sm-justify-content-flex-end">
        <Button class="oj-sm-margin-1x" label={i18n.cancel()} onOjAction={() => onClose?.(null)} />
        <Button
          class="oj-sm-margin-1x"
          label={i18n.bookNow()}
          chroming="callToAction"
          onOjAction={bookAppointment}
          disabled={disabled}
        />
      </div>
    </oj-dialog>
  );
}
