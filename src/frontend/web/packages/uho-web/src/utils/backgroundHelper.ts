/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { ComponentProps } from 'preact/compat';
import { Avatar } from 'oj-c/avatar';

type AvatarProps = ComponentProps<typeof Avatar>;
const backgrounds: AvatarProps['background'][] = [
  'neutral',
  'orange',
  'green',
  'teal',
  'blue',
  'slate',
  'pink',
  'purple',
  'lilac',
  'gray'
];

// simple hash function to generate a number from 0 - brackets
function computeHash(key: string, brackets: number) {
  const hash = key.length + key.charCodeAt(0);
  return hash % brackets || 0;
}

export function getBackgroundByName(name: string) {
  return backgrounds[computeHash(name, backgrounds.length)];
}
