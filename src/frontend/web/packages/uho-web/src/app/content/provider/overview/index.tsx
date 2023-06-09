/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { Dashboard } from 'components/layouts/dashboard';
import { useQuery } from '@tanstack/react-query';
import { providerApi } from 'api';
import { useAuth } from '../../../../utils/authProvider';
import { useMemo } from 'preact/hooks';
import { useNavigate } from 'react-router-dom';
import { DashboardCard } from 'components/layouts/dashboard/card';
import { useI18n } from 'hooks/useI18n';
import { ProviderFooter } from '../providerFooter';
import { ProviderProfile } from 'components/provider-profile';
import { GeneralOverviewLayout } from 'components/layouts/general-overview';
import { UserMenu } from 'components/user-menu';

export function Overview() {
  const overview = useI18n().provider.overview;
  const navigate = useNavigate();
  const { user } = useAuth();
  const { data, error, isError, isLoading } = useQuery(['provider'], () =>
    providerApi.getProviderByUsername({ username: user!.username! })
  );
  const cards: DashboardCard[] = useMemo(() => {
    return [
      {
        title: overview.appointmentsTitle(),
        description: overview.appointmentsDescription(),
        icon: 'oj-ux-ico-calendar-clock',
        background: 'green',
        onOjAction: () => navigate('/provider/appointments')
      },
      {
        title: overview.encountersTitle(),
        description: overview.encountersDescription(),
        icon: 'oj-ux-ico-clock-history',
        background: 'purple',
        onOjAction: () => navigate('/provider/encounters')
      },
      {
        title: overview.covidTitle(),
        description: overview.covidDescription(),
        icon: 'oj-ux-ico-doctor',
        background: 'orange',
        onOjAction: () => window.open('https://covid19.ca.gov/')
      }
    ] as DashboardCard[];
  }, [overview, navigate]);

  if (isError) {
    return <div>Something went wrong!! {error}</div>;
  }
  if (isLoading) {
    return <div>Loading...</div>;
  }
  if (!data) {
    return <div>Nothing found.</div>;
  }
  return (
    <GeneralOverviewLayout
      direction="ltr"
      title={overview.title()}
      subtitle={overview.description()}
      primary={
        <div class="oj-flex oj-sm-flex-direction-column">
          <div class="oj-flex-item">
            <Dashboard cards={cards} />
          </div>
        </div>
      }
      secondary={
        <div>
          <span class="oj-typography-subheading-sm">Your Profile</span>
          <ProviderProfile displayBadge={false} provider={data} />
        </div>
      }
      actions={<UserMenu />}
      footer={<ProviderFooter />}
    />
  );
}
