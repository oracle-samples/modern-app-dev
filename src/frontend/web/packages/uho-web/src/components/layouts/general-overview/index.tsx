/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ComponentChildren, Fragment, h } from 'preact';
import './styles.scss';
import { Banner } from 'components/banner';
import { useMemo } from 'preact/hooks';

type Props = Readonly<{
  primary: ComponentChildren;
  secondary: ComponentChildren;
  title: string;
  subtitle: string;
  footer?: ComponentChildren;
  direction?: 'ltr' | 'rtl';
  actions?: ComponentChildren;
}>;

export function GeneralOverviewLayout({ primary, secondary, title, subtitle, footer, direction, actions }: Props) {
  const content = useMemo(() => {
    const primaryContent = <div class="oj-flex-item primary oj-sm-12 oj-md-9 oj-lg-9 oj-sm-padding-8x">{primary}</div>;
    const secondaryContent = (
      <div class="oj-flex-item oj-sm-only-hide secondary oj-sm-3 oj-sm-padding-8x">{secondary}</div>
    );
    if (direction === 'ltr') {
      return [primaryContent, secondaryContent];
    }
    return [secondaryContent, primaryContent];
  }, [direction, primary, secondary]);

  return (
    <div class="overview-layout oj-sm-padding-12x-horizontal oj-flex oj-sm-flex-direction-column fullscreen">
      <div class="oj-flex-item oj-sm-padding-6x-top oj-sm-padding-4x-bottom oj-sm-flex-0">
        <div class="oj-flex-bar">
          <div class="oj-flex-bar-start oj-flex oj-sm-flex-direction-column">
            <div class="oj-typography-subheading-lg oj-flex-item header-title-text-color">{title}</div>
            <div class="oj-typography-semi-bold-sm oj-flex-item header-subtitle-text-color">{subtitle}</div>
          </div>
          <div class="oj-flex-bar-end">{actions}</div>
        </div>
      </div>
      <div class="oj-flex-item oj-sm-flex-0">
        <Banner />
      </div>
      <div class="oj-flex-item oj-flex oj-sm-flex-1">{content}</div>
      <div class="oj-flex-item oj-sm-flex-0">{footer}</div>
    </div>
  );
}
