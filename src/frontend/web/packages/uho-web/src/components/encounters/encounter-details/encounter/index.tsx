/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojvalidationgroup';
import 'ojs/ojformlayout';
import 'ojs/ojlabel';
import 'ojs/ojlabelvalue';
import 'ojs/ojselectcombobox';
import 'ojs/ojselectsingle';
import 'ojs/ojcheckboxset';
import { InputText } from 'oj-c/input-text';
import { TextArea } from 'oj-c/text-area';
import { ojValidationGroup } from 'ojs/ojvalidationgroup';
import { h } from 'preact';
import { useEffect, useMemo, useState } from 'preact/hooks';
import { Observations } from '../../../observations';
import { ExtendGlobalProps } from 'ojs/ojvcomponent';
import {
  CodeType,
  Condition,
  Encounter as EncounterType,
  Observation
} from '@uho/encounter-api-client/dist/api-client';
import { Conditions } from 'components/conditions';
import { RecursivePartial } from '../../../../../typings';
import { UserRole } from '../../../../utils/authProvider';
import { CodeSelection } from '../../codes';

type Props = {
  encounter: RecursivePartial<EncounterType>;
  onUpdate?: (encounter: RecursivePartial<EncounterType> & { valid?: boolean }) => void;
  readonly: boolean;
  userRole: UserRole;
};

export function Encounter({ readonly, encounter, onUpdate, userRole }: ExtendGlobalProps<Props>) {
  const [isValid, setIsValid] = useState<ojValidationGroup['valid']>();
  const [reasonCode, setReasonCode] = useState(encounter.reasonCode);
  const [status, setStatus] = useState(encounter.status);
  const [encounterType, setEncounterType] = useState(encounter.type);
  const [location, setLocation] = useState(encounter.location);
  const [serviceProvider, setServiceProvider] = useState(encounter.serviceProvider);
  const [followUpRequested, setFollowUpRequested] = useState(encounter.followUpRequested);

  const [instruction, setInstruction] = useState(encounter?.recommendation?.instruction);
  const [additionalInstruction, setAdditionalInstruction] = useState(encounter?.recommendation?.additionalInstructions);
  const [recommendationDate, setRecommendationDate] = useState(encounter?.recommendation?.recommendationDate);
  const [recommendedBy, setRecommendedBy] = useState(encounter?.recommendation?.recommendedBy);

  const [participantName, setParticipantName] = useState(encounter?.participant?.name);
  const [participantType, setParticipantType] = useState(encounter?.participant?.type);

  const [observations, setObservations] = useState<RecursivePartial<Observation>[]>(encounter.observations || []);
  const [conditions, setConditions] = useState<RecursivePartial<Condition>[]>(encounter.conditions || []);

  const onObservationsChanged = useMemo(
    () => (changedObservations: RecursivePartial<Observation>[]) => {
      setObservations([...changedObservations]);
    },
    [setObservations]
  );
  const onConditionsChanged = useMemo(
    () => (changedConditions: RecursivePartial<Condition>[]) => {
      setConditions([...changedConditions]);
    },
    [setConditions]
  );

  const onEncounterTypeChanged = function (encounterType: string) {
    setEncounterType(encounterType);
  };

  useEffect(() => {
    const encounter: RecursivePartial<EncounterType> & { valid?: boolean } = {
      reasonCode,
      status,
      type: encounterType,
      location,
      serviceProvider,
      followUpRequested,
      recommendation: {
        instruction,
        additionalInstructions: additionalInstruction,
        recommendationDate,
        recommendedBy
      },
      participant: {
        name: participantName,
        type: participantType
      },
      observations,
      conditions: conditions as unknown as Condition[], // conditionId is not optional,
      valid: isValid === 'valid'
    };
    onUpdate?.(encounter);
  }, [
    reasonCode,
    status,
    encounterType,
    location,
    serviceProvider,
    followUpRequested,
    instruction,
    additionalInstruction,
    recommendationDate,
    recommendedBy,
    participantName,
    participantType,
    observations,
    conditions,
    onUpdate,
    isValid
  ]);

  return (
    <div class="encounter-container">
      <div class="oj-flex-bar">
        <div class="oj-flex-bar-start">{encounter?.type || ''}</div>
        <div class="oj-flex-bar-end oj-text-secondary-color">
          {userRole == 'PATIENT' ? encounter.patientName : encounter?.participant?.name}
        </div>
      </div>
      <hr />
      <div class="encounter-body">
        <h3 class="oj-typography-subheading-sm">Encounter</h3>
        <oj-validation-group
          onvalidChanged={(valid) => {
            setIsValid(valid.detail.value);
          }}
        >
          <oj-form-layout maxColumns={2} readonly={readonly}>
            <CodeSelection
              labelHint="Reason Code"
              type={CodeType.Reason}
              required={true}
              readonly={readonly}
              value={reasonCode}
              onvalueChanged={(id) => setReasonCode(id)}
            />
            <InputText
              required={true}
              readonly={readonly}
              labelHint="Status"
              value={status}
              onValueChanged={setStatus}
            />
            <CodeSelection
              required={true}
              labelHint="Type"
              type={CodeType.Encounter}
              readonly={readonly}
              value={encounterType}
              onvalueChanged={onEncounterTypeChanged}
            />
            <InputText
              required={true}
              readonly={readonly}
              labelHint="Location"
              value={location}
              onValueChanged={setLocation}
            />
            <InputText
              required={true}
              readonly={readonly}
              labelHint="Service Provider"
              value={serviceProvider}
              onValueChanged={setServiceProvider}
            />
            <oj-checkboxset
              readonly={readonly}
              translations={{ readonlyNoValue: 'No' }}
              label-hint="Follow up"
              value={followUpRequested ? ['yes'] : []}
              onvalueChanged={(evt) => setFollowUpRequested(!!evt.detail.value?.length)}
            >
              <oj-option value="yes">Yes</oj-option>
            </oj-checkboxset>
          </oj-form-layout>
          <h3 class="oj-typography-subheading-sm">Participant</h3>
          <oj-form-layout maxColumns={2} readonly={readonly}>
            <InputText
              required={true}
              readonly={readonly}
              labelHint="Participant Name"
              value={participantName}
              onValueChanged={setParticipantName}
            />
            <InputText
              required={true}
              readonly={readonly}
              labelHint="Participant Type"
              value={participantType}
              onValueChanged={setParticipantType}
            />
          </oj-form-layout>
          <Observations observations={observations} readonly={readonly} onChanged={onObservationsChanged} />

          <Conditions conditions={conditions} readonly={readonly} onChanged={onConditionsChanged} />
          <h3 class="oj-typography-subheading-sm">Recommendation</h3>
          <oj-form-layout class="oj-formlayout-full-width" maxColumns={1} readonly={readonly}>
            <TextArea
              required={true}
              readonly={readonly}
              labelHint="Instruction"
              value={instruction}
              onValueChanged={setInstruction}
            />
            <TextArea
              required={true}
              readonly={readonly}
              labelHint="Additional Instructions"
              value={additionalInstruction}
              onValueChanged={setAdditionalInstruction}
            />
          </oj-form-layout>
          <oj-form-layout maxColumns={2} readonly={readonly}>
            <oj-input-date-time
              required={true}
              readonly={readonly}
              labelHint="Recommendation Date"
              value={recommendationDate}
              onvalueChanged={(evt) => setRecommendationDate(evt.detail.value)}
            />
            <InputText
              readonly={readonly}
              labelHint="Recommended By"
              value={recommendedBy}
              onValueChanged={setRecommendedBy}
            />
          </oj-form-layout>
        </oj-validation-group>
      </div>
    </div>
  );
}
