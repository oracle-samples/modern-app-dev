/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';
import { Appointments } from './appointments';
import { Providers } from './providers';
import { Overview as PatientOverview } from './overview';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Encounters } from './encounters';
import { ProviderDetails } from './providers/details';

export function Patient() {
  return (
    <div class="patient-background oj-web-applayout-page">
      <div class="patient-landing">
        <Routes>
          <Route path="appointments" element={<Appointments />} />
          <Route path="appointments/:appointmentId" element={<Appointments />} />
          <Route path="providers" element={<Providers />} />
          <Route path="providers/:providerId" element={<ProviderDetails />} />
          <Route path="encounters" element={<Encounters />} />
          <Route path="encounters/:encounterId" element={<Encounters />} />
          <Route path="" element={<PatientOverview />} />
          <Route path="*" element={<Navigate to="/404" />} />
        </Routes>
      </div>
    </div>
  );
}
