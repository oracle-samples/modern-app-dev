#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

# IDCS_URL - idcs url
# IDCS_ADMIN_CLIENT_ID - client id of admin app
# IDCS_ADMIN_CLIENT_SECRET - client secret of admin app
# PREFIX - deploy id prefix
echo "idcs url $IDCS_URL"
echo "idcs admin client id $IDCS_ADMIN_CLIENT_ID"
echo "idcs admin client secret $IDCS_ADMIN_CLIENT_SECRET"
echo "idcs prefix $PREFIX"


delete_app_by_name_if_exists() {
    echo "Searching for app with name $1"
    RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Apps \
    -G --data-urlencode "filter=displayName co \"$1\"" \
    -d 'attributes=id' \
    --header "Authorization: Bearer $ACCESS_TOKEN" `

    TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
    echo "Total results $TOTAL_RESULTS"

    if (( $TOTAL_RESULTS > 0 )); then
    echo "Deleting app with name $1";
    APP_ID=`echo $RESP | jq -r '.Resources[0].id'`;

    echo "Deactivating app with app id $APP_ID";
    DEACTIVATE_RESPONSE=`curl -sS -f --location --request PUT $IDCS_URL/admin/v1/AppStatusChanger/$APP_ID \
    --header 'Content-Type: application/json'\
    --header "Authorization: Bearer $ACCESS_TOKEN"\
    --data-binary '{
    "active": false,
    "schemas": [
        "urn:ietf:params:scim:schemas:oracle:idcs:AppStatusChanger"
    ]
    }'`;
    echo "Deactivate response $DEACTIVATE_RESPONSE";

    echo "Deleting app with app id $APP_ID"
    DELETE_RESPONSE=`curl -sS -f --request DELETE $IDCS_URL/admin/v1/Apps/$APP_ID \
    --header "Authorization: Bearer $ACCESS_TOKEN"`
    echo "Delete response $DELETE_RESPONSE";
    fi;
}

delete_user_by_name_if_exists() {
    echo "Searching patient with name $1"
    RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Users \
    -G --data-urlencode "filter=userName eq \"$1\"" \
    -d 'attributes=id' \
    --header "Authorization: Bearer $ACCESS_TOKEN" `

    TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
    echo "Total results $TOTAL_RESULTS"

    if (( $TOTAL_RESULTS > 0 )); then
        USER_ID=`echo $RESP | jq -r '.Resources[0].id'`;

        echo "Deleting user with user id $USER_ID"
        DELETE_RESPONSE=`curl -sS -f --request DELETE $IDCS_URL/admin/v1/Users/$USER_ID \
        --header "Authorization: Bearer $ACCESS_TOKEN"`
        echo "Delete response $DELETE_RESPONSE";
    fi;
}

BEARER_TOKEN=`printf "%s" "$IDCS_ADMIN_CLIENT_ID:$IDCS_ADMIN_CLIENT_SECRET" | base64 | tr -d '\n'`
echo "Bearer token $BEARER_TOKEN"

## Obtain access token using the admin app credentials
echo 'Fetching access token...'
ACCESS_TOKEN=`curl -sS -f --location --request POST $IDCS_URL/oauth2/v1/token \
        --header 'Authorization: Basic '$BEARER_TOKEN \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'grant_type=client_credentials' \
        --data-urlencode 'scope=urn:opc:idm:__myscopes__' | jq -r '.access_token'`
    
echo "Access token $ACCESS_TOKEN"


APP_NAMES=("$PREFIX Patient Client Application" "$PREFIX Provider Client Application" "$PREFIX Service Client Application" "$PREFIX Resource Server for UHO")

for i in ${!APP_NAMES[@]}; do
    delete_app_by_name_if_exists "${APP_NAMES[$i]}"
done

USER_NAMES=("${PREFIX}_patient" "${PREFIX}_provider")

for i in ${!USER_NAMES[@]}; do
    delete_user_by_name_if_exists "${USER_NAMES[$i]}"
done