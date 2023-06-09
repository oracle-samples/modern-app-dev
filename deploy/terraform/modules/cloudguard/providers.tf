terraform {
  required_providers {
    oci = {
      source                = "oracle/oci"
      configuration_aliases = [oci.reporting_region, oci.home]
    }
  }
  required_version = ">= 1.0.0"
}