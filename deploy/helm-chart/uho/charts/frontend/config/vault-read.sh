#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

set -e
resp=`oci --auth instance_principal secrets secret-bundle get-secret-bundle-by-name --secret-name idcs --vault-id $VAULT_OCID  | jq  --raw-output '.data."secret-bundle-content".content' | base64 -d`

echo resp=$resp

PATIENT_APP_CLIENT_ID=`echo $resp | jq --raw-output '."patient-app-client-id"'`
PATIENT_APP_CLIENT_SECRET=`echo $resp | jq --raw-output '."patient-app-client-secret"'`
PROVIDER_APP_CLIENT_ID=`echo $resp | jq --raw-output '."provider-app-client-id"'`
PROVIDER_APP_CLIENT_SECRET=`echo $resp | jq --raw-output '."provider-app-client-secret"'`
SERVICE_APP_CLIENT_ID=`echo $resp | jq --raw-output '."service-app-client-id"'`
SERVICE_APP_CLIENT_SECRET=`echo $resp | jq --raw-output '."service-app-client-secret"'`

echo -n $PATIENT_APP_CLIENT_ID > /idcs/patient-app-client-id
echo -n $PATIENT_APP_CLIENT_SECRET > /idcs/patient-app-client-secret
echo -n $PROVIDER_APP_CLIENT_ID > /idcs/provider-app-client-id
echo -n $PROVIDER_APP_CLIENT_SECRET > /idcs/provider-app-client-secret
echo -n $SERVICE_APP_CLIENT_ID > /idcs/service-app-client-id
echo -n $SERVICE_APP_CLIENT_SECRET > /idcs/service-app-client-secret
