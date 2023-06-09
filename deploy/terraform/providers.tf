terraform {
  required_version = ">= 1.0"
  required_providers {
    oci = {
      source  = "oracle/oci"
      version = ">=4.72.0"
    }
    tls = {
      source = "hashicorp/tls"
    }
    local = {
      source = "hashicorp/local"
    }
    random = {
      source = "hashicorp/random"
    }
  }
}

data "oci_identity_tenancy" "tenancy" {
  tenancy_id = var.tenancy_ocid
}

data "oci_identity_regions" "home-region" {
  filter {
    name   = "key"
    values = [data.oci_identity_tenancy.tenancy.home_region_key]
  }
}

data "local_file" "private_ssh_key_file" {
  filename = var.private_key_path
  count    = var.generate_ssh_pair ? 0 : 1
}

data "local_file" "public_ssh_key_file" {
  filename = var.public_key_path
  count    = var.generate_ssh_pair ? 0 : 1
}

provider "oci" {
  tenancy_ocid = var.tenancy_ocid
  region       = var.region
}

provider "oci" {
  alias        = "home"
  tenancy_ocid = var.tenancy_ocid
  region       = local.home_region
}

provider "oci" {
  alias        = "reporting_region"
  tenancy_ocid = var.tenancy_ocid
  region       = var.reporting_region == "" ? var.region : var.reporting_region
}