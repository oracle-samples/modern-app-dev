/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ListItemLayout } from 'oj-c/list-item-layout';
import 'ojs/ojlistview';
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import { ojListView } from 'ojs/ojlistview';
import { useArrayDataProvider } from 'hooks/useDataProvider';
import { Feedback } from '@uho/provider-api-client/dist/api-client';
import { Rating } from '../rating';
import { useI18n } from 'hooks/useI18n';

export function FeedbackList({ feedback }: { feedback: Feedback[] }) {
  const i18n = useI18n().feedback;
  const feedbacksDataProvider = useArrayDataProvider(
    feedback.map((feedback, index) => {
      return { ...feedback, id: index }; // add artifical id for data provider
    }),
    'id'
  );

  const renderListItem = useCallback(
    (item: ojListView.ItemContext<number, Feedback>) => {
      return (
        <li>
          <ListItemLayout>
            <Rating
              positive={{ count: item.data.rating || 0, text: i18n.likeProvider() }}
              negative={{ count: 5 - (item.data.rating || 0), text: i18n.notLikeProvider() }}
            />
            <div slot="secondary" class="oj-typography-body-sm">
              {item.data.text || ''}
            </div>
          </ListItemLayout>
        </li>
      );
    },
    [i18n]
  );

  return (
    <div>
      <oj-list-view data={feedbacksDataProvider}>
        <template slot="itemTemplate" render={renderListItem} />
      </oj-list-view>
    </div>
  );
}
