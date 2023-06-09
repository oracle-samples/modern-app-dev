resource "oci_identity_policy" "atp_access_policy" {
  name           = "${local.atp_policy_name}-${var.label_suffix}"
  description    = "${var.app_name} compartment policy for managing autonomus transaction processing"
  compartment_id = var.compartment_id
  statements     = local.allow_atp_mgmt_statements
  provider       = oci.home
}

locals {
  atp_policy_name ="atp-access-policy"
  allow_atp_mgmt_statements    = [
    "Allow dynamic-group ${var.dynamic_group_name} to manage autonomous-database-family in compartment id ${var.compartment_id}"
  ]
}