resource "oci_apm_apm_domain" "uho_apm_domain" {

  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.display_name}"

  description = local.description
}

data "oci_apm_data_keys" "uho_public_data_keys" {

  apm_domain_id = oci_apm_apm_domain.uho_apm_domain.id

  #Optional
  data_key_type = local.data_key_type_public
}

data "oci_apm_data_keys" "uho_private_data_keys" {

  apm_domain_id = oci_apm_apm_domain.uho_apm_domain.id

  #Optional
  data_key_type = local.data_key_type_private
}

locals {
  display_name          = "UHO APM domain"
  description           = "UHO APM domain"
  data_key_type_public  = "PUBLIC"
  data_key_type_private = "PRIVATE"
}