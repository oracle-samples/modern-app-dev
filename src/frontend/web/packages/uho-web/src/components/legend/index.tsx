/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojlegend';
import { h } from 'preact';
import { useCallback, useEffect, useRef } from 'preact/hooks';
import { ItemContext } from 'ojs/ojcommontypes';
import Context from 'ojs/ojcontext';
import { ojLegend } from 'ojs/ojlegend';
import { useArrayDataProvider } from 'hooks/useDataProvider';
import { info } from 'ojs/ojlogger';

interface LegendElement {
  color: string;
  text: string;
}

type Props = Readonly<{
  elements: LegendElement[];
  maxWidth?: number;
  maxHeight?: number;
}>;

export function Legend({ elements, maxWidth = 1024, maxHeight = 500 }: Props) {
  const legendRef = useRef<EventTarget>(null);
  const legendElementsDataProvider = useArrayDataProvider(elements, 'text');
  const renderItem = useCallback((item: ItemContext<string, LegendElement>) => {
    return <oj-legend-item lineWidth={10} text={item.data.text} color={item.data.color} />;
  }, []);

  useEffect(() => {
    //  getPreferredSize method on the legend element can be used to find the optimal size for the legend based on its contents
    const legend = legendRef.current as ojLegend<string, Record<string, string>>;
    Context.getContext(legend)
      .getBusyContext()
      .whenReady()
      .then(() => {
        // TODO legend.preferredSize returns 0,0 when it shouldn't. Current workaround is to hardcode the height to 30px
        const dims = legend.getPreferredSize(maxWidth, maxHeight);
        if (dims) {
          legend.style.height = '30px'; // `${dims['height']}px`;
          // legend.style.width = `${dims['width']}px`;
        }
      })
      .catch((reason) => info(`Cannot retrieve busy context: ${reason}`));
  }, [legendElementsDataProvider, legendRef, maxHeight, maxWidth]);

  return (
    <oj-legend
      ref={legendRef}
      symbolHeight={15}
      halign="start"
      valign="bottom"
      symbolWidth={25}
      orientation="horizontal"
      data={legendElementsDataProvider}
    >
      <template slot="itemTemplate" render={renderItem} />
    </oj-legend>
  );
}
