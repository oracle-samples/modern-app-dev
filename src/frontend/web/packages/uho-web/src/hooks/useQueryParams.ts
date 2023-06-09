/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { useLocation } from 'react-router-dom';

export function useQueryParams() {
  return new URLSearchParams(useLocation().search);
}
