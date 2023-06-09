### creates an ATP database
resource "oci_database_autonomous_database" "uho_autonomous_database" {
  admin_password           = random_string.autonomous_database_admin_password.result
  compartment_id           = var.compartment_id
  cpu_core_count           = local.autonomous_database_cpu_core_count
  data_storage_size_in_tbs = local.autonomous_database_data_storage_size_in_tbs
  data_safe_status         = local.autonomous_database_data_safe_status
  db_version               = local.autonomous_database_db_version
  db_name                  = "${local.app_name_for_db}${var.label_suffix}"
  display_name             = "Db ${var.app_name} ${var.label_suffix} "
  license_model            = local.autonomous_database_license_model
  is_auto_scaling_enabled  = local.is_auto_scaling_enabled
  is_free_tier             = local.is_free_tier
  subnet_id                = oci_core_subnet.adb_endpoint_subnet.id
  nsg_ids                  = [oci_core_network_security_group.adb_security_group.id]
  private_endpoint_label   = local.autonomous_database_private_endpoint_label
  depends_on               = [oci_core_subnet.adb_endpoint_subnet]
}

### Wallet
resource "oci_database_autonomous_database_wallet" "autonomous_database_wallet" {
  autonomous_database_id = oci_database_autonomous_database.uho_autonomous_database.id
  password               = random_string.autonomous_database_wallet_password.result
  generate_type          = local.autonomous_database_wallet_generate_type
  base64_encode_content  = true
}

resource "oci_core_subnet" "adb_endpoint_subnet" {
  cidr_block                 = var.adb_cidr_block
  compartment_id             = var.compartment_id
  display_name               = "adb-endpoint-subnet-${local.app_name_normalized}-${var.label_suffix}"
  dns_label                  = "adbepsn${var.label_suffix}"
  vcn_id                     = var.vcn_id
  prohibit_public_ip_on_vnic = true
  route_table_id             = var.nat_route_id
  depends_on                 = [oci_core_network_security_group.adb_security_group]
}

resource "oci_core_network_security_group" "adb_security_group" {
  compartment_id = var.compartment_id
  display_name   = "ADBSecurityGroup"
  vcn_id         = var.vcn_id
}

#EGRESS
resource "oci_core_network_security_group_security_rule" "adb_security_egress_group_rule" {
  network_security_group_id = oci_core_network_security_group.adb_security_group.id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = var.subnet_regional_cidr
  destination_type          = "CIDR_BLOCK"
}

# INGRESS
resource "oci_core_network_security_group_security_rule" "adb_security_ingress_group_rules" {
  network_security_group_id = oci_core_network_security_group.adb_security_group.id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = var.subnet_regional_cidr
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 1522
      min = 1522
    }
  }
}

locals {
  autonomous_database_cpu_core_count           = 1
  autonomous_database_data_storage_size_in_tbs = 1
  autonomous_database_data_safe_status         = "NOT_REGISTERED"
  autonomous_database_db_version               = "19c"
  autonomous_database_license_model            = "BRING_YOUR_OWN_LICENSE"
  autonomous_database_private_endpoint_label   = "ADB-Private-Endpoint-Label${var.label_suffix}"
  autonomous_database_wallet_generate_type     = "SINGLE"
  app_name_normalized                          = substr(replace(lower(var.app_name), " ", "-"), 0, 6)
  app_name_for_db                              = regex("[[:alnum:]]{1,10}", var.app_name)
  is_auto_scaling_enabled                      = false
  is_free_tier                                 = false
}