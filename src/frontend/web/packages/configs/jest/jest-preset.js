/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
/* eslint-disable @typescript-eslint/no-var-requires */
const path = require('path');
const jestPreset = { ...require('@oracle/oraclejet-jest-preset') };
jestPreset.setupFilesAfterEnv = [path.resolve(__dirname, 'setupTests.ts')];
jestPreset.transform['^.+\\.(mjs|js|jsx|ts|tsx)$'] = path.resolve(__dirname, 'babel.config.js');
jestPreset.moduleNameMapper['oj-c/(.*)'] = '@oracle/oraclejet-core-pack/oj-c/$1';

const ignoredPatterns = ['@oracle/oraclejet', '@oracle/oraclejet-core-pack'];
jestPreset.transformIgnorePatterns = [
  `/node_modules/(?!(${ignoredPatterns.join('|')})/)`,
  '^.+\\.(css|sass|scss|less)$'
];

module.exports = {
  ...jestPreset
};
