/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { Dashboard } from 'components/layouts/dashboard';
import { useMemo } from 'preact/hooks';
import { useNavigate } from 'react-router';
import { providers } from 'api/mocks/providerApi';
import { ProviderDropdown } from 'components/provider-list/dropdown';
import { useI18n } from 'hooks/useI18n';
import useTitle from 'hooks/useTitle';
import { GeneralOverviewLayout } from 'components/layouts/general-overview';
import { ProviderProfile } from 'components/provider-profile';
import { DashboardCard } from 'components/layouts/dashboard/card';
import { PatientFooter } from '../patientFooter';
import { UserMenu } from 'components/user-menu';

export function Overview() {
  const { patient } = useI18n();
  const { overview, title } = patient;
  useTitle(title());
  const navigate = useNavigate();

  const cards: DashboardCard[] = useMemo(() => {
    return [
      {
        title: overview.scheduleAppointmentTitle(),
        description: overview.scheduleAppointmentDescription(),
        icon: 'oj-ux-ico-calendar-clock',
        background: 'green',
        onOjAction: () => navigate('/patient/appointments')
      },
      {
        title: overview.pastVisitsTitle(),
        description: overview.pastVisitsDescription(),
        icon: 'oj-ux-ico-clock-history',
        background: 'purple',
        onOjAction: () => navigate('/patient/encounters')
      },
      {
        title: overview.covidTitle(),
        description: overview.covidDescription(),
        icon: 'oj-ux-ico-vaccine',
        background: 'orange',
        onOjAction: () => window.open('https://covid19.ca.gov/')
      },
      {
        title: overview.healthcareTitle(),
        description: overview.healthcareDescription(),
        icon: 'oj-ux-ico-doctor',
        background: 'blue',
        onOjAction: () => navigate('/patient/providers')
      }
    ] as DashboardCard[];
  }, [navigate, overview]);

  return (
    <GeneralOverviewLayout
      direction="ltr"
      title={overview.title()}
      subtitle={overview.description()}
      actions={<UserMenu />}
      primary={
        <div class="oj-flex oj-sm-flex-direction-column">
          <div class="oj-flex-item">
            <span class="oj-typography-subheading-sm">Search for a Provider</span>
          </div>
          <div class="oj-flex-item oj-sm-margin-12x-bottom">
            <div class="oj-sm-margin-4x-top landing-search">
              <ProviderDropdown
                onvalueChanged={(provider) => {
                  navigate(`/patient/providers/${provider}`);
                }}
              />
            </div>
          </div>
          <div class="oj-flex-item">
            <Dashboard cards={cards} />
          </div>
        </div>
      }
      secondary={
        <div>
          <span class="oj-typography-subheading-sm">Your Primary Care Physician</span>
          <ProviderProfile displayBadge={false} provider={providers[0]} />
        </div>
      }
      footer={<PatientFooter />}
    />
  );
}
