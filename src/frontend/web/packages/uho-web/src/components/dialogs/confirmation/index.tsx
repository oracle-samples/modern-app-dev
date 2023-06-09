/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import 'ojs/ojdialog';
import { ojDialog } from 'ojs/ojdialog';
import { error } from 'ojs/ojlogger';
import { getContext } from 'ojs/ojcontext';
import { ComponentChildren, h } from 'preact';
import { useEffect, useRef } from 'preact/hooks';

type Props = Readonly<{
  show: boolean;
  onClose: () => void;
  children?: ComponentChildren;
  footer?: ComponentChildren;
  dialogTitle: string;
}>;

export function Confirmation({ onClose, show, children, footer, dialogTitle }: Props) {
  const dialogRef = useRef<ojDialog>(null);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (dialog) {
      getContext(dialog)
        .getBusyContext()
        .whenReady(2000)
        .then(() => {
          show ? dialog.open() : dialog.close();
        })
        .catch((reason) => {
          error(`BusyContext timed out: ${reason}`);
        });
    }
  }, [show]);

  return (
    <oj-dialog ref={dialogRef} dialogTitle={dialogTitle} cancelBehavior={'icon'} onojClose={onClose}>
      <div slot="body">{children}</div>
      <div slot="footer">{footer}</div>
    </oj-dialog>
  );
}
