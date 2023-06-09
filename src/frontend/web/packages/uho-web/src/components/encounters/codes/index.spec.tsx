/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { CodeSelection } from '.';
import { h } from 'preact';
import { queryClient, snapshot } from '../../../../test/jetHelper';
import { CodeType } from '@uho/encounter-api-client/dist/api-client';
import { QueryClientProvider } from '@tanstack/react-query';

snapshot(
  'CodeSelection',
  <QueryClientProvider client={queryClient}>
    <CodeSelection labelHint="Hint" readonly={false} required={true} type={CodeType.Condition} />
  </QueryClientProvider>
);
