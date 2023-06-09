/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import {
  AuthorizeDeviceRequest,
  CreatePatientRequest,
  DeletePatientRequest,
  Gender,
  GetPatientByUsernameRequest,
  GetPatientRequest,
  ListPatientsRequest,
  Patient,
  PatientApi,
  PatientCollection,
  UpdatePatientRequest
} from '@uho/patient-api-client/dist/api-client';
import { mock, mockDate, reject } from './utils';

const patients: Patient[] = [
  {
    city: 'San Francisco',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'brad.p.stark@gmail.com',
    username: 'bradpstark',
    gender: Gender.Male,
    id: 1,
    name: 'Brad P. Stark',
    phone: '985-435-4859',
    zip: '94404',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Los Angeles',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'jane.doe@gmail.com',
    username: 'janedoe',
    gender: Gender.Female,
    id: 2,
    name: 'Jane Doe',
    phone: '123-456-7890',
    zip: '90001',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'New York City',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'john.smith@gmail.com',
    username: 'johnsmith',
    gender: Gender.Male,
    id: 3,
    name: 'John Smith',
    phone: '555-555-5555',
    zip: '10001',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Chicago',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'sarah.jones@gmail.com',
    username: 'sarahjones',
    gender: Gender.Female,
    id: 4,
    name: 'Sarah Jones',
    phone: '987-654-3210',
    zip: '60601'
  },
  {
    city: 'Houston',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'mark.johnson@gmail.com',
    username: 'markjohnson',
    gender: Gender.Male,
    id: 5,
    name: 'Mark Johnson',
    phone: '111-222-3333',
    zip: '77001',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Miami',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'amy.wong@gmail.com',
    username: 'amywong',
    gender: Gender.Female,
    id: 6,
    name: 'Amy Wong',
    phone: '444-555-6666',
    zip: '33101',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Seattle',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'brian.wilson@gmail.com',
    gender: Gender.Male,
    username: 'brianwilson',
    id: 7,
    name: 'Brian Wilson',
    phone: '777-888-9999',
    zip: '98101',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Boston',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'emily.johnson@gmail.com',
    username: 'emilyjohnson',
    gender: Gender.Female,
    id: 8,
    name: 'Emily Johnson',
    phone: '555-444-3333',
    zip: '02101',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Denver',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'peter.davis@gmail.com',
    username: 'peterdavis',
    gender: Gender.Male,
    id: 9,
    name: 'Peter Davis',
    phone: '999-888-7777',
    zip: '80201',
    insuranceProvider: 'United Healthcare'
  },
  {
    city: 'Dallas',
    country: 'United States',
    dateOfBirth: mockDate(),
    email: 'jennifer.smith@gmail.com',
    username: 'jennifersmith',
    gender: Gender.Female,
    id: 10,
    name: 'Jennifer Smith',
    phone: '123-456-7890',
    zip: '75201',
    insuranceProvider: 'United Healthcare'
  }
];

/**
 * Mocked Patient API for local development
 */
export class MockPatientApi extends PatientApi {
  authorizeDevice(requestParameters: AuthorizeDeviceRequest, initOverrides?: RequestInit): Promise<string> {
    return mock('true');
  }
  createPatient({ createPatientDetailsRequest }: CreatePatientRequest, initOverrides?: RequestInit): Promise<Patient> {
    return mock({ ...createPatientDetailsRequest });
  }
  deletePatient(requestParameters: DeletePatientRequest, initOverrides?: RequestInit): Promise<void> {
    const index = patients.findIndex((patient) => patient.id == requestParameters.patientId);
    patients.splice(index, 1);
    return mock(undefined);
  }
  getPatient(requestParameters: GetPatientRequest, initOverrides?: RequestInit): Promise<Patient> {
    const patient = patients.find((e) => e.id == requestParameters.patientId);
    if (!patient) {
      return reject();
    }
    return mock(patient);
  }
  getPatientByUsername(requestParameters: GetPatientByUsernameRequest, initOverrides?: RequestInit): Promise<Patient> {
    const patient = patients.find((e) => e.username == requestParameters.username);
    if (!patient) {
      return reject();
    }
    return mock(patient);
  }
  listPatients(requestParameters: ListPatientsRequest, initOverrides?: RequestInit): Promise<PatientCollection> {
    return mock({ items: [...patients] });
  }
  updatePatient(requestParameters: UpdatePatientRequest, initOverrides?: RequestInit): Promise<Patient> {
    return mock(requestParameters.updatePatientDetailsRequest);
  }
}
