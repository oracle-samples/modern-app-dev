resource "oci_identity_policy" "oke_logging_policy" {
  name           = "${local.oke_logging_policy_name}-${var.label_prefix}"
  description    = "${var.app_name} compartment policy for application logging "
  compartment_id = var.compartment_id
  statements     = local.allow_logging_statements
  provider       = oci.home
}

locals {
  allow_logging_statements    = [
    "Allow dynamic-group ${var.dynamic_group_name} to use log-content in compartment id ${var.compartment_id}"
  ]
}