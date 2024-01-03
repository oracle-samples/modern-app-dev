#!/bin/bash
#  Copyright (c) 2023 Oracle and/or its affiliates.
#  Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/

# REGION_CODE
# NAMESPACE
# USERNAME
# AUTHTOKEN
# IMAGE_TAG
# DEPLOY_ID

set -e

cd ../../src
export DOCKER_BUILDKIT=1
echo $NAMESPACE/$USERNAME
docker login $REGION_CODE.ocir.io --username "$NAMESPACE/$USERNAME" --password "$AUTHTOKEN"

# docker images build from dockerfile
for service in appointment encounter feedback followup frontend patient provider; do
  mkdir -p "$service/target"
  cp -r ../deploy/terraform/specs "$service/target"
  docker build -t "$REGION_CODE.ocir.io/$NAMESPACE/uho-$service-$DEPLOY_ID:$IMAGE_TAG" "$service/."
  docker push "$REGION_CODE.ocir.io/$NAMESPACE/uho-$service-$DEPLOY_ID:$IMAGE_TAG"
done

# docker images build from jib
for service in notification; do
  mkdir -p "$service/target"
  cp -r ../deploy/terraform/specs "$service/target"
  cd "$service"
  mvn package -Dpackaging=docker -Djib.docker.image="$REGION_CODE.ocir.io/$NAMESPACE/uho-$service-$DEPLOY_ID" -Djib.docker.tag="$IMAGE_TAG"
  docker push "$REGION_CODE.ocir.io/$NAMESPACE/uho-$service-$DEPLOY_ID:$IMAGE_TAG"
  cd ..
done

echo "Docker image builds and push to OCIR completed"
