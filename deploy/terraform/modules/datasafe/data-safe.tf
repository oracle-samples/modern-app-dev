resource "oci_data_safe_data_safe_configuration" "uho_data_safe_configuration" {
  is_enabled = "true"
  provider   = oci
}

resource "oci_core_network_security_group" "data_safe_private_endpoint_nsg" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id
  display_name   = "DataSafeSecurityGroup-${var.label_suffix}"
  provider       = oci
}

resource "oci_core_subnet" "data_safe_private_endpoint_subnet" {
  cidr_block                 = var.data_safe_subnet_cidr
  display_name               = "data-safe-subnet-${var.label_suffix}"
  dns_label                  = "datasafe"
  compartment_id             = var.compartment_id
  vcn_id                     = var.vcn_id
  prohibit_public_ip_on_vnic = true
  prohibit_internet_ingress  = true
  depends_on                 = [oci_core_network_security_group.data_safe_private_endpoint_nsg]
  provider                   = oci
}

resource "oci_data_safe_data_safe_private_endpoint" "uho_data_safe_private_endpoint" {
  compartment_id      = var.compartment_id
  display_name        = "Data-Safe-Private-Endpoint-${var.label_suffix}"
  subnet_id           = oci_core_subnet.data_safe_private_endpoint_subnet.id
  vcn_id              = var.vcn_id
  nsg_ids             = [oci_core_network_security_group.data_safe_private_endpoint_nsg.id]
  private_endpoint_ip = var.private_endpoint_ip
  depends_on          = [oci_data_safe_data_safe_configuration.uho_data_safe_configuration, oci_core_network_security_group.data_safe_private_endpoint_nsg, oci_core_subnet.data_safe_private_endpoint_subnet]
  provider            = oci
}

resource "oci_core_network_security_group_security_rule" "atp_pe_subnet_ingress_rule" {
  network_security_group_id = var.atp_nsg_id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = oci_core_subnet.data_safe_private_endpoint_subnet.cidr_block
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 1522
      min = 1522
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "atp_pe_subnet_egress_rule" {
  network_security_group_id = var.atp_nsg_id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = oci_core_subnet.data_safe_private_endpoint_subnet.cidr_block
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "atp_egress_rule" {
  network_security_group_id = var.atp_nsg_id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = "${data.oci_database_autonomous_database.uho_adb.private_endpoint_ip}/32"
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "ajdb_pe_subnet_ingress_rule" {
  network_security_group_id = var.ajdb_nsg_id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = oci_core_subnet.data_safe_private_endpoint_subnet.cidr_block
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 1522
      min = 1522
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "ajdb_pe_subnet_egress_rule" {
  network_security_group_id = var.ajdb_nsg_id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = oci_core_subnet.data_safe_private_endpoint_subnet.cidr_block
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "ajdb_egress_rule" {
  network_security_group_id = var.ajdb_nsg_id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = "${data.oci_database_autonomous_database.uho_json_db.private_endpoint_ip}/32"
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "pe_adb_ingress_rule" {
  network_security_group_id = oci_core_network_security_group.data_safe_private_endpoint_nsg.id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = var.atp_cidr
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 1522
      min = 1522
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "pe_adb_egress_rule" {
  network_security_group_id = oci_core_network_security_group.data_safe_private_endpoint_nsg.id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = var.atp_cidr
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "pe_ajdb_ingress_rule" {
  network_security_group_id = oci_core_network_security_group.data_safe_private_endpoint_nsg.id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = var.ajdb_cidr
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 1522
      min = 1522
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "pe_ajdb_egress_rule" {
  network_security_group_id = oci_core_network_security_group.data_safe_private_endpoint_nsg.id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = var.ajdb_cidr
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}

resource "oci_core_network_security_group_security_rule" "pe_pe_egress_rule" {
  network_security_group_id = oci_core_network_security_group.data_safe_private_endpoint_nsg.id
  direction                 = "EGRESS"
  protocol                  = "6"
  destination               = "${var.private_endpoint_ip}/32"
  destination_type          = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = "1522"
      min = "1522"
    }
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint]
  provider   = oci
}


resource "oci_data_safe_target_database" "uho_adb_data_safe_target_database" {
  compartment_id = var.compartment_id
  display_name   = "uho-atp-target-database-${var.label_suffix}"

  database_details {
    database_type          = "AUTONOMOUS_DATABASE"
    infrastructure_type    = "ORACLE_CLOUD"
    autonomous_database_id = var.atp_id
  }

  connection_option {
    connection_type              = "PRIVATE_ENDPOINT"
    datasafe_private_endpoint_id = oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint.id
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint,
    oci_core_network_security_group_security_rule.atp_egress_rule, oci_core_network_security_group_security_rule.atp_pe_subnet_egress_rule, oci_core_network_security_group_security_rule.atp_pe_subnet_ingress_rule,
    oci_core_network_security_group_security_rule.ajdb_egress_rule, oci_core_network_security_group_security_rule.ajdb_pe_subnet_egress_rule, oci_core_network_security_group_security_rule.ajdb_pe_subnet_ingress_rule,
    oci_core_network_security_group_security_rule.pe_adb_egress_rule, oci_core_network_security_group_security_rule.pe_adb_ingress_rule,
    oci_core_network_security_group_security_rule.pe_ajdb_egress_rule, oci_core_network_security_group_security_rule.pe_ajdb_ingress_rule,
  oci_core_network_security_group_security_rule.pe_pe_egress_rule]
  description = "UHO ADB Target Database"
  provider    = oci
}

resource "oci_data_safe_target_database" "uho_ajdb_data_safe_target_database" {
  compartment_id = var.compartment_id
  display_name   = "uho-ajdb-target-database-${var.label_suffix}"

  database_details {
    database_type          = "AUTONOMOUS_DATABASE"
    infrastructure_type    = "ORACLE_CLOUD"
    autonomous_database_id = var.ajdb_id
  }

  connection_option {
    connection_type              = "PRIVATE_ENDPOINT"
    datasafe_private_endpoint_id = oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint.id
  }
  depends_on = [oci_data_safe_data_safe_private_endpoint.uho_data_safe_private_endpoint,
    oci_core_network_security_group_security_rule.atp_egress_rule, oci_core_network_security_group_security_rule.atp_pe_subnet_egress_rule, oci_core_network_security_group_security_rule.atp_pe_subnet_ingress_rule,
    oci_core_network_security_group_security_rule.ajdb_egress_rule, oci_core_network_security_group_security_rule.ajdb_pe_subnet_egress_rule, oci_core_network_security_group_security_rule.ajdb_pe_subnet_ingress_rule,
    oci_core_network_security_group_security_rule.pe_adb_egress_rule, oci_core_network_security_group_security_rule.pe_adb_ingress_rule,
    oci_core_network_security_group_security_rule.pe_ajdb_egress_rule, oci_core_network_security_group_security_rule.pe_ajdb_ingress_rule,
  oci_core_network_security_group_security_rule.pe_pe_egress_rule]
  provider = oci
}

data "oci_database_autonomous_database" "uho_adb" {
  autonomous_database_id = var.atp_id
}
data "oci_database_autonomous_database" "uho_json_db" {
  autonomous_database_id = var.ajdb_id
}

