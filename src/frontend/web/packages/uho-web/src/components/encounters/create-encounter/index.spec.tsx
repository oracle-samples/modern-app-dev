/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { CreateEncounter } from '.';
import { h } from 'preact';
import { queryClient, snapshot } from '../../../../test/jetHelper';
import { QueryClientProvider } from '@tanstack/react-query';

snapshot(
  'CreateEncounter',
  <body>
    <QueryClientProvider client={queryClient}>
      <CreateEncounter appointmentId={1} onClose={console.log} patientId={1} providerId={1} show={true} />
    </QueryClientProvider>
  </body>
);
