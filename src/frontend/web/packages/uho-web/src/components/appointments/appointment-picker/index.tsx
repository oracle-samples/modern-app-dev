/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojdatetimepicker';
import 'ojs/ojformlayout';
import { h } from 'preact';
import { useState } from 'preact/hooks';
import { SelectableSlot } from '../appointment-schedule';
import { SlotPicker } from './slot-picker';
import { DatePicker } from './date-picker';
import { useQuery } from '@tanstack/react-query';
import { providerApi } from 'api';
import { IntlConverterUtils } from 'ojs/ojconverterutils-i18n';
import { now } from '../../../utils/dateProvider';
import { useI18n } from 'hooks/useI18n';

interface Selection {
  onSelection: (slot: SelectableSlot) => void;
}

type Props = Readonly<
  {
    selected?: SelectableSlot;
    className?: string;
    providerId: number;
  } & Selection
>;

function createDate(day: string, hour = 0) {
  const date = new Date(day);
  date.setHours;
  date.setHours(hour);
  date.setMinutes(0);
  date.setSeconds(0);
  date.setMilliseconds(0);
  return date;
}

function dayRange(day: string) {
  return {
    startTime: createDate(day, 7),
    endTime: createDate(day, 18)
  };
}

function getDateString(date: Date) {
  return IntlConverterUtils.dateToLocalIso(date);
}

export function AppointmentPicker({ selected, onSelection, providerId }: Props) {
  const i18n = useI18n().appointments;
  const [selectedSlot, setSelectedSlot] = useState<SelectableSlot | undefined>(selected);
  const [selectedDay, setSelectedDay] = useState<string>(getDateString(selected?.startTime! || now()));

  const appointmentQuery = useQuery(['slots', providerId, selectedDay], () => {
    return providerApi.listSlots({ providerId, ...dayRange(selectedDay), page: 0, limit: 50 });
  });

  return (
    <div class="oj-flex oj-sm-flex-items-1">
      <div class="oj-flex-item oj-sm-flex-0">
        <DatePicker
          onSelectedDay={(day) => {
            setSelectedDay(day);
          }}
          selectedDay={selectedDay}
        />
      </div>
      <div class="oj-flex-item oj-sm-padding-6x-start">
        <h3 class="oj-typography-subheading-sm">{i18n.availableTimes()}</h3>
        <SlotPicker
          slots={appointmentQuery.data?.items || []}
          selectedSlot={selectedSlot}
          onSelectedSlot={(slot) => {
            setSelectedSlot(slot);
            onSelection(slot);
          }}
        />
      </div>
    </div>
  );
}
