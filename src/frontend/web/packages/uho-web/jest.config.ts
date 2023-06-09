/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { compilerOptions } from './tsconfig.json';
import { pathsToModuleNameMapper } from 'ts-jest';

const paths: Partial<typeof compilerOptions.paths> = { ...compilerOptions.paths };
delete paths['oj-c/*'];
delete paths['ojs/*'];
delete paths['react-dom'];
delete paths['react'];

const jestConfig = {
  roots: ['<rootDir>'],
  preset: '@uho/jest-preset',
  setupFilesAfterEnv: ['<rootDir>/test/setup.ts'],
  moduleNameMapper: {
    './apiClients': '<rootDir>/src/api/mockApiClients',
    ...pathsToModuleNameMapper(paths)!
  },
  modulePaths: [compilerOptions.baseUrl]
};

export default jestConfig;
