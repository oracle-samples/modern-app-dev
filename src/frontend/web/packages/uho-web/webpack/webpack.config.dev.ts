/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { resolve } from 'path';
import { NormalModuleReplacementPlugin } from 'webpack';
import { WebpackConfiguration } from 'webpack-dev-server';
import {
  htmlWebpack,
  cssLoader,
  tsLoader,
  htmlLoader,
  fileLoader,
  fontLoader,
  buildConfig,
  rootDirectory,
  locales,
  scssLoader
} from './webpack.config.base';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const PreactRefreshPlugin = require('@prefresh/webpack');

function config(locale: string, directory: string): WebpackConfiguration {
  return {
    entry: {
      main: resolve(rootDirectory, `src/index.tsx`)
    },
    name: locale,
    mode: 'development',
    output: {
      filename: `app/${locale}-[name].[contenthash].bundle.js`,
      chunkFilename: 'app/[name].[contenthash].js',
      path: resolve(rootDirectory, 'build', directory, locale),
      publicPath: '/home/'
    },
    devtool: 'eval-source-map',
    module: {
      rules: [
        scssLoader([
          'style-loader', // creates style nodes from JS strings
          {
            loader: 'css-loader', // translates CSS into CommonJS
            options: {
              sourceMap: true
            }
          },
          {
            loader: 'sass-loader', // compiles Sass to CSS
            options: {
              sourceMap: true
            }
          }
        ]),
        cssLoader(false, 'development'),
        tsLoader(resolve(rootDirectory, 'build', directory, locale, 'dtDir')),
        htmlLoader(false),
        fileLoader('app/assets/[contenthash][ext][query]'),
        fontLoader()
      ]
    },
    plugins: [
      new PreactRefreshPlugin(),
      new NormalModuleReplacementPlugin(/\/api\/apiClients.ts/, resolve(rootDirectory, 'src/api/mockApiClients.ts')),
      htmlWebpack('dev', locale)
    ],
    devServer: {
      open: ['/home'],
      historyApiFallback: {
        index: '/home/'
      },
      hot: true
    },
    watchOptions: {
      ignored: ['/node_modules/']
    }
  };
}

async function build() {
  const directory = process.env.DIRECTORY || 'dev';
  return locales.map((locale) => buildConfig(config(locale, directory), locale));
}

export default build();
