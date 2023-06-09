/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';

type Props = Readonly<{
  title: string;
  description?: string;
}>;

export function Empty({ title, description }: Props) {
  return (
    <div class="oj-flex oj-sm-flex-direction-column oj-sm-justify-content-flex-end empty-screen">
      <span class="oj-text-primary-color title oj-typography-heading-2xl oj-flex-item oj-sm-flex-0">{title}</span>
      {description && (
        <span class="oj-text-primary-color description oj-typography-body-xl oj-flex-item oj-sm-flex-0">
          {description}
        </span>
      )}
    </div>
  );
}
