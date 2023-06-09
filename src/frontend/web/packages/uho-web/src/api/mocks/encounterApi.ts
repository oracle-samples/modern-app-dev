/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import {
  CodeCollection,
  CodeSummary,
  CodeType,
  Condition,
  CreateEncounterRequest,
  DeleteEncounterRequest,
  Encounter,
  EncounterApi,
  EncounterCollection,
  EncounterSummary,
  GetEncounterRequest,
  ListCodesRequest,
  ListEncountersRequest,
  Observation,
  Recommendation,
  UpdateEncounterRequest
} from '@uho/encounter-api-client';
import { getUniqueId } from 'ojs/ojvcomponent';
import { mock, mockDate, reject } from './utils';

const recommendation: Recommendation = {
  additionalInstructions: 'None',
  instruction: 'Test recommendation',
  recommendationDate: mockDate().toISOString(),
  recommendationId: '31e59371-98ba-4ab4-854e-5e016965c3di',
  recommendedBy: 'Dr. Danilo Mraz'
};

const condition1: Condition = {
  category: 'encounter-diagnosis',
  clinicalStatus: 'active',
  code: '160245001',
  conditionId: 'be4ae8a8-6002-4b66-883f-f9351d7f09cc',
  recordedDate: mockDate().toISOString(),
  verificationStatus: 'confirmed'
};
const condition2: Condition = {
  category: 'encounter-diagnosis',
  clinicalStatus: 'active',
  code: '160245001',
  conditionId: 'be4ae8a8-6002-4b66-883f-f9351d7f09cd',
  recordedDate: mockDate().toISOString(),
  verificationStatus: 'confirmed'
};

const observation: Observation = {
  category: 'vital-signs',
  dateRecorded: mockDate().toISOString(),
  observationId: '0b07bd29-2cf6-4253-974c-6b595b4a955f',
  parameterType: 'Body Height',
  parameterValue: {
    unit: 'meter',
    value: 1.8
  },
  status: 'final'
};

export const encounter: Encounter & EncounterSummary = {
  status: 'triage',
  type: '162673000',
  reasonCode: 'Choroidal hemorrhage',
  location: 'NP2U, LLC',
  serviceProvider: 'provider',
  encounterId: '31e59371-98ba-4ab4-854e-5e016965c3df',
  followUpRequested: true,
  recommendation,
  participant: {
    name: 'Foo',
    type: 'type'
  },

  providerId: 1,
  patientId: 22,
  patientName: 'Mr Patient',
  appointmentId: 1,
  observations: [observation, observation],
  conditions: [condition1, condition2],
  period: {
    startDate: mockDate().toISOString(),
    endDate: mockDate().toISOString()
  },
  providerName: 'Dr. Foo',
  recommendationText: 'My Recommendation'
};

const encounters: (Encounter & EncounterSummary)[] = [
  { ...encounter, type: '162673000', encounterId: '1', providerName: 'Dr. Foo' },
  { ...encounter, type: '162673000', encounterId: '2', providerName: 'Dr. Bar' },
  { ...encounter, type: '162673000', encounterId: '3', providerName: 'Dr. Jonah' },
  { ...encounter, type: '162673000', encounterId: '4', providerName: 'Dr. Zeus' },
  { ...encounter, type: '162673000', encounterId: '5', providerName: 'Dr. Bar' },
  { ...encounter, type: '162673000', encounterId: '6', providerName: 'Dr. Jonah' },
  { ...encounter, type: '162673000', encounterId: '7', providerName: 'Dr. Zeus' }
];

const reasonTypes: CodeSummary[] = [
  { code: '006', text: 'Anxiety disorder of childhood OR adolescence' },
  { code: '122003', text: 'Choroidal hemorrhage' },
  { code: '127009', text: 'Spontaneous abortion with laceration of cervix' },
  { code: '129007', text: 'Homoiothermia' },
  { code: '134006', text: 'Decreased hair growth' },
  { code: '140004', text: 'Chronic pharyngitis' },
  { code: '144008', text: 'Normal peripheral vision' },
  { code: '147001', text: 'SuperÞcial foreign body of scrotum without major open wound but with infection' },
  { code: '150003', text: 'Abnormal bladder continence' },
  { code: '151004', text: 'Meningitis due to gonococcus' },
  { code: '162004', text: 'Severe manic bipolar I disorder without psychotic features' },
  { code: '165002', text: 'Accident-prone' },
  { code: '168000', text: 'Typhlolithiasis' },
  { code: '171008', text: 'Injury of ascending right colon without open wound into abdominal cavity' },
  { code: '172001', text: 'Endometritis following molar AND/OR ectopic pregnancy' },
  { code: '175004', text: 'Supraorbital neuralgia' },
  { code: '177007', text: 'Poisoning by sawßy larvae' },
  { code: '179005', text: 'Apraxia of dressing' },
  { code: '181007', text: 'Hemorrhagic bronchopneumonia' },
  { code: '183005', text: 'Autoimmune pancytopenia' }
];
const encounterTypes: CodeSummary[] = [
  { code: '410620009', text: 'Well child visit (procedure)' },
  { code: '162673000', text: 'General examination of patient (procedure)' },
  { code: '185349003', text: 'Encounter for check up (procedure)' },
  { code: '185347001', text: 'Encounter for problem (procedure)' },
  { code: '185345009', text: 'Encounter for symptom' },
  { code: '390906007', text: 'Follow-up encounter (procedure)' },
  { code: '50849002', text: 'Emergency room admission (procedure)' },
  { code: '439740005', text: 'Postoperative follow-up visit (procedure)' },
  { code: '410410006', text: 'Screening surveillance (regime/therapy)' },
  { code: '308335008', text: 'Patient encounter procedure' },
  { code: '698314001', text: 'Consultation for treatment' },
  { code: '33879002', text: 'Administration of vaccine to produce active immunity (procedure)' },
  { code: '424441002', text: 'Prenatal initial visit' },
  { code: '424619006', text: 'Prenatal visit' },
  { code: '183460006', text: 'Obstetric emergency hospital admission' },
  { code: '169762003', text: 'Postnatal visit' },
  { code: '702927004', text: 'Urgent care clinic (procedure)' },
  { code: '449411000124106', text: 'Admission to skilled nursing facility (procedure)' },
  { code: '394701000', text: 'Asthma follow-up' },
  { code: '183478001', text: 'Emergency hospital admission for asthma' }
];
const observationTypes: CodeSummary[] = [
  { code: '8302-2', text: 'Body Height' },
  { code: '72514-3', text: 'Pain severity - 0-10 verbal numeric rating [Score] - Reported' },
  { code: '29463-7', text: 'Body Weight' },
  { code: '39156-5', text: 'Body Mass Index' },
  { code: '85354-9', text: 'Blood Pressure' },
  { code: '8867-4', text: 'Heart rate' },
  { code: '9279-1', text: 'Respiratory rate' },
  { code: '2093-3', text: 'Total Cholesterol' },
  { code: '2571-8', text: 'Triglycerides' },
  { code: '18262-6', text: 'Low Density Lipoprotein Cholesterol' },
  { code: '2085-9', text: 'High Density Lipoprotein Cholesterol' },
  { code: '6690-2', text: 'Leukocytes [#/volume] in Blood by Automated count' },
  { code: '789-8', text: 'Erythrocytes [#/volume] in Blood by Automated count' },
  { code: '718-7', text: 'Hemoglobin [Mass/volume] in Blood' },
  { code: '4544-3', text: 'Hematocrit [Volume Fraction] of Blood by Automated count' },
  { code: '787-2', text: 'MCV [Entitic volume] by Automated count' },
  { code: '785-6', text: 'MCH [Entitic mass] by Automated count' },
  { code: '786-4', text: 'MCHC [Mass/volume] by Automated count' },
  { code: '21000-5', text: 'Erythrocyte distribution width [Entitic volume] by Automated count' },
  { code: '777-3', text: 'Platelets [#/volume] in Blood by Automated count' }
];
const conditionTypes: CodeSummary[] = [
  { code: '160968000', text: 'Risk activity involvement (finding)' },
  { code: '278860009', text: 'Chronic low back pain (finding)' },
  { code: '38822007', text: 'Cystitis' },
  { code: '248595008', text: 'Sputum finding (finding)' },
  { code: '43724002', text: 'Chill (finding)' },
  { code: '88805009', text: 'Chronic congestive heart failure (disorder)' },
  { code: '156073000', text: 'Fetus with unknown complication' },
  { code: '58150001', text: 'Fracture of clavicle' },
  { code: '185086009', text: 'Chronic obstructive bronchitis (disorder)' },
  { code: '239720000', text: 'Tear of meniscus of knee' },
  { code: '239873007', text: 'Osteoarthritis of knee' },
  { code: '241929008', text: 'Acute allergic reaction' },
  { code: '195662009', text: 'Acute viral pharyngitis (disorder)' },
  { code: '403191005', text: 'Second degree burn' },
  { code: '408512008', text: 'Body mass index 40+ - severely obese (finding)' },
  { code: '90560007', text: 'Gout' },
  { code: '196416002', text: 'Impacted molars' },
  { code: '55680006', text: 'Drug overdose' },
  { code: '110030002', text: 'Concussion injury of brain' },
  { code: '275272006', text: 'Brain damage - traumatic' }
];

/**
 * Mocked Encounter API for local development
 */
export class MockEncounterApi extends EncounterApi {
  createEncounter(requestParameters: CreateEncounterRequest, initOverrides?: RequestInit): Promise<Encounter> {
    const createdEncounter: EncounterSummary & Encounter = {
      ...(requestParameters.encounter as Encounter),
      encounterId: getUniqueId(),
      providerName: 'Dr. Foo',
      recommendationText: 'My Recommendation'
    };
    encounters.push(createdEncounter);
    return mock(createdEncounter);
  }

  listCodes(requestParameters: ListCodesRequest, initOverrides?: RequestInit): Promise<CodeCollection> {
    let types: CodeSummary[] = [];
    switch (requestParameters.type) {
      case CodeType.Encounter:
        types = encounterTypes;
        break;
      case CodeType.Reason:
        types = reasonTypes;
        break;
      case CodeType.Observation:
        types = observationTypes;
        break;
      case CodeType.Condition:
        types = conditionTypes;
        break;
    }
    return mock({
      items: [...types]
    });
  }

  deleteEncounter(requestParameters: DeleteEncounterRequest, initOverrides?: RequestInit): Promise<void> {
    const index = encounters.findIndex((encounter) => encounter.encounterId == requestParameters.encounterId);
    encounters.splice(index, 1);
    return mock(undefined);
  }

  getEncounter(requestParameters: GetEncounterRequest, initOverrides?: RequestInit): Promise<Encounter> {
    const encounter = encounters.find((e) => e.encounterId == requestParameters.encounterId);
    if (!encounter) {
      return reject();
    }
    return mock(encounter);
  }

  listEncounters(requestParameters: ListEncountersRequest, initOverrides?: RequestInit): Promise<EncounterCollection> {
    return mock({
      items: encounters.map((encounter) => {
        return {
          ...encounter
        } as EncounterSummary;
      })
    });
  }

  updateEncounter(requestParameters: UpdateEncounterRequest, initOverrides?: RequestInit): Promise<Encounter> {
    return mock(encounter);
  }
}
