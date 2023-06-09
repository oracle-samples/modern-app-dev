/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ComponentChildren, h } from 'preact';
import 'ojs/ojbutton';
import './styles.scss';

type Props = Readonly<{
  goBack?: (() => void) | false;
  goNext?: (() => void) | false;
  goPrevious?: (() => void) | false;
  content: ComponentChildren;
}>;

export function ProfileFoldout({ goBack, goNext, goPrevious, content }: Props) {
  return (
    <div class="foldout-profile oj-color-invert">
      <div class="oj-flex-bar oj-sm-padding-4x-horizontal oj-sm-padding-2x-vertical">
        <div class="oj-flex-bar-start">
          {goBack && (
            <oj-button display="icons" onojAction={goBack} chroming="borderless">
              <span class="oj-ux-ico-arrow-up-alt" />
            </oj-button>
          )}
        </div>
        <div class="oj-flex-bar-end">
          {goPrevious && (
            <oj-button display="icons" onojAction={goPrevious} chroming="borderless">
              <span class="oj-ux-ico-arrow-left-alt" />
            </oj-button>
          )}
          {goNext && (
            <oj-button display="icons" onojAction={goNext} chroming="borderless">
              <span class="oj-ux-ico-arrow-right-alt" />
            </oj-button>
          )}
        </div>
      </div>
      <div class="oj-flex oj-sm-justify-content-center oj-sm-padding-4x-start">
        <div class="oj-sm-padding-10x-horizontal">{content}</div>
      </div>
    </div>
  );
}
