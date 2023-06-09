#!/bin/bash

#
# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/
#

# set -e

terraform state rm "kubernetes_config_map.idcs"

terraform state rm "kubernetes_secret.idcs[0]"

terraform state rm "kubernetes_secret.atp-admin[0]"

terraform state rm "kubernetes_secret.atp-connection[0]"

terraform state rm "kubernetes_secret.atp_wallet_zip[0]"

terraform state rm "kubernetes_service_account.secret_creator_sa[0]"

terraform state rm "kubernetes_config_map.apigw"

terraform state rm "helm_release.uho"

terraform state rm "kubernetes_cluster_role_binding.secret_creator_crb[0]"

terraform state rm "kubernetes_secret.uho-streampool-connection[0]"
