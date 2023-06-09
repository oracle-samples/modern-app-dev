/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Button } from 'oj-c/button';
import 'ojs/ojformlayout';
import 'ojs/ojinputtext';
import 'ojs/ojdatetimepicker';
import 'ojs/ojinputnumber';
import 'ojs/ojselectcombobox';

import { h } from 'preact';
import { useMemo } from 'preact/hooks';
import { Observation } from '@uho/encounter-api-client/dist/api-client';
import { Observation as ObservationComp } from './observation';
import { generateGuid } from '../../utils/uuid';
import { RecursivePartial } from '../../../typings';
import { useI18n } from 'hooks/useI18n';

type Props = Readonly<{
  observations: RecursivePartial<Observation>[];
  readonly: boolean;
  onChanged: (observations: RecursivePartial<Observation>[]) => void;
}>;

export function Observations({ observations, readonly, onChanged }: Props) {
  const i18n = useI18n().observations;
  const updateObservation = useMemo(
    () => (index: number, observation: RecursivePartial<Observation>) => {
      const updatedObservations = [...observations.slice(0, index), observation, ...observations.slice(index + 1)];
      onChanged(updatedObservations);
    },
    [observations, onChanged]
  );

  const deleteObservation = useMemo(
    () => (index: number) => {
      const updatedObservations = [...observations.slice(0, index), ...observations.slice(index + 1)];
      onChanged(updatedObservations);
    },
    [observations, onChanged]
  );

  const addObservation = useMemo(
    () => () => {
      const updatedObservations = [...observations, { observationId: generateGuid() }];
      onChanged(updatedObservations);
    },
    [observations, onChanged]
  );

  const observationElements = useMemo(() => {
    return observations.map((o, i) => (
      <div
        key={o.observationId}
        class={`${
          readonly ? 'oj-xl-4 oj-lg-6 oj-sm-6' : 'oj-sm-12'
        } oj-flex-item oj-sm-padding-2x-end oj-sm-padding-2x-bottom`}
      >
        <ObservationComp
          observation={o}
          readonly={readonly}
          onDelete={() => deleteObservation(i)}
          onChanged={(updatedObservation) => updateObservation(i, updatedObservation)}
        />
      </div>
    ));
  }, [observations, deleteObservation, updateObservation, readonly]);

  return (
    <div class="oj-sm-margin-2x-vertical">
      <h3 class="oj-typography-subheading-sm">{i18n.title()}</h3>
      <div class={`oj-flex ${readonly ? 'oj-sm-flex-direction-row' : 'oj-sm-flex-direction-column'}`}>
        {observationElements}
        {!readonly && (
          <div class="oj-flex oj-sm-justify-content-flex-end oj-flex-item oj-sm-margin-4x-top">
            <Button
              label={i18n.addObservation()}
              startIcon={<span class="oj-ux-ico-plus" />}
              chroming="borderless"
              onOjAction={addObservation}
            />
          </div>
        )}
      </div>
    </div>
  );
}
