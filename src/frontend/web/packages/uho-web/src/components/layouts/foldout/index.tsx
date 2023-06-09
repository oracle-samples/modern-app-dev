/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojconveyorbelt';
import './styles.scss';
import { ComponentChildren, h } from 'preact';

type Props = {
  content: ComponentChildren[];
};

export function FoldoutLayout({ content }: Props) {
  const [profile, ...rest] = content;
  return (
    <div class="foldout-layout fullscreen oj-flex oj-sm-flex-wrap-nowrap">
      <div class="foldout-profile oj-flex-item oj-sm-flex-0">{profile}</div>
      <oj-conveyor-belt arrowVisibility="hidden" class="conveyor-belt-container oj-flex-item">
        {rest}
      </oj-conveyor-belt>
    </div>
  );
}
