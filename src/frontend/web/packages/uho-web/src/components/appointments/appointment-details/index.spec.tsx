/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { AppointmentDetails } from '.';
import { h } from 'preact';
import { queryClient, snapshot } from '../../../../test/jetHelper';
import { QueryClientProvider } from '@tanstack/react-query';

snapshot(
  'AppointmentDetails',
  <QueryClientProvider client={queryClient}>
    <AppointmentDetails appointmentId={1} userRole="PATIENT" onSelectEncounter={console.log} />
  </QueryClientProvider>
);
