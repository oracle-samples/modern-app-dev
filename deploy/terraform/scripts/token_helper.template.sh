#!/bin/bash
#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

CLUSTER=$5
REGION=$7

TOKEN_FILE=$HOME/.kube/TOKEN-$CLUSTER

if ! test -f "$TOKEN_FILE" || test $(( `date +%s` - `stat -L -c %Y $TOKEN_FILE` )) -gt 240; then
  umask 022
  oci ce cluster generate-token --cluster-id "$CLUSTER" --region "$REGION" > $TOKEN_FILE
fi

cat $TOKEN_FILE
