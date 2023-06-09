#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

# IDCS_ADMIN_CLIENT_ID - client id of admin app
# IDCS_ADMIN_CLIENT_SECRET - client secret of admin app
# APIGW_URL - api gw url
# PREFIX - deploy id prefix

set -e

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

# Check if patient client app exists
echo 'Searching patient client app...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Apps \
  -G --data-urlencode "filter=displayName co \"$PREFIX Patient Client Application\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  echo 'Deleting existing patient client app..';
  APP_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deactivating app with app id $APP_ID";
  DEACTIVATE_RESPONSE=`curl -sS -f --location --request PUT $IDCS_URL/admin/v1/AppStatusChanger/$APP_ID \
  --header 'Content-Type: application/json'\
  --header "Authorization: Bearer $ACCESS_TOKEN"\
  --data-raw '{
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

# Check if provider client app exists
echo 'Searching provider client app...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Apps \
  -G --data-urlencode "filter=displayName co \"$PREFIX Provider Client Application\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  echo 'Deleting existing provider client app..';
  APP_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deactivating app with app id $APP_ID";
  DEACTIVATE_RESPONSE=`curl -sS -f --location --request PUT $IDCS_URL/admin/v1/AppStatusChanger/$APP_ID \
  --header 'Content-Type: application/json'\
  --header "Authorization: Bearer $ACCESS_TOKEN"\
  --data-raw '{
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

# Check if service client app exists
echo 'Searching service client app...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Apps \
  -G --data-urlencode "filter=displayName co \"$PREFIX Service Client Application\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  echo 'Deleting existing provider client app..';
  APP_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deactivating app with app id $APP_ID";
  DEACTIVATE_RESPONSE=`curl -sS -f --location --request PUT $IDCS_URL/admin/v1/AppStatusChanger/$APP_ID \
  --header 'Content-Type: application/json'\
  --header "Authorization: Bearer $ACCESS_TOKEN"\
  --data-raw '{
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

# Check if resource server app exists
echo 'Searching resource server app...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Apps \
  -G --data-urlencode "filter=displayName co \"$PREFIX Resource Server for UHO\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  echo 'Deleting existing resource server app..';
  APP_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deactivating app with app id $APP_ID";
  DEACTIVATE_RESPONSE=`curl -sS -f --location --request PUT $IDCS_URL/admin/v1/AppStatusChanger/$APP_ID \
  --header 'Content-Type: application/json'\
  --header "Authorization: Bearer $ACCESS_TOKEN"\
  --data-raw '{
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

# ## Create resource server app
echo 'Creating resource server app...'
RESOURCE_APP_ID=`curl -sS -f --location --request POST $IDCS_URL/admin/v1/Apps \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--data-raw "{
  \"schemas\": [\"urn:ietf:params:scim:schemas:oracle:idcs:App\"],
  \"basedOnTemplate\": { \"value\": \"CustomWebAppTemplateId\" },
  \"displayName\": \"$PREFIX Resource Server for UHO\",
  \"description\": \"$PREFIX Resource Server application for UHO\",
  \"isOAuthResource\": true,
  \"accessTokenExpiry\": 3600,
  \"refreshTokenExpiry\": 604800,
  \"allUrlSchemesAllowed\": false,
  \"audience\": \"$APIGW_URL\",
  \"active\": true,
  \"scopes\": [
    {
      \"value\": \"/patient\",
      \"fqs\": \"$APIGW_URL/patient\",
      \"requiresConsent\": true,
      \"description\": \"Access resources as a patient\"
    },{
      \"value\": \"/provider\",
      \"fqs\": \"$APIGW_URL/provider\",
      \"requiresConsent\": true,
      \"description\": \"Access resources as a provider\"
    },{
      \"value\": \"/analyst\",
      \"fqs\": \"$APIGW_URL/analyst\",
      \"requiresConsent\": true,
      \"description\": \"Access resources as a analyst\"
    },{
      \"value\": \"/service\",
      \"fqs\": \"$APIGW_URL/service\",
      \"requiresConsent\": true,
      \"description\": \"Access resources as a service\"
    }]
}" | jq -r '.id'`


# # echo "Resource app id $RESOURCE_APP_ID"
# # sleep 5;
# # # Activate resource server app
# # echo 'Activating resource server app...'

# # curl -sS -f --location --request PUT "$IDCS_URL/admin/v1/AppStatusChanger/$RESOURCE_APP_ID" \
# # --header 'Content-Type: application/json' \
# # --header "Authorization: Bearer $ACCESS_TOKEN" \
# # --data-raw '{
# #   "active": true,
# #   "schemas": [
# #     "urn:ietf:params:scim:schemas:oracle:idcs:AppStatusChanger"
# #   ]
# # }'

# ## Create users

# Check if patient user exists
echo 'Searching patient user...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Users \
  -G --data-urlencode "filter=userName eq \"${PREFIX}_patient\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  PATIENT_USER_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deleting patient user with user id $PATIENT_USER_ID"
  DELETE_RESPONSE=`curl -sS -f --request DELETE $IDCS_URL/admin/v1/Users/$PATIENT_USER_ID \
  --header "Authorization: Bearer $ACCESS_TOKEN"`
  echo "Delete response $DELETE_RESPONSE";
fi;

# # Patient user
echo 'Creating a patient user...'
PATIENT_USER_ID=`curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Users" \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--header 'Content-Type: application/json' \
--data-raw "{
  \"schemas\": [
    \"urn:ietf:params:scim:schemas:core:2.0:User\"
  ],
  \"name\": {
	\"givenName\": \"John\",
	\"familyName\": \"Doe\"
  },
  \"userName\": \"${PREFIX}_patient\",
  \"password\": \"UH01_patient\",
  \"emails\": [
      {
	  \"value\": \"${PREFIX}_patient@example.com\",
	  \"primary\": true,
	  \"type\": \"work\"
	},
	{
	  \"value\": \"${PREFIX}_patient@example.com\",
	  \"primary\": false,
	  \"type\": \"recovery\"
	}
  ]
}" | jq -r '.id'`


# Check if provider user exists
echo 'Searching provider user...'
RESP=`curl -sS -f --location --request GET $IDCS_URL/admin/v1/Users \
  -G --data-urlencode "filter=userName eq \"${PREFIX}_provider\"" \
  -d 'attributes=id' \
  --header "Authorization: Bearer $ACCESS_TOKEN" `

TOTAL_RESULTS=`echo $RESP | jq '.totalResults'`
echo "Total results $TOTAL_RESULTS"

if (( $TOTAL_RESULTS > 0 )); then
  PROVIDER_USER_ID=`echo $RESP | jq -r '.Resources[0].id'`;

  echo "Deleting provider user with user id $PROVIDER_USER_ID"
  DELETE_RESPONSE=`curl -sS -f --request DELETE $IDCS_URL/admin/v1/Users/$PROVIDER_USER_ID \
  --header "Authorization: Bearer $ACCESS_TOKEN"`
  echo "Delete response $DELETE_RESPONSE";
fi;

# echo 'Creating a provider user...'
PROVIDER_USER_ID=`curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Users" \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--header 'Content-Type: application/json' \
--data-raw "{
  \"schemas\": [
    \"urn:ietf:params:scim:schemas:core:2.0:User\"
  ],
  \"name\": {
	\"givenName\": \"John\",
	\"familyName\": \"Doe\"
  },
  \"userName\": \"${PREFIX}_provider\",
  \"password\": \"UH01_provider\",
  \"emails\": [
      {
	  \"value\": \"${PREFIX}_provider@example.com\",
	  \"primary\": true,
	  \"type\": \"work\"
	},
	{
	  \"value\": \"${PREFIX}_provider@example.com\",
	  \"primary\": false,
	  \"type\": \"recovery\"
	}
  ]
}" | jq -r '.id'`

# Create client applications
echo 'Creating patient app...'
response=`curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Apps" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--data-raw "{
  \"schemas\": [\"urn:ietf:params:scim:schemas:oracle:idcs:App\"],
  \"basedOnTemplate\": { \"value\": \"CustomWebAppTemplateId\" },
  \"displayName\": \"$PREFIX Patient Client Application\",
  \"description\": \"Patient Client Application\",
  \"clientType\": \"confidential\",
  \"isOAuthClient\": true,
  \"allowedGrants\": [\"authorization_code\",\"client_credentials\"],
  \"redirectUris\": [\"$APIGW_URL/oauth/callback/patient\"],
  \"logoutUri\": \"$APIGW_URL/logout\",
  \"postLogoutRedirectUris\": [\"$APIGW_URL\"],
  \"allowedScopes\": [{
      \"fqs\": \"$APIGW_URL/patient\"
    }],
  \"active\": true,  
  \"trustScope\":\"Explicit\",
  \"allowAccessControl\": true
}" `
echo $response
PATIENT_APP_ID=`echo $response | jq -r '.id'`
PATIENT_APP_CLIENT_ID=`echo -n $response | jq -r '.name'`
PATIENT_APP_CLIENT_SECRET=`echo -n $response | jq -r '.clientSecret'`

echo 'Creating provider app...'
response=`curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Apps" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--data-raw "{
  \"schemas\": [\"urn:ietf:params:scim:schemas:oracle:idcs:App\"],
  \"basedOnTemplate\": { \"value\": \"CustomWebAppTemplateId\" },
  \"displayName\": \"$PREFIX Provider Client Application\",
  \"description\": \"Provider Client Application\",
  \"clientType\": \"confidential\",
  \"isOAuthClient\": true,
  \"allowedGrants\": [\"authorization_code\",\"client_credentials\"],
  \"redirectUris\": [\"$APIGW_URL/oauth/callback/provider\"],
  \"logoutUri\": \"$APIGW_URL/logout\",
  \"postLogoutRedirectUris\": [\"$APIGW_URL\"],
  \"allowedScopes\": [{
      \"fqs\": \"$APIGW_URL/provider\"
    }],
  \"active\": true,
  \"trustScope\":\"Explicit\",
  \"allowAccessControl\": true
}"`
PROVIDER_APP_ID=`echo -n $response | jq -r '.id'`
PROVIDER_APP_CLIENT_ID=`echo -n $response | jq -r '.name'`
PROVIDER_APP_CLIENT_SECRET=`echo -n $response | jq -r '.clientSecret'`

echo 'Creating service app...'
response=`curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Apps" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--data-raw "{
  \"schemas\": [\"urn:ietf:params:scim:schemas:oracle:idcs:App\"],
  \"basedOnTemplate\": { \"value\": \"CustomWebAppTemplateId\" },
  \"displayName\": \"$PREFIX Service Client Application\",
  \"description\": \"Service Client Application\",
  \"clientType\": \"confidential\",
  \"isOAuthClient\": true,
  \"allowedGrants\": [\"authorization_code\",\"client_credentials\"],
  \"redirectUris\": [\"$APIGW_URL\"],
  \"allowedScopes\": [{
      \"fqs\": \"$APIGW_URL/service\"
    }],
  \"active\": true,
  \"trustScope\":\"Explicit\",
  \"allowAccessControl\": true
}"`
SERVICE_APP_ID=`echo -n $response | jq -r '.id'`
SERVICE_APP_CLIENT_ID=`echo -n $response | jq -r '.name'`
SERVICE_APP_CLIENT_SECRET=`echo -n $response | jq -r '.clientSecret'`

# Assign users to respective applications
echo 'Granting apps to users'
# echo $PATIENT_USER_ID
# echo $PATIENT_APP_ID
curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Grants" \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--header 'Content-Type: application/json' \
--data-raw "{
    \"grantee\": {
        \"type\": \"User\",
         \"value\": \"${PATIENT_USER_ID}\"
    },
    \"app\": {
        \"value\": \"$PATIENT_APP_ID\"
    },
    \"grantMechanism\" : \"ADMINISTRATOR_TO_USER\",
    \"schemas\": [
    \"urn:ietf:params:scim:schemas:oracle:idcs:Grant\"
  ]
}"

curl -sS -f --location --request POST "$IDCS_URL/admin/v1/Grants" \
--header "Authorization: Bearer $ACCESS_TOKEN" \
--header 'Content-Type: application/json' \
--data-raw "{
    \"grantee\": {
        \"type\": \"User\",
         \"value\": \"${PROVIDER_USER_ID}\"
    },
    \"app\": {
        \"value\": \"$PROVIDER_APP_ID\"
    },
    \"grantMechanism\" : \"ADMINISTRATOR_TO_USER\",
    \"schemas\": [
    \"urn:ietf:params:scim:schemas:oracle:idcs:Grant\"
  ]
}"

echo -n $PATIENT_APP_CLIENT_ID > /idcs/patient-app-client-id
echo -n $PATIENT_APP_CLIENT_SECRET > /idcs/patient-app-client-secret
echo -n $PROVIDER_APP_CLIENT_ID > /idcs/provider-app-client-id
echo -n $PROVIDER_APP_CLIENT_SECRET > /idcs/provider-app-client-secret
echo -n $SERVICE_APP_CLIENT_ID > /idcs/service-app-client-id
echo -n $SERVICE_APP_CLIENT_SECRET > /idcs/service-app-client-secret