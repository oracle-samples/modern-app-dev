/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { Button } from 'oj-c/button';
import { InputNumber } from 'oj-c/input-number';
import 'ojs/ojselectcombobox';
import { h } from 'preact';
import { useMemo } from 'preact/hooks';
import { ojCombobox } from 'ojs/ojselectcombobox';
import { Observation as ObservationType } from '@uho/encounter-api-client/dist/api-client';
import { RecursivePartial } from '../../../../typings';

type Props = Readonly<{
  observation: RecursivePartial<ObservationType>;
  readonly: boolean;
  onDelete?: () => void;
  onChanged?: (observation: RecursivePartial<ObservationType>) => void;
}>;

// http://hl7.org/fhir/codesystem-observation-category.html
const categories: ojCombobox.Option[] = [
  { label: 'Vital Signs', value: 'vital-signs' },
  { label: 'Social History', value: 'social-history' },
  { label: 'Imaging', value: 'imaging' },
  { label: 'Laboratory', value: 'laboratory' },
  { label: 'Procedure', value: 'procedure' },
  { label: 'Survey', value: 'survey' },
  { label: 'Exam', value: 'exam' },
  { label: 'Therapy', value: 'therapy' },
  { label: 'Activity', value: 'activity' }
];

// http://hl7.org/fhir/valueset-observation-status.html
const status: ojCombobox.Option[] = [
  { label: 'Registered', value: 'registered' },
  { label: 'Preliminary', value: 'preliminary' },
  { label: 'Final', value: 'final' },
  { label: 'Amended', value: 'amended' },
  { label: 'Corrected', value: 'corrected' },
  { label: 'Cancelled', value: 'cancelled' },
  { label: 'Entered in Error', value: 'entered-in-error' },
  { label: 'Unknown', value: 'unknown' }
];

// http://hl7.org/fhir/R4/observation.html
const parameterTypes: ojCombobox.Option[] = [
  { label: 'Body Height', value: 'body-height' },
  { label: 'Body Weight', value: 'body-weight' },
  { label: 'Blood Pressure', value: 'blood-pressure' },
  { label: 'Temperature', value: 'temperature' },
  { label: 'Eye Color', value: 'eye-color' },
  { label: 'Bone Density', value: 'bone-density' },
  { label: 'Blood Glucose', value: 'blood-glucose' }
];

// https://ucum.org/ucum.html table 4
const units: ojCombobox.Option[] = [
  { label: 'Meter', value: 'meter' },
  { label: 'Second', value: 'second' },
  { label: 'Gram', value: 'gram' },
  { label: 'Celsius', value: 'celsius' },
  { label: 'Coulomb', value: 'coulomb' },
  { label: 'Candela', value: 'candela' }
];

export function Observation({ observation, readonly, onDelete, onChanged }: Props) {
  const updateObservation = useMemo(
    () => (partialObservation: RecursivePartial<ObservationType>) => {
      const updatedObservation = { ...observation, ...partialObservation };
      onChanged?.(updatedObservation);
    },
    [observation, onChanged]
  );

  return (
    <div class="oj-panel oj-flex oj-sm-justify-content-space-between">
      <oj-form-layout
        readonly={readonly}
        columns={2}
        maxColumns={3}
        class={`oj-flex-item oj-sm-0 ${!readonly ? 'observation-edit' : ''}`}
      >
        <oj-combobox-one
          readonly={readonly}
          options={categories}
          value={observation.category}
          onvalueChanged={(evt) => updateObservation({ category: evt.detail.value })}
          labelHint="Category"
        />
        <oj-combobox-one
          readonly={readonly}
          options={parameterTypes}
          value={observation.parameterType}
          onvalueChanged={(evt) => updateObservation({ parameterType: evt.detail.value })}
          labelHint="Parameter Type"
        />
        <oj-combobox-one
          readonly={readonly}
          options={units}
          value={observation.parameterValue?.unit}
          onvalueChanged={(evt) =>
            updateObservation({
              parameterValue: { ...observation.parameterValue!, unit: evt.detail.value! }
            })
          }
          labelHint="Unit"
        />
        <InputNumber
          readonly={readonly}
          labelHint="Value"
          value={observation.parameterValue?.value}
          onValueChanged={(value) =>
            updateObservation({
              parameterValue: { ...observation.parameterValue!, value }
            })
          }
        />
        <oj-input-date-time
          readonly={readonly}
          labelHint="Date Recorded"
          value={observation.dateRecorded}
          onvalueChanged={(evt) => updateObservation({ dateRecorded: evt.detail.value })}
        />
        <oj-combobox-one
          readonly={readonly}
          options={status}
          value={observation.status}
          onvalueChanged={(evt) => updateObservation({ status: evt.detail.value })}
          labelHint="Status"
        />
      </oj-form-layout>
      {!readonly && (
        <Button
          label="Delete"
          startIcon={<span class="oj-ux-ico-close" />}
          display="icons"
          chroming="borderless"
          class="oj-button-sm delete-button"
          onOjAction={onDelete}
        />
      )}
    </div>
  );
}
