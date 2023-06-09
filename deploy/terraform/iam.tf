resource "oci_identity_policy" "app_compartment_policies" {
  name           = "${local.app_name_normalized}-compartment-policies-${random_string.deploy_id.result}"
  description    = "${var.app_name} Compartment Policies (${random_string.deploy_id.result})"
  compartment_id = var.compartment_ocid
  statements     = var.use_cluster_encryption ? concat(local.allow_oke_use_oci_vault_keys_statements, [local.blockstorage_policy_statement]) : local.allow_oke_use_oci_vault_keys_statements
  provider       = oci.home
  count          = var.create_compartment_policies ? 1 : 0
}

# Individual Policy Statements
locals {
  dynamic_group_matching_rules = concat(
    local.instances_in_compartment_rule,
    local.clusters_in_compartment_rule
  )
  allow_oke_use_oci_vault_keys_statements = [
    "Allow any-user to use keys in compartment id ${var.compartment_ocid} where ALL {request.principal.type = 'cluster', target.key.id = '${local.uho_key}'}",

    "Allow any-user to manage vaults in compartment id  ${var.compartment_ocid} where ALL {request.principal.type = 'cluster'}",        // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",
    "Allow any-user to manage keys in compartment id  ${var.compartment_ocid} where ALL {request.principal.type = 'cluster'}",          // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",
    "Allow any-user to manage secret-family in compartment id  ${var.compartment_ocid} where ALL {request.principal.type = 'cluster'}", // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",

    "Allow any-user to manage vaults in compartment id  ${var.compartment_ocid}",        // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",
    "Allow any-user to manage keys in compartment id  ${var.compartment_ocid}",          // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",
    "Allow any-user to manage secret-family in compartment id  ${var.compartment_ocid}", // where target.vault.id='${oci_kms_vault.uho_vault.0.id}'",

    "Allow any-user to use metrics in compartment id ${var.compartment_ocid}"
  ]

  blockstorage_policy_statement = "Allow service blockstorage to use keys in compartment id ${var.compartment_ocid} where target.key.id = '${local.uho_key}'" // delete after oke module is updated

  instances_in_compartment_rule = ["ALL {instance.compartment.id = '${var.compartment_ocid}'}"]
  clusters_in_compartment_rule  = ["ALL {resource.type = 'cluster', resource.compartment.id = '${var.compartment_ocid}'}"]
}