/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojselectcombobox';
import { Button } from 'oj-c/button';
import { h } from 'preact';
import { useMemo } from 'preact/hooks';
import { ojCombobox } from 'ojs/ojselectcombobox';
import { CodeType, Condition as ConditionType } from '@uho/encounter-api-client/dist/api-client';
import { RecursivePartial } from '../../../../typings';
import { CodeSelection } from '../../encounters/codes';
import { useI18n } from 'hooks/useI18n';

type Props = Readonly<{
  condition: RecursivePartial<ConditionType>;
  readonly: boolean;
  onDelete?: () => void;
  onChanged?: (condition: RecursivePartial<ConditionType>) => void;
}>;

export function Condition({ condition, readonly, onDelete, onChanged }: Props) {
  const i18n = useI18n().conditions;

  // https://terminology.hl7.org/3.0.0/CodeSystem-condition-category.html
  const categories = useMemo<ojCombobox.Option[]>(
    () => [
      { label: i18n.categories.problemListItem(), value: 'problem-list-item' },
      { label: i18n.categories.encounterDiagnosis(), value: 'encounter-diagnosis' }
    ],
    [i18n]
  );

  // https://terminology.hl7.org/3.0.0/CodeSystem-condition-ver-status.html
  const verificationStatus = useMemo<ojCombobox.Option[]>(
    () => [
      { label: i18n.verificationStatus.unconfirmed(), value: 'unconfirmed' },
      { label: i18n.verificationStatus.provisional(), value: 'provisional' },
      { label: i18n.verificationStatus.differential(), value: 'differential' },
      { label: i18n.verificationStatus.confirmed(), value: 'confirmed' },
      { label: i18n.verificationStatus.refuted(), value: 'refuted' },
      { label: i18n.verificationStatus.enteredInError(), value: 'entered-in-error' }
    ],
    [i18n]
  );

  // https://terminology.hl7.org/3.0.0/CodeSystem-condition-clinical.html
  const clinicalStatus = useMemo<ojCombobox.Option[]>(
    () => [
      { label: i18n.clinicalStatus.active(), value: 'active' },
      { label: i18n.clinicalStatus.recurrence(), value: 'recurrence' },
      { label: i18n.clinicalStatus.relapse(), value: 'relapse' },
      { label: i18n.clinicalStatus.inactive(), value: 'inactive' },
      { label: i18n.clinicalStatus.remission(), value: 'remission' },
      { label: i18n.clinicalStatus.resolved(), value: 'resolved' }
    ],
    [i18n]
  );

  const updateCondition = useMemo(
    () => (partialCondition: Partial<ConditionType>) => {
      const updatedCondition = { ...condition, ...partialCondition };
      onChanged?.(updatedCondition);
    },
    [condition, onChanged]
  );

  return (
    <div class="oj-panel oj-flex oj-sm-justify-content-space-between">
      <oj-form-layout
        readonly={readonly}
        columns={2}
        maxColumns={3}
        class={`oj-flex-item oj-sm-0 ${!readonly ? 'condition-edit' : ''} oj-sm-padding-2x-end`}
      >
        <oj-combobox-one
          readonly={readonly}
          required={true}
          options={categories}
          value={condition.category}
          onvalueChanged={(evt) => updateCondition({ category: evt.detail.value })}
          labelHint={i18n.categoryLabel()}
        />
        <CodeSelection
          required={true}
          labelHint={i18n.codeLabel()}
          type={CodeType.Condition}
          readonly={readonly}
          value={condition.code}
          onvalueChanged={(code) => updateCondition({ code })}
        />
        <oj-combobox-one
          readonly={readonly}
          required={true}
          options={clinicalStatus}
          value={condition.clinicalStatus}
          onvalueChanged={(evt) => updateCondition({ clinicalStatus: evt.detail.value })}
          labelHint={i18n.clinicalStatusLabel()}
        />
        <oj-combobox-one
          readonly={readonly}
          required={true}
          options={verificationStatus}
          value={condition.verificationStatus}
          onvalueChanged={(evt) => updateCondition({ verificationStatus: evt.detail.value })}
          labelHint={i18n.verificationStatusLabel()}
        />
        <oj-input-date-time
          required={true}
          readonly={readonly}
          labelHint={i18n.dateRecordedLabel()}
          value={condition.recordedDate}
          onvalueChanged={(evt) => updateCondition({ recordedDate: evt.detail.value })}
        />
      </oj-form-layout>
      {!readonly && (
        <Button
          label={i18n.delete()}
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
