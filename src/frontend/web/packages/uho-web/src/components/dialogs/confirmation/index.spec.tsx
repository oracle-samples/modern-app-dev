/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Confirmation } from '.';
import { h } from 'preact';
import { snapshot } from '../../../../test/jetHelper';

snapshot(
  'Confirmation',
  <Confirmation
    dialogTitle="Dialog Title"
    onClose={() => /* closed */ undefined}
    show={true}
    footer={<div>Footer</div>}
  >
    <div>Body</div>
  </Confirmation>,
  1000
);
