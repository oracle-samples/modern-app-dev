/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojselectsingle';
import { h } from 'preact';
import ArrayDataProvider from 'ojs/ojarraydataprovider';
import { CodeType } from '@uho/encounter-api-client/dist/api-client';
import { useMemo } from 'preact/hooks';
import { useQuery } from '@tanstack/react-query';
import { encounterApi } from 'api';

type Props = Readonly<{
  readonly: boolean;
  required: boolean;
  onvalueChanged?: (code: string) => void;
  value?: string | undefined;
  type: CodeType;
  labelHint: string;
}>;

export function CodeSelection({ readonly, value, onvalueChanged, type, labelHint, required }: Props) {
  const result = useQuery(['codes', type], async () => {
    return (await encounterApi.listCodes({ type, limit: 1000 })).items || [];
  });
  const dp = useMemo(() => {
    return new ArrayDataProvider<string, { value: string; label: string }>(
      (result.data || []).map((r) => {
        return { value: r.text, label: r.text };
      }),
      { keyAttributes: 'value' }
    );
  }, [result.data]);

  return (
    <oj-select-single
      data={dp}
      required={required}
      readonly={readonly}
      value={value}
      onojValueAction={(evt) => onvalueChanged?.(evt.detail.value!)}
      itemText="label"
      labelHint={labelHint}
    />
  );
}
