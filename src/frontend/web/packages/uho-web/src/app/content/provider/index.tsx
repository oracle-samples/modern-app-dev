/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';
import { Navigate, Route, Routes } from 'react-router-dom';
import { Appointments } from './appointments';
import { Encounters } from './encounters';
import { Overview } from './overview';

export function Provider() {
  return (
    <div class="provider-background oj-web-applayout-page">
      <div class="provider-landing">
        <Routes>
          <Route path="appointments" element={<Appointments />} />
          <Route path="appointments/:appointmentId" element={<Appointments />} />
          <Route path="encounters" element={<Encounters />} />
          <Route path="encounters/:encounterId" element={<Encounters />} />
          <Route path="/" element={<Overview />} />
          <Route path="*" element={<Navigate to="/404" />} />
        </Routes>
      </div>
    </div>
  );
}
