resource "oci_identity_policy" "cloud_guard_policy" {
  name           = "cloud_guard_policy_${var.label_suffix}"
  description    = "Policies for enabling Cloud Guard"
  compartment_id = var.tenancy_id
  statements     = local.cloud_guard_policy_statements
  provider       = oci.home
}

locals {
  cloud_guard_policy_statements = [
    "allow service cloudguard to read vaults in tenancy",
    "allow service cloudguard to read keys in tenancy",
    "allow service cloudguard to read compartments in tenancy",
    "allow service cloudguard to read tenancies in tenancy",
    "allow service cloudguard to read audit-events in tenancy",
    "allow service cloudguard to read compute-management-family in tenancy",
    "allow service cloudguard to read instance-family in tenancy",
    "allow service cloudguard to read virtual-network-family in tenancy",
    "allow service cloudguard to read volume-family in tenancy",
    "allow service cloudguard to read database-family in tenancy",
    "allow service cloudguard to read object-family in tenancy",
    "allow service cloudguard to read load-balancers in tenancy",
    "allow service cloudguard to read users in tenancy",
    "allow service cloudguard to read groups in tenancy",
    "allow service cloudguard to read policies in tenancy",
    "allow service cloudguard to read dynamic-groups in tenancy",
    "allow service cloudguard to read authentication-policies in tenancy",
    "allow service cloudguard to use network-security-groups in tenancy",
    "allow service cloudguard to read data-safe-family in tenancy",
    "allow service cloudguard to read autonomous-database-family in tenancy"
  ]
}


resource "oci_cloud_guard_cloud_guard_configuration" "cloud_guard_configuration" {
  compartment_id   = var.tenancy_id
  reporting_region = var.reporting_region
  status           = "ENABLED"
  provider         = oci.reporting_region
  depends_on       = [oci_identity_policy.cloud_guard_policy]
}

resource "oci_cloud_guard_target" "app_target" {
  compartment_id       = var.compartment_id
  display_name         = "RefApp Target-${var.label_suffix}"
  target_resource_id   = var.compartment_id
  target_resource_type = "COMPARTMENT"
  provider             = oci.reporting_region
  depends_on           = [data.oci_cloud_guard_detector_recipes.test_detector_recipes, data.oci_cloud_guard_responder_recipes.test_responder_recipes]

  target_detector_recipes {
    detector_recipe_id = data.oci_cloud_guard_detector_recipes.test_detector_recipes.detector_recipe_collection.0.items.0.id
  }
  target_detector_recipes {
    detector_recipe_id = data.oci_cloud_guard_detector_recipes.test_detector_recipes.detector_recipe_collection.0.items.1.id
  }
  target_responder_recipes {
    responder_recipe_id = data.oci_cloud_guard_responder_recipes.test_responder_recipes.responder_recipe_collection.0.items.0.id
  }
}

data "oci_cloud_guard_detector_recipes" "test_detector_recipes" {
  compartment_id = var.tenancy_id
  provider       = oci.reporting_region
  depends_on     = [oci_cloud_guard_cloud_guard_configuration.cloud_guard_configuration]
}

data "oci_cloud_guard_responder_recipes" "test_responder_recipes" {
  compartment_id = var.tenancy_id
  provider       = oci.reporting_region
  depends_on     = [oci_cloud_guard_cloud_guard_configuration.cloud_guard_configuration]
}
