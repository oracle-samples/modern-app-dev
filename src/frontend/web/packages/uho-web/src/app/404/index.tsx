/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';
import { Button } from 'oj-c/button';
import { useNavigate } from 'react-router-dom';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';

export function NotFound() {
  const navigate = useNavigate();
  const { notFound } = useI18n();
  useTitle(notFound.header());

  return (
    <div class="not-found-wrapper">
      <div class="content">
        <div class="oj-flex oj-sm-flex-direction-column">
          <div class="oj-flex-item">
            <span class="animation oj-text-primary-color">{notFound.header()}</span>
          </div>
          <div class="oj-flex-item">
            <Button
              class="oj-button-lg"
              label={notFound.action()}
              chroming="callToAction"
              onOjAction={() => navigate('/')}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
