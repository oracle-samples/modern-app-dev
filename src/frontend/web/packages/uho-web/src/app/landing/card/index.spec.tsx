/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { RenderCard } from '.';
import { h } from 'preact';
import { snapshot } from '../../../../test/jetHelper';

snapshot(
  'RenderCard',
  <RenderCard iconClass="oj-ux-ico-patient" title="Title" type="PATIENT" onSelection={console.log} background="teal" />
);
