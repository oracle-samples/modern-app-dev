/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import 'ojs/ojpictochart';
import { ItemContext } from 'ojs/ojcommontypes';
import { useArrayDataProvider } from 'hooks/useDataProvider';

function getColor(id: number) {
  return id === 1 ? '#ed6647' : '';
}

type Props = Readonly<{
  negative: {
    count: number;
    text: string;
  };
  positive: {
    count: number;
    text: string;
  };
}>;

interface RatingEntry {
  id: number;
  name: string;
  count: number;
}

export function Rating({ positive, negative }: Props) {
  const elements: RatingEntry[] = [
    {
      id: 1,
      name: positive.text,
      count: positive.count
    },
    {
      id: 0,
      name: negative.text,
      count: negative.count
    }
  ];

  const ratingElementsDataProvider = useArrayDataProvider(elements, 'id');
  const renderChartItem = useCallback((item: ItemContext<string, RatingEntry>) => {
    return (
      <oj-picto-chart-item name={item.data.name} shape="star" count={item.data.count} color={getColor(item.data.id)} />
    );
  }, []);

  return (
    <oj-picto-chart data={ratingElementsDataProvider} columnCount={5}>
      <template slot="itemTemplate" render={renderChartItem} />
    </oj-picto-chart>
  );
}
