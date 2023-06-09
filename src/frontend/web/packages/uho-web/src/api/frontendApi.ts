/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { Patient } from '@uho/patient-api-client/dist/api-client';
import { Provider } from '@uho/provider-api-client/dist/api-client';
import { UserRole } from '../utils/authProvider';

export type Apm = {
  serviceName: string;
  webApplication: string;
  ociDataUploadEndpoint: string;
  publicDataKey: string;
};

export type User = (Patient | Provider) & { role: UserRole };

export class Configuration {
  basePath: string;
  constructor(config: { basePath: string }) {
    this.basePath = config.basePath;
  }
}

export class FrontendApi {
  public constructor(public configuration: Configuration) {}

  public getUserInformation(): Promise<User> {
    return fetch(`${this.configuration.basePath}/userInformation`, {
      method: 'GET',
      headers: {
        accept: '*/*'
      }
    })
      .then((result) => result.json())
      .then((users) => users[0]);
  }

  public getApmInformation(): Promise<Apm> {
    return fetch(`${this.configuration.basePath}/apmInformation`, {
      method: 'GET',
      headers: {
        accept: '*/*'
      }
    }).then((result) => result.json());
  }

  public login(role: UserRole) {
    window.location.href = `oauth/login/${role.toLowerCase()}`;
  }

  public signout() {
    window.location.href = '/home/oauth/logout/';
  }
}
