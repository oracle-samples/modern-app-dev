
#  Copyright (c) 2023 Oracle and/or its affiliates.
#  Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
 

#!/bin/bash

set -e

# IDCS_URL
# IDCS_ADMIN_CLIENT_ID
# IDCS_ADMIN_CLIENT_SECRET
# BUCKET_NAME

BEARER_TOKEN=`printf "%s" "$IDCS_ADMIN_CLIENT_ID:$IDCS_ADMIN_CLIENT_SECRET" | base64 | tr -d '\n'`
echo $BEARER_TOKEN

## Obtain access token using the admin app credentials
echo 'Fetching access token...'
ACCESS_TOKEN=`curl -sS -f --location --request POST $IDCS_URL/oauth2/v1/token \
        --header 'Authorization: Basic '$BEARER_TOKEN \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'grant_type=client_credentials' \
        --data-urlencode 'scope=urn:opc:idm:__myscopes__' | jq -r '.access_token'`

# echo $ACCESS_TOKEN

key=`curl -sS -f --request GET $IDCS_URL/admin/v1/SigningCert/jwk \
  --header "Authorization: Bearer $ACCESS_TOKEN" | jq -r '.keys[0]' | jq -r '.key_ops |= ["verify"]'`

echo $key>idcs_public_key

echo 'Uploading idcs key file to object storage'
oci os object put -bn $BUCKET_NAME --file idcs_public_key --force
