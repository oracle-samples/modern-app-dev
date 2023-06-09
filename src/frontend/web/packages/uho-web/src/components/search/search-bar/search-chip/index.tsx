/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Fragment, h, Ref } from 'preact';
import 'ojs/ojinputtext';
import 'ojs/ojswitch';
import 'ojs/ojradioset';
import 'ojs/ojpopup';
import { ojInputTextEventMap } from 'ojs/ojinputtext';
import { ojPopup } from 'ojs/ojpopup';
import './styles.scss';
import { Filter } from '../..';
import { useCallback, useEffect, useMemo, useRef, useState } from 'preact/hooks';
import { getContext } from 'ojs/ojcontext';

type SearchChipProps = {
  filter: Filter;
  onClose: (key: Filter['key']) => void;
  onValueChanged: (key: Filter['key'], value: Filter['value']) => void;
};

export function SearchChip({ filter, onClose, onValueChanged }: SearchChipProps) {
  const [openPopup, setOpenPopup] = useState(false);
  const popupRef: Ref<ojPopup> = useRef(null);
  const valueRef: Ref<HTMLDivElement> = useRef(null);

  const closePopup = useCallback(() => {
    setOpenPopup(false);
  }, [setOpenPopup]);

  const onClickCallback = useCallback(() => {
    setOpenPopup(true);
  }, [setOpenPopup]);

  const onCloseCallback = useCallback(() => {
    onClose(filter.key);
  }, [onClose, filter.key]);

  useEffect(() => {
    const popup = popupRef.current;
    const valueDiv = valueRef.current;
    if (!popup || !valueDiv) {
      return;
    }
    getContext(popup)
      .getBusyContext()
      .whenReady(2000)
      .then(() => {
        openPopup ? popupRef.current?.open(valueRef.current!) : popupRef.current?.close();
      });
  }, [openPopup]);

  const onvalueChanged = useCallback(
    (event: ojInputTextEventMap['valueChanged']) => {
      onValueChanged?.(filter.key, event.detail.value);
    },
    [onValueChanged, filter]
  );

  const popupContent = useMemo(() => {
    switch (filter.type) {
      case 'boolean':
        return <oj-switch labelHint={filter.label} value={filter.value} onvalueChanged={onvalueChanged} />;
      case 'enum':
        return (
          <oj-radioset labelHint={filter.label} value={filter.value} onvalueChanged={onvalueChanged}>
            {filter.values.map((val) => {
              return (
                <oj-option key={val.value} value={val.value}>
                  {val.label}
                </oj-option>
              );
            })}
          </oj-radioset>
        );
      case 'text':
      default:
        return <oj-input-text labelHint={filter.label} value={filter.value} onvalueChanged={onvalueChanged} />;
    }
  }, [filter, onvalueChanged]);

  const value = () => {
    switch (filter.type) {
      case 'boolean':
        return filter.value === true ? 'Yes' : 'No';
      case 'enum':
        return filter.values.find((val) => val.value == filter.value)?.label || filter.value;
      case 'text':
      default:
        return filter.value;
    }
  };

  return (
    <Fragment>
      <div
        class="search-chip oj-flex oj-sm-align-items-center oj-sm-margin-2x-end"
        ref={valueRef}
        onClick={onClickCallback}
      >
        <div class="oj-flex-item search-chip-label oj-sm-flex-wrap-nowrap oj-sm-justify-content-center oj-typography-body-sm label">
          {filter.label}
        </div>
        <div class="oj-flex-item oj-sm-padding-1x-start oj-typography-body-sm oj-typography-semi-bold value">
          {value()}
        </div>
        <div class="oj-sm-margin-2x-start filter-divider" />
        <a class="oj-flex chip-close oj-sm-margin-1x-start" onClick={onCloseCallback}>
          <span class="oj-ux-ico-close search-chip-icon" />
        </a>
      </div>
      <span>
        <oj-popup onojClose={closePopup} ref={popupRef}>
          {popupContent}
        </oj-popup>
      </span>
    </Fragment>
  );
}
