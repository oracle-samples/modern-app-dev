/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { ComponentChildren, h } from 'preact';

export function ContentPanel({ children, clazz }: { children: ComponentChildren; clazz?: string }) {
  return (
    <div class={`${clazz} oj-panel oj-panel-shadow-sm oj-flex oj-flex-1 oj-panel-shadow-sm content-panel`}>
      {children}
    </div>
  );
}
