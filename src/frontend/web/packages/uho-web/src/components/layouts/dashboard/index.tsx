/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojactioncard';
import { h } from 'preact';
import { Card, DashboardCard } from './card';
import { useI18n } from 'hooks/useI18n';

type Props = Readonly<{
  cards: DashboardCard[];
}>;

export function Dashboard(props: Props) {
  const i18n = useI18n().dashboard;
  return (
    <div>
      <div class="oj-typography-subheading-sm">{i18n.title()}</div>
      <div class="oj-flex oj-sm-padding-4x-top">
        {props.cards.map((c, i) => {
          return (
            <div key={i} class="oj-sm-12 oj-md-6 oj-flex-item oj-sm-padding-1x">
              <Card {...c} />
            </div>
          );
        })}
      </div>
    </div>
  );
}
