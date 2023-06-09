resource "oci_identity_policy" "devopspolicy" {
  provider       = oci.home
  name           = "devops-policies-${var.label_prefix}"
  description    = "policy created for devops"
  compartment_id = var.compartment_id

  statements = [
    "Allow any-user to manage all-resources in compartment id ${var.compartment_id} where ALL {request.principal.type = 'devopsdeploypipeline'}",
  ]
}

resource "oci_identity_policy" "devopsrootpolicy" {
  provider       = oci.home
  name           = "devops-root-policies-${var.label_prefix}"
  description    = "policy created for refapp compartment"
  compartment_id = var.compartment_id

  statements = [
    "Allow any-user to manage all-resources in compartment id ${var.compartment_id} where ALL {request.principal.type = 'devopsrepository'}",
    "Allow any-user to manage all-resources in compartment id ${var.compartment_id} where ALL {request.principal.type = 'devopsbuildpipeline'}"
  ]
}