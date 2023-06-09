/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import {
  Provider,
  Schedule,
  Gender,
  Feedback,
  Status,
  ProviderApi,
  CreateFeedbackRequest,
  CreateProviderRequest,
  CreateScheduleRequest,
  DeleteProviderRequest,
  GetProviderRequest,
  DeleteScheduleRequest,
  GetProviderByUsernameRequest,
  GetScheduleRequest,
  ListFeedbacksRequest,
  FeedbackCollection,
  ListProvidersRequest,
  ProviderCollection,
  ScheduleCollection,
  ListSchedulesRequest,
  ListSlotsRequest,
  SlotCollection,
  FeedbackSummary
} from '@uho/provider-api-client';
import { mock, mockDate, reject } from './utils';
import { SelectableSlot } from 'components/appointments/appointment-schedule';

export const providers: Provider[] = [
  {
    id: 1,
    firstName: 'John',
    middleName: 'A.',
    lastName: 'Smith',
    title: 'Chief of Surgery',
    username: 'jsmith',
    email: 'jsmith@example.com',
    phone: '(555) 555-1234',
    country: 'United States',
    zip: '98103',
    city: 'Seattle',
    gender: Gender.Female,
    hospitalAddress: '123 Main St.',
    hospitalName: 'Seattle Hospital',
    hospitalPhone: '(555) 555-5678',
    speciality: 'Physician',
    qualification: 'MD',
    designation: 'FACS',
    professionalSummary: 'Experienced surgeon with a passion for patient care.',
    tags: ['surgery', 'patient care', 'experienced'],
    expertise: 'General surgery, laparoscopic surgery, robotic surgery'
  },
  {
    id: 2,
    firstName: 'Emily',
    middleName: '',
    lastName: 'Nguyen',
    title: 'Neurologist',
    username: 'enguyen',
    email: 'enguyen@example.com',
    phone: '(555) 555-2345',
    country: 'United States',
    zip: '60611',
    city: 'Chicago',
    gender: Gender.Female,
    hospitalAddress: '456 State St.',
    hospitalName: 'Northwestern Memorial Hospital',
    hospitalPhone: '(555) 555-3456',
    speciality: 'Neurologist',
    qualification: 'MD',
    designation: 'FAAN',
    professionalSummary: "Caring neurologist with a passion for improving patients' quality of life.",
    tags: ['neurologist', 'patient care', 'caring'],
    expertise: "Epilepsy, Parkinson's disease, stroke"
  },
  {
    id: 3,
    firstName: 'David',
    middleName: 'T.',
    lastName: 'Kim',
    title: 'Cardiologist',
    username: 'dkim',
    email: 'dkim@example.com',
    phone: '(555) 555-3456',
    country: 'United States',
    zip: '77030',
    city: 'Houston',
    gender: Gender.Male,
    hospitalAddress: '123 Main St.',
    hospitalName: 'Houston Medical Center',
    hospitalPhone: '(555) 555-7890',
    speciality: 'Cardiologist',
    qualification: 'MD',
    designation: 'FACC',
    professionalSummary: 'Skilled cardiologist with a passion for heart health.',
    tags: ['cardiologist', 'heart health', 'skilled'],
    expertise: 'Heart disease prevention, heart failure, arrhythmias'
  },
  {
    id: 4,
    firstName: 'Emily',
    middleName: 'R.',
    lastName: 'Garcia',
    title: 'Obstetrician-Gynecologist',
    username: 'egarcia',
    email: 'egarcia@example.com',
    phone: '(555) 555-9012',
    country: 'United States',
    zip: '30303',
    city: 'Seattle',
    gender: Gender.Female,
    hospitalAddress: '321 Maple St.',
    hospitalName: 'Seattle Medical Center',
    hospitalPhone: '(555) 555-2345',
    speciality: 'Physician',
    qualification: 'MD',
    designation: 'FACOG',
    professionalSummary: "Compassionate OB/GYN with a focus on women's health and wellness.",
    tags: ['obstetrics', 'gynecology', "women's health"],
    expertise: 'Prenatal care, childbirth, menopause management'
  },
  {
    id: 5,
    firstName: 'Adam',
    middleName: '',
    lastName: 'Gonzalez',
    title: 'Bone Specialist',
    username: 'agonzalez',
    email: 'agonzalez@example.com',
    phone: '(555) 555-9012',
    country: 'United States',
    zip: '97209',
    city: 'Portland',
    gender: Gender.Male,
    hospitalAddress: '123 Main St.',
    hospitalName: 'Portland Orthopedic Center',
    hospitalPhone: '(555) 555-1234',
    speciality: 'BoneSpecialist',
    qualification: 'MD',
    designation: '',
    professionalSummary: 'Expert in the diagnosis and treatment of bone and joint disorders.',
    tags: ['bone specialist', 'orthopedics', 'expert'],
    expertise: 'Arthritis, joint replacement, sports injuries'
  },
  {
    id: 6,
    firstName: 'Jennifer',
    middleName: 'L.',
    lastName: 'Chen',
    title: 'Cardiologist',
    username: 'jchen',
    email: 'jchen@example.com',
    phone: '(555) 555-5678',
    country: 'United States',
    zip: '78701',
    city: 'Austin',
    gender: Gender.Female,
    hospitalAddress: '123 Main St.',
    hospitalName: 'Seton Medical Center',
    hospitalPhone: '(555) 555-6789',
    speciality: 'Cardiologist',
    qualification: 'MD',
    designation: '',
    professionalSummary: 'Dedicated cardiologist with a focus on patient care.',
    tags: ['cardiologist', 'patient care', 'dedicated'],
    expertise: 'Heart disease prevention, heart attacks, hypertension'
  },
  {
    id: 7,
    firstName: 'Michael',
    middleName: 'D.',
    lastName: 'Williams',
    title: 'Physician',
    username: 'mwilliams',
    email: 'mwilliams@example.com',
    phone: '(555) 555-6789',
    country: 'United States',
    zip: '75201',
    city: 'Dallas',
    gender: Gender.Male,
    hospitalAddress: '123 Main St.',
    hospitalName: 'UT Southwestern Medical Center',
    hospitalPhone: '(555) 555-9012',
    speciality: 'Physician',
    qualification: 'MD',
    designation: '',
    professionalSummary: 'Experienced physician with a passion for helping patients achieve their best health.',
    tags: ['physician', 'experienced', 'patient care'],
    expertise: 'Primary care, general medicine'
  },
  {
    id: 8,
    firstName: 'Julia',
    lastName: 'Gonzalez',
    title: 'Neurologist',
    username: 'juliag',
    email: 'juliag@example.com',
    phone: '(555) 555-1234',
    country: 'United States',
    zip: '77002',
    city: 'Houston',
    gender: Gender.Female,
    hospitalAddress: '123 Main St, Houston, TX',
    hospitalName: 'Houston Medical Center',
    hospitalPhone: '(555) 555-6789',
    speciality: 'Neurologist',
    qualification: 'MD, PhD',
    designation: 'Fellow',
    professionalSummary: 'Expert in the diagnosis and treatment of neurological disorders.',
    tags: ['neurology', 'brain', 'nervous system'],
    expertise: 'Diagnosis and treatment of neurological disorders'
  },
  {
    id: 9,
    firstName: 'James',
    lastName: 'Lee',
    title: 'Physician',
    username: 'jamesl',
    email: 'jamesl@example.com',
    phone: '(555) 555-1234',
    country: 'United States',
    zip: '75201',
    city: 'Dallas',
    gender: Gender.Male,
    hospitalAddress: '456 Oak St, Dallas, TX',
    hospitalName: 'Dallas General Hospital',
    hospitalPhone: '(555) 555-6789',
    speciality: 'Physician',
    qualification: 'MD',
    designation: 'Resident',
    professionalSummary: 'Experienced physician with expertise in internal medicine.',
    tags: ['internal medicine', 'primary care', 'general health'],
    expertise: 'Internal medicine'
  },
  {
    id: 10,
    firstName: 'Emily',
    lastName: 'Wang',
    title: 'Bone Specialist',
    username: 'emilyw',
    email: 'emilyw@example.com',
    phone: '(555) 555-1234',
    country: 'United States',
    zip: '97205',
    city: 'Portland',
    gender: Gender.Female,
    hospitalAddress: '789 Park Ave, Portland, OR',
    hospitalName: 'Portland Orthopedic Center',
    hospitalPhone: '(555) 555-6789',
    speciality: 'BoneSpecialist',
    qualification: 'DO',
    designation: 'Attending',
    professionalSummary: 'Expert in the treatment of bone and joint injuries and conditions.',
    tags: ['orthopedics', 'bone health', 'joint health'],
    expertise: 'Diagnosis and treatment of bone and joint injuries and conditions'
  }
];

const feedback: Feedback = {
  providerId: 1,
  text: 'This is my feedback',
  rating: 4
};

const schedule: Schedule = {
  startTime: mockDate(),
  endTime: mockDate(),
  providerId: 1
};

const feedbacks: FeedbackSummary[] = [
  { text: 'Very nice provider and super knowledgeable!', rating: 3, providerId: 1 },
  { text: 'Great doctor and staff', rating: 4, providerId: 1 },
  { text: 'Best doctor I have ever seen', rating: 5, providerId: 1 },
  { text: 'Did not like him', rating: 2, providerId: 1 },
  { text: 'Not very knowledgeable', rating: 1, providerId: 1 },
  { text: 'Late but very competent', rating: 3, providerId: 1 }
];

/**
 * Mocked Provider API for local development
 */
export class MockProviderApi extends ProviderApi {
  createFeedback(requestParameters: CreateFeedbackRequest, initOverrides?: RequestInit): Promise<Feedback> {
    return mock(feedback);
  }
  createProvider(requestParameters: CreateProviderRequest, initOverrides?: RequestInit): Promise<Provider> {
    return mock(requestParameters.createProviderDetailsRequest);
  }
  createSchedule(requestParameters: CreateScheduleRequest, initOverrides?: RequestInit): Promise<Schedule> {
    return mock(schedule);
  }
  deleteProvider(requestParameters: DeleteProviderRequest, initOverrides?: RequestInit): Promise<void> {
    const index = providers.findIndex((provider) => provider.id == requestParameters.providerId);
    providers.splice(index, 1);
    return mock(undefined);
  }
  deleteSchedule(requestParameters: DeleteScheduleRequest, initOverrides?: RequestInit): Promise<void> {
    return mock(undefined);
  }
  getProvider(requestParameters: GetProviderRequest, initOverrides?: RequestInit): Promise<Provider> {
    const provider = providers.find((e) => e.id == requestParameters.providerId);
    if (!provider) {
      return reject();
    }
    return mock(provider);
  }
  getProviderByUsername(
    requestParameters: GetProviderByUsernameRequest,
    initOverrides?: RequestInit
  ): Promise<Provider> {
    return mock(providers.find((p) => p.username === requestParameters.username)!);
  }
  getSchedule(requestParameters: GetScheduleRequest, initOverrides?: RequestInit): Promise<Schedule> {
    return mock(schedule);
  }
  listFeedbacks(requestParameters: ListFeedbacksRequest, initOverrides?: RequestInit): Promise<FeedbackCollection> {
    return mock({ items: feedbacks });
  }
  listProviders(
    { city, speciality, name }: ListProvidersRequest,
    initOverrides?: RequestInit
  ): Promise<ProviderCollection> {
    return mock({
      items: providers.filter((p) => {
        if (speciality && p.speciality != speciality) {
          return false;
        }
        if (city && !p.city?.toLowerCase().includes(city.toLowerCase())) {
          return false;
        }
        const pName = `${p.title} ${p.firstName} ${p.lastName}`;
        if (name && !pName.toLowerCase().includes(name.toLowerCase())) {
          return false;
        }
        return true;
      })
    });
  }
  listSchedules(requestParameters: ListSchedulesRequest, initOverrides?: RequestInit): Promise<ScheduleCollection> {
    return mock({ items: [schedule] });
  }
  listSlots(requestParameters: ListSlotsRequest, initOverrides?: RequestInit): Promise<SlotCollection> {
    return mock({ items: generateSlots(new Date(1654558710036), new Date(1654732825254)) });
  }
}

const hours = [
  [10, 0],
  [10, 30],
  [11, 0],
  [11, 30],
  [12, 0],
  [12, 30],
  [13, 0],
  [13, 30],
  [14, 0]
];

export function generateSlots(start: Date, end: Date) {
  const dates: Date[] = [];
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    dates.push(new Date(d));
  }
  let index = 0;
  const mockColumns = dates
    .map((time) => {
      return hours.map((hour) => {
        const startTime = new Date(time);
        startTime.setHours(hour[0]);
        startTime.setMinutes(hour[1]);

        const endTime = new Date(startTime);
        endTime.setMinutes(startTime.getMinutes() + 30);

        const slot: SelectableSlot = {
          startTime,
          endTime,
          id: index++,
          status: Math.random() > 0.4 ? Status.Available : Status.Unavailable,
          selected: false
        };
        return slot;
      });
    })
    .flatMap((n) => n);
  return mockColumns;
}
