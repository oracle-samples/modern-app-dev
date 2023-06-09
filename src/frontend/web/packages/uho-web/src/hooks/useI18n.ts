/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useContext } from 'preact/hooks';
import { AppBundle } from '../nls';
import { EnvironmentContext } from '@oracle/oraclejet-preact/UNSAFE_Environment';

export function useI18n() {
  const { translations } = useContext(EnvironmentContext);
  return translations?.app as unknown as AppBundle;
}
