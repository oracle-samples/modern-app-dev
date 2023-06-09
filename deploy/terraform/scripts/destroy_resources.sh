#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

# Create ssh key pair
ssh-keygen -t rsa -N '' -f id_rsa <<< y
PUBLIC_SSH_KEY_PATH="id_rsa.pub"
PRIVATE_SSH_KEY_PATH="id_rsa"

response=`oci bastion session create-managed-ssh --bastion-id ${BASTION_SERVICE_OCID} --key-type "PUB" --session-ttl 1800 --target-resource-id ${OPERATOR_OCID} --target-os-username opc --display-name DestroyTfSession --ssh-public-key-file ${PUBLIC_SSH_KEY_PATH} --wait-for-state SUCCEEDED --wait-for-state FAILED`

BASTION_SESSION_ID=`echo $response | jq -r '.data.resources[0].identifier'`
echo "Bastion session id ${BASTION_SESSION_ID}"

response=`oci bastion session get --session-id $BASTION_SESSION_ID`

OPERATOR_IP=`echo $response | jq -r '.data."target-resource-details"."target-resource-private-ip-address"'`
echo "Operator ip ${OPERATOR_IP}"

chmod +x scripts/uninstall_helm_releases.sh
ssh -i ${PRIVATE_SSH_KEY_PATH} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -o ProxyCommand="ssh -i ${PRIVATE_SSH_KEY_PATH} -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -W %h:%p -p 22 ${BASTION_SESSION_ID}@host.bastion.${REGION}.oci.oraclecloud.com" -p 22 opc@${OPERATOR_IP} 'bash -s' < scripts/uninstall_helm_releases.sh


rm id_rsa
rm id_rsa.pub

echo "Cleaning up idcs apps and users"
chmod +x scripts/idcs_cleanup.sh && source scripts/idcs_cleanup.sh
