/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 */
import { UserRole } from '../../utils/authProvider';
import { Apm, Configuration, FrontendApi, User } from '../frontendApi';
import { mock } from './utils';

/**
 * Mocked Frontend API for local development
 */
export class MockFrontendApi extends FrontendApi {
  public constructor(configuration: Configuration) {
    super(configuration);
  }

  public getUserInformation(): Promise<User> {
    const userString = localStorage.getItem('user');
    if (userString) {
      return Promise.resolve(JSON.parse(userString));
    }
    return Promise.reject();
  }

  public getApmInformation(): Promise<Apm> {
    return mock({
      publicDataKey: 'publicDataKey',
      ociDataUploadEndpoint: 'ociDataUploadEndpoint',
      serviceName: 'serviceName',
      webApplication: 'webApplication'
    });
  }

  public async login(role: UserRole): Promise<void> {
    const user = {
      role,
      name: role === 'PATIENT' ? 'Patient XYZ' : 'Provider XYZ',
      username: role === 'PATIENT' ? 'DevUserId' : 'jsmith',
      email: role === 'PATIENT' ? 'patient@oracle.com' : 'provider@oracle.com'
    };
    localStorage.setItem('user', JSON.stringify(user));
    window.location.reload();
  }

  public signout() {
    window.location.href = '/home';
  }
}
