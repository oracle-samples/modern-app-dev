/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { h } from 'preact';
import { UserRole, useAuth } from '../../utils/authProvider';
import { ContentPanel } from 'components/layouts/content-panel';
import { Navigate } from 'react-router';
import { RenderCard } from './card';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';

type Props = Readonly<{
  onSelection: (selection: UserRole) => void;
}>;

export function Landing({ onSelection }: Props) {
  const { landing } = useI18n();
  const { user } = useAuth();
  useTitle(landing.title());

  if (user) {
    if (user.role === 'PATIENT') {
      return <Navigate to="/patient" />;
    }
    if (user.role === 'PROVIDER') {
      return <Navigate to="/provider" />;
    }
  }
  return (
    <div class="landing-page">
      <div class="oj-web-applayout-max-width oj-flex oj-sm-margin-12x-top oj-sm-justify-content-center">
        <div class="oj-flex-item oj-sm-10 oj-md-10 oj-lg-10 oj-sm-margin-12x-top">
          <ContentPanel>
            <div class="oj-flex">
              <div class="oj-flex-item oj-sm-margin-6x">
                <div class="oj-typography-heading-lg">{landing.header()}</div>
                <div class="oj-typography-body-lg">{landing.subheader()}</div>
              </div>
            </div>
            <div class="oj-flex oj-sm-flex-items-initial oj-sm-justify-content-center oj-sm-align-items-center oj-xl-margin-12x">
              <RenderCard
                iconClass="oj-ux-ico-patient"
                title={landing.loginPatientTitle()}
                type="PATIENT"
                background="teal"
                onSelection={onSelection}
              />
              <RenderCard
                iconClass="oj-ux-ico-user-md"
                title={landing.loginProviderTitle()}
                type="PROVIDER"
                background="pink"
                onSelection={onSelection}
              />
            </div>
          </ContentPanel>
        </div>
      </div>
    </div>
  );
}
