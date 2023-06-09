/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Patient } from '@uho/patient-api-client/dist/api-client';
import { Provider } from '@uho/provider-api-client/dist/api-client';

export function getInitialsFromPatient(patient?: Patient) {
  if (!patient || !patient.name) {
    return '';
  }
  return getInitialsFromName(...patient.name.split(' '));
}

export function getInitialsFromProvider(provider?: Provider) {
  if (!provider) {
    return '';
  }
  return getInitialsFromName(provider.firstName, provider.lastName);
}

export function getInitialsFromName(...names: (string | undefined)[]) {
  if (!names) {
    return '';
  }
  return names.map((part) => (part ? part.substring(0, 1).toUpperCase() : '')).join('');
}
