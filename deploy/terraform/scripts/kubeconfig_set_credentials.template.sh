#!/bin/bash
#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

kubectl config set-credentials "user-${cluster-id-11}" --exec-command="$HOME/bin/token_helper.sh" \
  --exec-arg="ce" \
  --exec-arg="cluster" \
  --exec-arg="generate-token" \
  --exec-arg="--cluster-id" \
  --exec-arg="${cluster-id}" \
  --exec-arg="--region" \
  --exec-arg="${region}"