data "oci_identity_user" "current_user" {
  user_id = var.current_user_ocid
}

resource "oci_identity_policy" "stream-group-policies" {
  name           = "stream-group-policies-${var.label_prefix}"
  description    = "stream group policies in uho app"
  compartment_id = var.compartment_id
  statements     = ["Allow any-user to manage streams in compartment id ${var.compartment_id}"]
  provider       = oci.home
}

resource "oci_identity_dynamic_group" "uho_dynamic_group" {
  compartment_id = var.tenancy_id
  description    = "Dynamic group for services that use streaming for asynchronous communications of messages in UHO"
  matching_rule  = "ALL {instance.compartment.id = '${var.compartment_id}'}"
  name           = "uho-dynamic-group-${var.label_prefix}"
  provider       = oci.home
}

resource "oci_identity_policy" "uho-dynamic-group-policies" {
  name           = "uho-dynamic-group-policies-${var.label_prefix}"
  description    = "UHO Dynamic Group policies in UHO app"
  compartment_id = var.compartment_id
  statements     = ["Allow dynamic-group ${oci_identity_dynamic_group.uho_dynamic_group.name} to manage stream-family in compartment id ${var.compartment_id}"]
  provider       = oci.home
  depends_on     = [oci_identity_dynamic_group.uho_dynamic_group]
}
