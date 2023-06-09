/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import 'ojs/ojactioncard';
import { Avatar } from 'oj-c/avatar';
import { h } from 'preact';
import { ComponentProps } from 'preact/compat';

type AvatarProps = ComponentProps<typeof Avatar>;

export interface DashboardCard {
  title: string;
  description: string;
  icon: string;
  background: AvatarProps['background'];
  onOjAction?: () => void;
}

export function Card({ title, description, icon, background, onOjAction }: DashboardCard) {
  return (
    <oj-action-card onojAction={onOjAction} class="dashboard-card">
      <div class="card-content oj-flex oj-sm-padding-2x oj-sm-flex-direction-row">
        <div class="oj-flex-item oj-sm-flex-0 oj-sm-padding-1x">
          <Avatar background={background} iconClass={icon} size="lg" />
        </div>
        <div class="oj-flex-item oj-flex oj-sm-flex-1 oj-sm-padding-2x oj-sm-flex-direction-column oj-sm-flex-items-initial">
          <div class="oj-flex-item oj-text-color-primary oj-typography-body-lg">{title}</div>
          <div class="oj-flex-item oj-text-color-secondary oj-typography-body-md">{description}</div>
        </div>
      </div>
    </oj-action-card>
  );
}
