/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { ComponentChildren, h } from 'preact';
import { Divider } from 'components/divider';

type Props = Readonly<{
  children: ComponentChildren;
  title: string;
}>;

export function Foldout({ title, children }: Props) {
  return (
    <div class="foldout-card">
      <div class="oj-sm-padding-12x">
        <div class="oj-sm-padding-6x-bottom">
          <span class="oj-text-color-primary oj-typography-bold oj-typography-subheading-md">{title}</span>
          <Divider />
        </div>
        {children}
      </div>
    </div>
  );
}
