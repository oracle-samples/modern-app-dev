#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#
# set -e

store_idcs_creds_from_env () {
    content=`jq -n --arg arg1 $PATIENT_APP_CLIENT_ID --arg arg2  $PATIENT_APP_CLIENT_SECRET --arg arg3 $PROVIDER_APP_CLIENT_ID --arg arg4 $PROVIDER_APP_CLIENT_SECRET '{"patient-app-client-id": $arg1, "patient-app-client-secret":$arg2, "provider-app-client-id":$arg3, "provider-app-client-secret":$arg4}' | base64 -w 0`

    echo content=$content

    resp=`oci --auth instance_principal secrets secret-bundle get-secret-bundle-by-name --secret-name idcs --vault-id $VAULT_OCID`
    echo resp="$resp"
    if [[ `echo $resp | jq 'has("data")'` ]]
    then #update the secret
        secretid=`echo $resp | jq --raw-output '.data."secret-id"'`
        echo "Updating secret id $secretid ..."
        oci --auth instance_principal vault secret update-base64 --secret-id $secretid --secret-content-content $content
    else #create new secret
        echo "Creating new secret..."
        oci --auth instance_principal vault secret create-base64 -c $COMPARTMENT_OCID --secret-name idcs --vault-id $VAULT_OCID --key-id $KEY_OCID --secret-content-content $content
    fi
}

store_idcs_creds_from_file () {

    content=`printf '%s\n' "$(ls /idcs)" | xargs -L 1 -I {} jq -sR --arg key {} '{ ($key): .}' {} | jq -s 'add' | base64 -w 0`
    echo content=$content

    resp=`oci --auth instance_principal secrets secret-bundle get-secret-bundle-by-name --secret-name idcs --vault-id $VAULT_OCID`
    echo resp="$resp"
    if [[ `echo $resp | jq 'has("data")'` ]]
    then #update the secret
        secretid=`echo $resp | jq --raw-output '.data."secret-id"'`
        echo "Updating secret id $secretid ..."
        oci --auth instance_principal vault secret update-base64 --secret-id $secretid --secret-content-content $content
    else #create new secret
        echo "Creating new secret..."
        oci --auth instance_principal vault secret create-base64 -c $COMPARTMENT_OCID --secret-name idcs --vault-id $VAULT_OCID --key-id $KEY_OCID --secret-content-content $content
    fi
}

if [[ $1 == 'env' ]]
then
    store_idcs_creds_from_env
elif [[ $1 == 'file' ]]
then
    store_idcs_creds_from_file
else
    echo "Unsupported parameter $1"
    exit 1
fi
