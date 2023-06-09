resource "oci_email_sender" "email_sender" {
  compartment_id = var.compartment_id
  email_address  = local.sender_email_address
}

locals {
  sender_email_address = "help@uho.com"
}