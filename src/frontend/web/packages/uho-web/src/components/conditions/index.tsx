/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Button } from 'oj-c/button';
import { h } from 'preact';
import { useMemo } from 'preact/hooks';
import { Condition } from '@uho/encounter-api-client/dist/api-client';
import { Condition as ConditionComp } from './condition';
import { generateGuid } from '../../utils/uuid';
import { RecursivePartial } from '../../../typings';
import { useI18n } from 'hooks/useI18n';

type Props = Readonly<{
  conditions: RecursivePartial<Condition>[];
  readonly: boolean;
  onChanged: (conditions: RecursivePartial<Condition>[]) => void;
}>;

export function Conditions({ conditions, readonly, onChanged }: Props) {
  const i18n = useI18n().conditions;
  const updateCondition = useMemo(
    () => (index: number, condition: RecursivePartial<Condition>) => {
      const updatedConditions = [...conditions.slice(0, index), condition, ...conditions.slice(index + 1)];
      onChanged(updatedConditions);
    },
    [conditions, onChanged]
  );

  const deleteCondition = useMemo(
    () => (index: number) => {
      const updatedConditions = [...conditions.slice(0, index), ...conditions.slice(index + 1)];
      onChanged(updatedConditions);
    },
    [conditions, onChanged]
  );

  const addCondition = useMemo(
    () => () => {
      const updatedConditions: RecursivePartial<Condition>[] = [
        ...conditions,
        { conditionId: generateGuid() } as Condition
      ];
      onChanged(updatedConditions);
    },
    [conditions, onChanged]
  );

  const conditionElements = useMemo(() => {
    return conditions.map((o, i) => (
      <div
        key={o.conditionId}
        class={`${
          readonly ? 'oj-xl-4 oj-lg-6 oj-sm-6' : 'oj-sm-12'
        } oj-flex-item oj-sm-padding-2x-end oj-sm-padding-2x-bottom`}
      >
        <ConditionComp
          condition={o}
          readonly={readonly}
          onDelete={() => deleteCondition(i)}
          onChanged={(updatedCondition) => updateCondition(i, updatedCondition)}
        />
      </div>
    ));
  }, [conditions, deleteCondition, updateCondition, readonly]);

  return (
    <div class="oj-sm-margin-2x-vertical">
      <h3 class="oj-typography-subheading-sm">{i18n.title()}</h3>
      <div class={`oj-flex ${readonly ? 'oj-sm-flex-direction-row' : 'oj-sm-flex-direction-column'}`}>
        {conditionElements}
        {!readonly && (
          <div class="oj-flex oj-sm-justify-content-flex-end oj-flex-item oj-sm-margin-4x-top">
            <Button
              label={i18n.addCondition()}
              startIcon={<span class="oj-ux-ico-plus" />}
              chroming="borderless"
              onOjAction={addCondition}
            />
          </div>
        )}
      </div>
    </div>
  );
}
