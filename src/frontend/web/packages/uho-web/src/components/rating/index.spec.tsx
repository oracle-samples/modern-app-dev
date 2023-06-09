/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Rating } from './index';
import { h } from 'preact';
import { snapshot } from '../../../test/jetHelper';

snapshot('Rating', <Rating negative={{ count: 10, text: 'Negative' }} positive={{ count: 5, text: 'Positive' }} />);
