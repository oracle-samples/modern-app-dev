/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Configuration, LoaderOptionsPlugin } from 'webpack';
import TerserPlugin from 'terser-webpack-plugin';
import CompressionPlugin from 'compression-webpack-plugin';
import { resolve } from 'path';
import {
  htmlWebpack,
  tsLoader,
  htmlLoader,
  fileLoader,
  cssLoader,
  buildConfig,
  fontLoader,
  rootDirectory,
  locales,
  scssLoader
} from './webpack.config.base';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';

function config(locale: string): Configuration {
  return {
    entry: {
      main: resolve(rootDirectory, `src/index.tsx`)
    },
    mode: 'production',
    stats: 'errors-only',
    output: {
      path: resolve(rootDirectory, 'build', 'prod', locale),
      filename: 'app/[name].[contenthash].bundle.js',
      chunkFilename: 'app/[id].[chunkhash].chunk.js',
      publicPath: '/home/'
    },
    optimization: {
      minimizer: [new TerserPlugin({ extractComments: false })],
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendors: {
            test: /[\\/]node_modules[\\/]/,
            name: 'vendors',
            minSize: 0
          }
        }
      }
    },
    module: {
      rules: [
        tsLoader(resolve(rootDirectory, 'build', 'prod', locale, 'dtDir')),
        scssLoader([
          MiniCssExtractPlugin.loader,
          'css-loader', // translates CSS into CommonJS
          'sass-loader' // compiles Sass to CSS
        ]),
        cssLoader(true, 'production'),
        htmlLoader(true),
        fileLoader('app/assets/[contenthash][ext][query]'),
        fontLoader()
      ]
    },
    plugins: [
      htmlWebpack('prod', locale),
      new CompressionPlugin({
        test: /\.tsx?$|\.jsx?$|\.css$|\.html$/,
        exclude: /\.d\.ts$/
      }),
      new LoaderOptionsPlugin({
        test: /\.html$/,
        optimization: {
          occurrenceOrder: true
        }
      })
    ]
  };
}

async function build() {
  return locales.map((locale) => buildConfig(config(locale), locale));
}

export default build();
