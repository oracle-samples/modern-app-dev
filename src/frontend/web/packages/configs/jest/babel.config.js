/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
/* eslint-disable @typescript-eslint/no-var-requires */
const { default: babelJest } = require('babel-jest');

module.exports = babelJest.createTransformer({
  extends: '@uho/babel-config'
});
