#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

set -e

echo "Inside operator"

helm uninstall oke-setup
echo "Sleeping for 60s after uninstalling oke-setup release.."
sleep 60

echo "Deleting ingress-nginx namespace"
kubectl delete namespaces ingress-nginx