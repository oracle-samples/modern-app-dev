/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { NotFound } from '.';
import { h } from 'preact';
import { snapshot } from '../../../test/jetHelper';

jest.mock('react-router-dom', () => ({
  useNavigate: jest.fn()
}));

snapshot('404', <NotFound />);
