/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { PatientApi, EncounterApi, AppointmentApi, ProviderApi, FrontendApi } from './apiClients';
import { Configuration as FrontendConfiguration } from './frontendApi';
import { Configuration as ProviderConfiguration } from '@uho/provider-api-client/dist/api-client';
import { Configuration as EncounterConfiguration, EncounterApiInterface } from '@uho/encounter-api-client';
import { Configuration as AppointmentConfiguration, AppointmentApiInterface } from '@uho/appointment-api-client';
import { Configuration as PatientConfiguration, PatientApiInterface } from '@uho/patient-api-client/dist/api-client';

const basePath = '/home/api';

export const providerApi = new ProviderApi(
  new ProviderConfiguration({
    basePath
  })
);
export const patientApi: PatientApiInterface = new PatientApi(new PatientConfiguration({ basePath }));
export const encounterApi: EncounterApiInterface = new EncounterApi(new EncounterConfiguration({ basePath }));
export const appointmentApi: AppointmentApiInterface = new AppointmentApi(new AppointmentConfiguration({ basePath }));
export const frontendApi: FrontendApi = new FrontendApi(new FrontendConfiguration({ basePath }));
