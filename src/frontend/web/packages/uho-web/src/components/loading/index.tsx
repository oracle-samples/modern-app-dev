/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';
import { Heart } from '../spinner/heart';

export function Loading() {
  return (
    <div class="loading-container">
      <div class="wrapper">
        <Heart />
      </div>
    </div>
  );
}
