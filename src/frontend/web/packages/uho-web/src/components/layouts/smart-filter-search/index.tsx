/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ComponentChildren, h } from 'preact';
import './styles.scss';
import { Banner } from 'components/banner';
import { Filter, Search } from 'components/search';

type Props = Readonly<{
  title: string;
  subtitle: string;
  placeholder: string;
  content: ComponentChildren;
  footer: ComponentChildren;
  filters: Filter[];
  onFiltersUpdated: (filters: Filter[]) => void;
  onSearchUpdated: (search: string) => void;
}>;

export function SmartFilterSearch({
  title,
  subtitle,
  placeholder,
  filters,
  onFiltersUpdated,
  onSearchUpdated,
  content,
  footer
}: Props) {
  return (
    <div class="smart-filter-search oj-sm-margin-12x-horizontal">
      <div class="oj-sm-padding-2x-top oj-flex oj-sm-flex-direction-column" style={{ height: '100%' }}>
        <div class="oj-flex-item header oj-sm-padding-4x-top oj-sm-padding-4x-bottom oj-sm-padding-8x-horizontal oj-sm-flex-0">
          <div>
            <div class="oj-typography-subheading-lg header-title-text-color">{title}</div>
            <div class="oj-typography-semi-bold-sm header-subtitle-text-color">{subtitle}</div>
            <div class="oj-sm-margin-4x-top">
              <Search
                filters={filters}
                placeholder={placeholder}
                onFiltersUpdated={onFiltersUpdated}
                onSearchUpdated={onSearchUpdated}
              />
            </div>
          </div>
        </div>
        <div class="oj-flex-item oj-sm-flex-0">
          <Banner />
        </div>
        <div class="content oj-flex-item oj-sm-padding-8x-horizontal oj-sm-padding-8x-top oj-sm-flex-1">{content}</div>
        <div class="footer oj-flex-item oj-sm-flex-0">{footer}</div>
      </div>
    </div>
  );
}
