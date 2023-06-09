##**************************************************************************
##                            OCI KMS Vault
##**************************************************************************

### OCI Vault vault
resource "oci_kms_vault" "uho_vault" {
  compartment_id = var.compartment_ocid
  display_name   = "${local.vault_display_name} - ${random_string.deploy_id.result}"
  vault_type     = local.vault_type[0]

  #   depends_on = [oci_identity_policy.kms_user_group_compartment_policies]

  count = var.create_new_key ? 1 : 0
}

data "oci_kms_vault" "uho_vault" {

  vault_id = var.existent_kms_vault_id
  count    = var.create_new_key ? 0 : 1
}

### UHO Encryption key
resource "oci_kms_key" "uho_key" {
  compartment_id      = var.compartment_ocid
  display_name        = "${local.vault_devops_key_display_name} - ${random_string.deploy_id.result}"
  management_endpoint = oci_kms_vault.uho_vault[0].management_endpoint

  key_shape {
    algorithm = local.vault_key_key_shape_algorithm
    length    = local.vault_key_key_shape_length
  }

  count = var.create_new_key ? 1 : 0
}

### Devops Github PAT Secret
resource "oci_vault_secret" "uho_devops_github_pat_secret" {
  compartment_id = var.compartment_ocid

  secret_content {
    content_type = "BASE64"
    content      = base64encode(var.github_pat_token)
    name         = "${local.vault_devops_github_pat_secret_display_name}-${random_string.deploy_id.result}"
  }

  secret_name = "${local.vault_devops_github_pat_secret_display_name}-${random_string.deploy_id.result}"
  vault_id    = local.uho_vault_id
  description = "Secret for storing user's Github PAT Token used in Devops Project"
  key_id      = local.uho_key
  count       = var.create_external_connection ? 1 : 0
}

### Vault and Key definitions
locals {
  vault_display_name                          = "UHO Vault"
  vault_key_display_name                      = "UHO Key"
  vault_object_storage_key_display_name       = "UHO Object Storage Key"
  vault_devops_key_display_name               = "UHO Devops Key"
  vault_devops_github_pat_secret_display_name = "UHO-Devops-Secret"
  vault_key_key_shape_algorithm               = "AES"
  vault_key_key_shape_length                  = 32
  vault_type                                  = ["DEFAULT", "VIRTUAL_PRIVATE"]
}