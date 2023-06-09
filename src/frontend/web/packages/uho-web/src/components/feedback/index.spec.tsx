/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { FeedbackList } from '.';
import { h } from 'preact';
import { snapshot } from '../../../test/jetHelper';
import { Feedback } from '@uho/provider-api-client/dist/api-client';

// JEST issue with Ratings component nested in list elements
jest.mock('../rating', () => jest.fn());

const feedback: Feedback[] = [
  {
    id: 1,
    patientId: 1,
    providerId: 2,
    rating: 4,
    text: 'Great doctor'
  }
];

snapshot('FeedbackList', <FeedbackList feedback={feedback} />);
