/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
// to guarantee idempotence, we mock the 'now' date
jest.mock('../src/utils/dateProvider', () => ({
  now: () => {
    return new Date(1654733487565);
  }
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn()
}));

// temporary workaround for the list view which sometimes adds an opacity value
expect.addSnapshotSerializer({
  test: (val) => {
    if (val instanceof HTMLElement && val.style && val.closest('oj-list-view')) {
      return !!val.style.opacity;
    }
    return false;
  },
  print: (val, serialize) => {
    const element = val as HTMLElement;
    element.style.removeProperty('opacity');
    if (element.style.length === 0) {
      element.removeAttribute('style');
    }
    return serialize(val);
  }
});

const attributesToRemove = ['id', 'for', 'aria-labelledby', 'aria-controls', 'aria-describedby'];
expect.addSnapshotSerializer({
  test: (val) => {
    if (val instanceof Element) {
      let currentElement: Element | null = val;
      while (currentElement && !currentElement.tagName.toLowerCase().startsWith('oj-')) {
        if (currentElement.parentElement instanceof Element) {
          currentElement = currentElement.parentElement as Element;
        } else {
          currentElement = null;
        }
      }
      if (currentElement === null) {
        return false;
      }
      return !!attributesToRemove.find((a) => val?.hasAttribute(a));
    }
    return false;
  },
  print: (val, serialize) => {
    const element = val as Element;
    attributesToRemove.forEach((attribute) => {
      if (element instanceof Element) {
        element.removeAttribute(attribute);
      }
    });
    return serialize(element);
  }
});
