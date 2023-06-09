/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { About } from './foldouts/about';
import { Experience } from './foldouts/experience';
import { FeedbackFoldout } from './foldouts/feedback';
import { Profile } from './foldouts/profile';
import { useQuery } from '@tanstack/react-query';
import { providerApi } from 'api';
import { useParams } from 'react-router-dom';
import { FoldoutLayout } from 'components/layouts/foldout';

type Route = { providerId: string };

export function ProviderDetails() {
  const { providerId } = useParams<Route>();
  const { data, error, isError, isLoading } = useQuery(['providers', providerId], ({ queryKey }) => {
    return providerApi.getProvider({ providerId: Number(queryKey[1]) });
  });
  if (isError) {
    return <div>Cannot read provider (Error: {JSON.stringify(error)}</div>;
  }

  return (
    <FoldoutLayout
      content={[
        <Profile key="provider" loading={isLoading} provider={data} />,
        <About key="about" loading={isLoading} provider={data} providerId={Number(providerId)} />,
        <Experience key="experience" loading={isLoading} provider={data} />,
        <FeedbackFoldout key="feedback" loading={isLoading} providerId={Number(providerId)} />
      ]}
    />
  );
}
