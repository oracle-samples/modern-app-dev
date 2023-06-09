/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { h } from 'preact';
import { ProviderProfile } from 'components/provider-profile';
import 'ojs/ojbutton';
import { useCallback, useMemo } from 'preact/hooks';
import { useNavigate } from 'react-router-dom';
import { ProfileFoldout } from 'components/layouts/foldout/profile';

type Props = Readonly<{
  loading: boolean;
  provider?: Provider;
}>;

export function Profile({ provider, loading }: Props) {
  const navigate = useNavigate();
  const goBack = useCallback(() => {
    navigate('..', { relative: 'path' });
  }, [navigate]);

  const previous = useCallback(() => {
    if (!provider?.id) {
      return;
    }
    navigate(`../${provider?.id - 1}`, { relative: 'path' });
  }, [navigate, provider]);

  const next = useCallback(() => {
    if (!provider?.id) {
      return;
    }
    navigate(`../${provider?.id + 1}`, { relative: 'path' });
  }, [navigate, provider]);

  return (
    <ProfileFoldout
      goPrevious={provider?.id! > 1 && previous}
      goNext={next}
      goBack={goBack}
      content={<ProviderProfile displayBadge={false} loading={loading} provider={provider} />}
    />
  );
}
