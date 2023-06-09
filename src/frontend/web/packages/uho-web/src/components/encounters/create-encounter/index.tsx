/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojdialog';
import { Button } from 'oj-c/button';
import { ojDialog } from 'ojs/ojdialog';
import { Encounter as EncounterModel } from '@uho/encounter-api-client/dist/api-client';
import { h } from 'preact';
import { useCallback, useEffect, useRef, useState } from 'preact/hooks';
import { RecursivePartial } from '../../../../typings';
import { Encounter } from '../encounter-details/encounter';
import { encounterApi } from 'api';
import { getContext } from 'ojs/ojcontext';
import { error } from 'ojs/ojlogger';
import { useI18n } from 'hooks/useI18n';

type Props = Readonly<{
  show: boolean;
  appointmentId: number;
  patientId: number;
  providerId: number;
  onClose: (encounter: EncounterModel | null) => void;
}>;

export function CreateEncounter({ show, appointmentId, patientId, providerId, onClose }: Props) {
  const i18n = useI18n().encounters.createEncounter;
  const dialogRef = useRef<ojDialog>(null);
  const [encounter, setEncounter] = useState<RecursivePartial<EncounterModel> & { valid?: boolean }>(
    {} as EncounterModel
  );
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
  }, [dialogRef, show]);

  const createEncounter = useCallback(async () => {
    const createdEncounter = await encounterApi.createEncounter({
      encounter: { ...encounter, appointmentId, patientId, providerId } as EncounterModel
    });
    onClose(createdEncounter);
  }, [encounter, appointmentId, patientId, providerId, onClose]);

  return (
    <div>
      <oj-dialog style={{ width: '60vw' }} ref={dialogRef} title={i18n.title()}>
        <div slot="body">
          <Encounter encounter={encounter} onUpdate={setEncounter} readonly={false} userRole="PROVIDER" />
        </div>
        <div slot="footer">
          <Button
            label={i18n.cancel()}
            class="oj-sm-margin-2x-end"
            chroming="outlined"
            onOjAction={() => onClose(null)}
          />
          <Button
            label={i18n.create()}
            disabled={!encounter.valid}
            chroming="callToAction"
            onOjAction={createEncounter}
          />
        </div>
      </oj-dialog>
    </div>
  );
}
