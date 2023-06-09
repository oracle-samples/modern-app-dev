/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import {
  Appointment,
  AppointmentApi,
  AppointmentCollection,
  CreateAppointmentOperationRequest,
  DeleteAppointmentRequest,
  GetAppointmentRequest,
  ListAppointmentsRequest,
  Status,
  UpdateAppointmentOperationRequest
} from '@uho/appointment-api-client';
import { mock, mockDate, reject } from './utils';

const startDate = mockDate();
startDate.setHours(13);

let appointmentId = 1;
const appointment: Appointment = {
  endTime: startDate,
  id: appointmentId,
  patientId: 1,
  providerId: 1,
  startTime: startDate,
  status: Status.Confirmed
};

const appointments = [appointment];

/**
 * Mocked Appointment API for local development
 */
export class MockAppointmentApi extends AppointmentApi {
  createAppointment(
    requestParameters: CreateAppointmentOperationRequest,
    initOverrides?: RequestInit
  ): Promise<Appointment> {
    appointmentId++;
    const newAppointment = {
      ...requestParameters.createAppointmentRequest,
      patientId: appointmentId,
      providerId: appointmentId,
      id: appointmentId,
      status: Status.Confirmed
    };
    appointments.push(newAppointment);
    return mock(newAppointment);
  }
  deleteAppointment(requestParameters: DeleteAppointmentRequest, initOverrides?: RequestInit): Promise<void> {
    const index = appointments.findIndex((appointment) => appointment.id == requestParameters.appointmentId);
    appointments.splice(index, 1);
    return mock(undefined);
  }
  getAppointment(requestParameters: GetAppointmentRequest, initOverrides?: RequestInit): Promise<Appointment> {
    const appointment = appointments.find((e) => e.id == requestParameters.appointmentId);
    if (!appointment) {
      return reject();
    }
    return mock(appointment);
  }
  listAppointments(
    requestParameters: ListAppointmentsRequest,
    initOverrides?: RequestInit
  ): Promise<AppointmentCollection> {
    return mock({ items: [...appointments] });
  }
  updateAppointment(
    requestParameters: UpdateAppointmentOperationRequest,
    initOverrides?: RequestInit
  ): Promise<Appointment> {
    return mock(appointment);
  }
}
