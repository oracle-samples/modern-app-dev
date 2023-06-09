/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojdatetimepicker';
import { ojDatePicker, ojInputDate } from 'ojs/ojdatetimepicker';
import { h } from 'preact';
import { useRef } from 'preact/hooks';
import { now } from '../../../../utils/dateProvider';

interface Map<TValue> {
  [key: string]: TValue;
}
type DayFormatterResult = null | 'all' | ojInputDate.DayFormatterOutput;

type Props = Readonly<{ selectedDay: string; onSelectedDay?: (date: string) => void }>;

export function DatePicker({ selectedDay, onSelectedDay }: Props) {
  const requestCache: Map<Promise<void>> = {};
  const dateCache: Map<DayFormatterResult> = {};
  const datePickerRef = useRef<ojDatePicker>(null);

  const dayFormatter: (day: ojInputDate.DayFormatterInput) => DayFormatterResult = (day) => {
    if (!requestCache[`${day.fullYear}-${day.month}`]) {
      requestCache[`${day.fullYear}-${day.month}`] = fetch(day.fullYear, day.month);
    }
    return dateCache[`${day.fullYear}-${day.month}-${day.date}`];
  };

  const fetch = (year: number, month: number) => {
    const currentTime = now();
    return Promise.resolve().then((val) => {
      for (let day = 1; day < 31; day++) {
        let value: DayFormatterResult = null;
        if (day < currentTime.getDate() && month <= currentTime.getMonth() + 1) {
          value = { disabled: true, className: 'past' };
        }
        // TODO query elements and see if it's fully booked
        // if (day < 24) {
        //   value = { disabled: true, className: 'booked' };
        // }
        dateCache[`${year}-${month}-${day}`] = value;
      }
      if (datePickerRef.current != null) {
        const dayFormatter = datePickerRef.current.dayFormatter;
        datePickerRef.current.dayFormatter = () => null;
        datePickerRef.current.dayFormatter = dayFormatter;
      }
    });
  };

  return (
    <oj-date-picker
      ref={datePickerRef}
      onvalueChanged={(event) => {
        if (event.detail.updatedFrom === 'internal') {
          onSelectedDay?.(event.detail.value);
        }
      }}
      value={selectedDay}
      class="date-picker oj-flex-item"
      dayFormatter={dayFormatter}
    />
  );
}
