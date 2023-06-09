resource "oci_identity_policy" "email-policies" {
  name           = "email-policies-${var.label_prefix}"
  description    = "uho email delivery policies"
  compartment_id = var.compartment_id
  statements = [
    "Allow any-user to use ons-topics in compartment id ${var.compartment_id}",
    "Allow any-user to {STREAM_READ, STREAM_CONSUME} in compartment id ${var.compartment_id}"
  ]
  provider = oci.home
}

resource "oci_identity_policy" "sch-policies" {
  name           = "sch-policies-${var.label_prefix}"
  description    = "Service Connector Hub Policies"
  compartment_id = var.compartment_id
  statements = [
    "Allow any-user to manage credentials in compartment id ${var.compartment_id} where target.credential.type = 'smtp'",
    "Allow any-user to manage email-family in compartment id ${var.compartment_id}"
  ]
  provider = oci.home
}
