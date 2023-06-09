/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import './styles.scss';
import { Avatar, CAvatarElement } from 'oj-c/avatar';
import { ActionCardElement } from 'ojs/ojactioncard';
import { h } from 'preact';
import { useCallback } from 'preact/hooks';
import { UserRole } from '../../../utils/authProvider';

interface Card {
  type: UserRole;
  title: string;
  iconClass: string;
  background?: CAvatarElement['background'];
  src?: string;
  onSelection: (selection: UserRole) => void;
}

export function RenderCard({ title, src, iconClass, background, type, onSelection }: Card) {
  const onAction = useCallback(
    (event: ActionCardElement.ojAction) => {
      onSelection(type);
    },
    [type, onSelection]
  );
  return (
    <div class="oj-flex-item oj-sm-margin-10x">
      <oj-action-card class="action-card" onojAction={onAction}>
        <div class="oj-sm-padding-4x">
          <div class="oj-flex oj-sm-flex-direction-column oj-sm-flex-justify-content-center">
            <Avatar
              class="card-avatar oj-flex-item oj-sm-margin-8x-horizontal oj-sm-margin-4x-bottom"
              role="img"
              size="2xl"
              iconClass={iconClass}
              aria-label={title}
              src={src}
              background={background}
            />
            <span class="card-title oj-text-primary-color oj-typography-subheading-xs oj-flex-item">{title}</span>
          </div>
        </div>
      </oj-action-card>
    </div>
  );
}
