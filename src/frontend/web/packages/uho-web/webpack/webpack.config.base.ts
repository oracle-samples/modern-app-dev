/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
/* eslint-disable @typescript-eslint/no-var-requires */
import { Configuration, LoaderOptionsPlugin, ProvidePlugin, RuleSetRule, RuleSetUseItem } from 'webpack';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import * as path from 'path';
import merge from 'webpack-merge';
import { readFileSync } from 'fs';

export const rootDirectory = path.normalize(`${__dirname}/../`);
export const locales = (process.env.LOCALES || 'en-US').split(',');
export const buildVersion = process.env.BUILD_VERSION;

const metadataTransformer = require('@oracle/oraclejet/dist/custom-tsc/metadataTransformer').default;
const decoratorTransformer = require('@oracle/oraclejet/dist/custom-tsc/decoratorTransformer').default;
const oracleJetPathPackageJsonPath = require.resolve('@oracle/oraclejet/package.json');
const jetVersion = JSON.parse(readFileSync(oracleJetPathPackageJsonPath).toString()).version;

export const config = function (locale: string) {
  const webpackConfig: Configuration = {
    output: {
      filename: '[name].[contenthash].bundle.js',
      chunkFilename: '[chunkhash].js',
      publicPath: ''
    },
    resolve: {
      modules: [path.resolve(rootDirectory, 'src'), path.resolve(rootDirectory, 'node_modules')],
      extensions: ['.js', '.ts', '.tsx', '.css'],
      alias: {
        react: 'preact/compat',
        'react-dom': 'preact/compat',
        // oracle jet libraries
        'jqueryui-amd': 'jquery-ui/ui/',
        ojs: '@oracle/oraclejet/dist/js/libs/oj/debug',
        'oj-c': '@oracle/oraclejet-core-pack/oj-c',
        ojdnd: '@oracle/oraclejet/dist/js/libs/dnd-polyfill/dnd-polyfill-1.0.2.min',
        touchr: '@oracle/oraclejet/dist/js/libs/touchr/touchr',
        ojtranslations: '@oracle/oraclejet/dist/js/libs/oj/resources'
      }
    },
    resolveLoader: {
      modules: ['node_modules', path.resolve('node_modules', '@oracle/oraclejet', 'dist', 'webpack-tools', 'loaders')],
      alias: {
        ojL10n: 'ojL10n-loader',
        text: 'raw-loader?esModule=false',
        css: 'raw-loader?esModule=false',
        ojcss: 'raw-loader?esModule=false',
        'ojs/ojcss': 'raw-loader?esModule=false'
      }
    },
    module: {
      rules: [
        {
          sideEffects: true
        }
      ]
    },
    plugins: [
      new MiniCssExtractPlugin({
        filename: 'app/assets/css/[name].css'
      }),
      new ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery'
      }),
      // This plugin sets options for the ojL10n-loader (in this case, just the locale name)
      new LoaderOptionsPlugin({
        options: {
          ojL10nLoader: {
            locale: 'en-US'
          }
        }
      }),
      webpackRequireFixupPlugin()
    ]
  };

  return webpackConfig;
};

const filePaths = [path.resolve(rootDirectory, 'src'), path.resolve(rootDirectory, 'node_modules', '@oracle')];

export const fileLoader = (fileName?: string) => {
  return {
    test: /\.jpe?g|\.gif|\.png|\.ico|\.cur|\.svg/,
    include: filePaths,
    type: 'asset/resource',
    generator: {
      filename: fileName
    }
  };
};

export const fontLoader = () => {
  return {
    test: /\.woff|\.ttf|\.eot/,
    include: filePaths,
    type: 'asset/inline'
  };
};

export const htmlLoader = (prod: boolean) => {
  const minimize = prod ? { removeComments: true } : false;
  return {
    test: /\.html$/,
    use: {
      loader: 'html-loader',
      options: {
        minimize
      }
    },
    exclude: [/index-webpack.html$/, /node_modules/] // index-webpack.html has to be processed in different stage
  };
};

export const tsLoader = (dtDir: string) => {
  const transformerOptions = {
    jetVersion,
    dtDir,
    version: '1.0.0'
  };

  return {
    test: /\.tsx?/,
    loader: 'ts-loader',
    exclude: /node_modules/,
    options: {
      getCustomTransformers: (program: unknown) => ({
        before: [metadataTransformer(program, transformerOptions), decoratorTransformer(program, transformerOptions)]
      })
    }
  };
};

export const scssLoader: (loaders: string | RuleSetUseItem[]) => RuleSetRule = (loaders) => {
  return {
    test: /\.scss$/,
    include: [path.resolve(rootDirectory, 'src')],
    use: loaders
  };
};

const fixUrlMap = {
  './images': '../images',
  '../../css/redwood/images/AI-Sparkle.gif': '../images/AI-Sparkle.gif'
};

export const cssLoader = (extract: boolean, env: 'development' | 'production') => {
  const loaders: RuleSetUseItem[] = [extract ? MiniCssExtractPlugin.loader : 'style-loader', 'css-loader'];
  Object.entries(fixUrlMap).forEach(([key, value]) => {
    loaders.push({
      loader: 'css-fix-url-loader',
      options: {
        from: key,
        to: value,
        env
      }
    });
  });
  return {
    test: /\.css$/i,
    use: loaders
  };
};

export const htmlWebpack = (env: string, locale: string) => {
  return new HtmlWebpackPlugin({
    template: path.resolve(rootDirectory, 'src/index-webpack.html'),
    favicon: path.resolve(rootDirectory, 'src/styles/favicon.ico'),

    minify: 'auto',
    build: {
      env,
      locale: {
        lang: locale,
        dir: ['ar', 'ar-XB', 'he'].indexOf(locale) > -1 ? 'rtl' : 'ltr'
      },
      version: buildVersion,
      date: new Date().toUTCString()
    }
  });
};

// This plugin performs certain fixups to enable the use of Webpack with JET
export function webpackRequireFixupPlugin() {
  const webpackRequireFixupPlugin = require('@oracle/oraclejet/dist/webpack-tools/plugins/WebpackRequireFixupPlugin');
  return new webpackRequireFixupPlugin({
    baseResourceUrl: path.resolve(rootDirectory, 'node_modules/@oracle/oraclejet/dist/js/libs/oj/debug')
  });
}

export function buildConfig(customConfig: Configuration, locale = 'en-US') {
  return merge(config(locale), customConfig);
}
