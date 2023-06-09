/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { FeedbackList } from 'components/feedback';
import { FeedbackSummary } from '@uho/provider-api-client/dist/api-client';
import { useQuery } from '@tanstack/react-query';
import { providerApi } from 'api';
import { useI18n } from 'hooks/useI18n';
import { Foldout } from 'components/layouts/foldout/card';

type Props = Readonly<{
  loading: boolean;
  providerId: number;
}>;

export function FeedbackFoldout({ loading, providerId }: Props) {
  const i18n = useI18n().patient.provider.feedback;
  const { data, error, isError, isLoading } = useQuery<FeedbackSummary[], Error>(
    ['feedback'],
    async () => (await providerApi.listFeedbacks({ providerId })).items || []
  );

  let content;
  if (isError) {
    content = <div>Something went wrong!! {error?.message}</div>;
  } else if (isLoading) {
    content = <div>Loading...</div>;
  } else if (!data) {
    content = <div>Nothing found.</div>;
  } else {
    const feedback = data || [];
    content = <FeedbackList feedback={feedback} />;
  }

  return <Foldout title={i18n.title()}>{content}</Foldout>;
}
