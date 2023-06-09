/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojprogress-bar';
import { ComponentChildren, h, Fragment } from 'preact';

type Props = Readonly<{
  loading: boolean;
  width?: string;
  height?: string;
  children: ComponentChildren;
}>;

export function LoadingWrapper({
  loading,
  children,
  height = 'var(--oj-typography-body-md-font-size)',
  width = '100px'
}: Props) {
  if (loading) {
    return <div class="oj-animation-skeleton" style={{ height, width }} />;
  }
  return <Fragment>{children}</Fragment>;
}
