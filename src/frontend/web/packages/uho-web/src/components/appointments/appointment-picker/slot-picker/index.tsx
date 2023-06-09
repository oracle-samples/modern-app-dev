/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import 'ojs/ojformlayout';
import { Status } from '@uho/provider-api-client/dist/api-client';
import { SelectableSlot } from '../../appointment-schedule';

interface Properties {
  onSelectedSlot?: (slot: SelectableSlot) => void;
  selectedSlot?: SelectableSlot;
  slots: SelectableSlot[];
}

export function SlotPicker({ selectedSlot, onSelectedSlot, slots }: Properties) {
  return (
    <div class="oj-flex oj-sm-justify-content-start">
      {slots
        .sort((s, t) => s.startTime?.getTime()! - t.startTime?.getTime()!)
        .map((slot) => {
          const time = slot.startTime?.toLocaleString('en-us', {
            hour: '2-digit',
            minute: '2-digit',
            timeZone: 'America/Los_Angeles'
          });
          return (
            <div key={slot} style={{ width: '120px' }}>
              <oj-buttonset-many
                disabled={slot.status == Status.Unavailable}
                onvalueChanged={(event) => {
                  const val = event.detail.value || [];
                  if (val[0]) {
                    onSelectedSlot?.(slot);
                  }
                }}
                chroming="borderless"
                value={slot.id == selectedSlot?.id ? [selectedSlot?.id] : []}
              >
                <oj-option value={slot.id}>{time}</oj-option>
              </oj-buttonset-many>
            </div>
          );
        })}
    </div>
  );
}
