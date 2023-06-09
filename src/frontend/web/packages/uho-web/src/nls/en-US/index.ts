/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
export default {
  appName: () => 'Universal Healthcare Organization',
  landing: {
    title: () => 'Landing',
    header: () => 'Welcome to Universal Healthcare Organization',
    subheader: () => 'All the care you need. In one single place.',
    loginPatientTitle: () => 'Login as Patient',
    loginProviderTitle: () => 'Login as Provider'
  },
  notFound: {
    title: () => '404',
    header: () => 'Error 404',
    action: () => 'Go back to Home'
  },
  provider: {
    title: () => 'Provider',
    header: {
      home: () => 'Home',
      appointments: () => 'Appointments',
      encounters: () => 'Encounters'
    },
    appointments: {
      title: () => 'Appointments',
      description: () => 'See all appointments'
    },
    encounters: {
      title: () => 'Encounters',
      description: () => 'View all encounters'
    },
    overview: {
      appointmentsTitle: () => 'See Appointments',
      appointmentsDescription: () => 'See all past and upcoming appointments',
      encountersTitle: () => 'Past Encounters',
      encountersDescription: () => 'See the patient visit history and medications prescribed.',
      covidTitle: () => 'COVID 19',
      covidDescription: () => 'Everything to know about COVID-19',
      title: () => 'Welcome to Universal Healthcare Organization',
      description: () => 'All the care you need. In one single place.'
    }
  },
  patient: {
    title: () => 'Patient',
    header: {
      home: () => 'Home',
      providers: () => 'Providers',
      appointments: () => 'Appointments',
      encounters: () => 'Encounters'
    },
    encounters: {
      title: () => 'Encounters',
      description: () => 'See all encounters'
    },
    appointments: {
      title: () => 'Appointments',
      description: () => 'View and schedule appointments',
      requestAppointment: () => 'Request Appointment',
      scheduledConfirmationTitle: () => 'Appointment scheduled',
      scheduledConfirmationDescription: () => 'The appointment has been scheduled!'
    },
    overview: {
      scheduleAppointmentTitle: () => 'Schedule Appointment',
      scheduleAppointmentDescription: () => 'Do you need our help? Schedule an appointment now',
      pastVisitsTitle: () => 'Past Visits',
      pastVisitsDescription: () => 'See your visit history and medications prescribed.',
      covidTitle: () => 'COVID 19',
      covidDescription: () => 'Everything to know about COVID-19',
      healthcareTitle: () => 'Choose a Healthcare Specialist',
      healthcareDescription: () => 'Choose one of over 200 healthcare specialists',
      title: () => 'Welcome to Universal Healthcare Organization',
      description: () => 'All the care you need. In one single place.'
    },
    providers: {
      allLocations: () => 'All Locations',
      physician: () => 'Physician',
      cardiologist: () => 'Cardiologist',
      boneSpecialist: () => 'Bone Specialist',
      neurologist: () => 'Neurologist',
      ophthalmologist: () => 'Ophthalmologist',
      title: () => 'Find a provider',
      description: () => 'Search a provider by name, keyword, email or any other properties',
      searchSpecialist: () => 'Search for healthcare specialist...',
      searchCity: () => 'Search by city...'
    },
    provider: {
      about: {
        title: () => 'About Me',
        tags: () => 'Tags',
        interests: () => 'Interests',
        expertise: () => 'Expertise',
        bookAppointment: () => 'Book Appointment',
        appointmentScheduledTitle: () => 'Appointment Scheduled',
        appointmentScheduledDescription: () => 'The appointment has been scheduled:',
        appointmentStartTime: () => 'Start Time',
        appointmentEndTime: () => 'End Time'
      },
      experience: {
        title: () => 'Experience',
        timeAt: (hospital: string) => `Time at ${hospital}`,
        years: () => 'Years,',
        months: () => 'Months'
      },
      feedback: {
        title: () => 'Feedback'
      },
      organization: {
        title: () => 'Organization'
      },
      profile: {
        title: () => 'Profile'
      }
    }
  },
  appointments: {
    noAppointmentSelectedTitle: () => 'No Appointment selected',
    noAppointmentSelectedDescription: () => 'Select an appointment or request a new appointment',
    encountersTitle: () => 'Encounters',
    encounterCreatedTitle: () => 'Encounter created',
    encounterCreatedDescription: () => 'The appointment has been scheduled!',
    addEncounter: () => 'Add Encounter',
    startTime: () => 'Start Time',
    endTime: () => 'End Time',
    provider: (name: string) => `Provider ${name}`,
    availableTimes: () => 'Available Times',
    scheduleAppointmentTitle: () => 'Schedule an appointment',
    reasonForVisitLabel: () => 'Reason for Visit',
    reasonForVisitHint: () => 'My reason for the visit is ...',
    nameLabel: () => 'Name',
    phoneLabel: () => 'Phone',
    emailLabel: () => 'Email',
    cancel: () => 'Cancel',
    bookNow: () => 'Book Now'
  },
  conditions: {
    title: () => 'Conditions',
    addCondition: () => 'Add Condition',
    categories: {
      problemListItem: () => 'Problem List Item',
      encounterDiagnosis: () => 'Encounter Diagnosis'
    },
    verificationStatus: {
      unconfirmed: () => 'Unconfirmed',
      provisional: () => 'Provisional',
      differential: () => 'Differential',
      confirmed: () => 'Confirmed',
      refuted: () => 'Refuted',
      enteredInError: () => 'Entered in Error'
    },
    clinicalStatus: {
      active: () => 'Active',
      recurrence: () => 'Recurrence',
      relapse: () => 'Relapse',
      inactive: () => 'Inactive',
      remission: () => 'Remission',
      resolved: () => 'Resolved'
    },
    categoryLabel: () => 'Category',
    codeLabel: () => 'Code',
    clinicalStatusLabel: () => 'Clinical Status',
    verificationStatusLabel: () => 'Verification Status',
    dateRecordedLabel: () => 'Date Recorded',
    delete: () => 'Delete'
  },
  encounters: {
    createEncounter: {
      title: () => 'Create Encounter',
      cancel: () => 'Cancel',
      create: () => 'Create'
    },
    noneSelectedTitle: () => 'No encounter selected',
    noneSelectedDescription: () => 'Select an encounter or submit a new encounter'
  },
  feedback: {
    likeProvider: () => 'like this provider',
    notLikeProvider: () => 'do not like this provider'
  },
  footer: {
    aboutOracle: () => 'About Oracle',
    contactUs: () => 'Contact Us',
    legalNotices: () => 'Legal Notices',
    termsOfUse: () => 'Terms of Use',
    privacyRights: () => 'Your Privacy Rights'
  },
  header: {
    logout: () => 'Logout'
  },
  dashboard: {
    title: () => 'Quick Actions'
  },
  observations: {
    title: () => 'Observations',
    addObservation: () => 'Add Observation'
  },
  patientList: {
    cardView: () => 'Card',
    listView: () => 'List',
    results: (count: number) => `${count} results`
  },
  providerList: {
    cardView: () => 'Card',
    listView: () => 'List',
    results: (count: number) => `${count} results`
  },
  providerProfile: {
    patientRole: () => 'Your Primary Care Physician',
    providerRole: () => 'Your Profile',
    emailLabel: () => 'Email',
    phoneLabel: () => 'Phone',
    facilityLabel: () => 'Facility',
    addressLabel: () => 'Address'
  }
};
