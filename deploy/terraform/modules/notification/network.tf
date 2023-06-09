# Subnets
resource "oci_core_subnet" "functions_subnet" {
  cidr_block                 = var.functions_cidr
  compartment_id             = var.compartment_id
  display_name               = "functions-subnet-${local.app_name_normalized}-${var.label_prefix}"
  dns_label                  = "funcsn${var.label_prefix}"
  vcn_id                     = var.vcn_id
  prohibit_public_ip_on_vnic = true
  route_table_id             = oci_core_route_table.functions_route_table.id
  security_list_ids          = [oci_core_security_list.functions_security_list.id]
  depends_on                 = [oci_core_network_security_group.function_security_group]
}

# Route tables
resource "oci_core_route_table" "functions_route_table" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id
  display_name   = "functions-route-table-${local.app_name_normalized}-${var.label_prefix}"

  route_rules {
    description       = "Traffic to/from internet"
    destination       = lookup(data.oci_core_services.all_services.services[0], "cidr_block")
    destination_type  = "SERVICE_CIDR_BLOCK"
    network_entity_id = data.oci_core_service_gateways.vcn_service_gateways.service_gateways[0].id
  }

  route_rules {
    description       = "For function to access internet to hit idcs endpoint and apigw"
    destination       = "0.0.0.0/0"
    destination_type  = "CIDR_BLOCK"
    network_entity_id = data.oci_core_nat_gateways.vcn_nat_gateways.nat_gateways[0].id
  }
}

# Security Groups

resource "oci_core_network_security_group" "function_security_group" {
  compartment_id = var.compartment_id
  display_name   = "FunctionSecurityGroup-${var.label_prefix}"
  vcn_id         = var.vcn_id
}

#EGRESS
resource "oci_core_network_security_group_security_rule" "function_security_egress_group_rule" {
  network_security_group_id = oci_core_network_security_group.function_security_group.id
  direction                 = "EGRESS"
  protocol                  = local.tcp_protocol_number
  destination               = "0.0.0.0/0"
  destination_type          = "CIDR_BLOCK"
}

# Gateways

data "oci_core_service_gateways" "vcn_service_gateways" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id
}

data "oci_core_nat_gateways" "vcn_nat_gateways" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id
}

resource "oci_core_security_list" "functions_security_list" {
  compartment_id = var.compartment_id
  display_name   = "functions-seclist-${local.app_name_normalized}-${var.label_prefix}"
  vcn_id         = var.vcn_id

  egress_security_rules {
    description      = "Allow Functions to forward requests"
    destination      = lookup(data.oci_core_services.all_services.services[0], "cidr_block")
    destination_type = "SERVICE_CIDR_BLOCK"
    protocol         = local.all_protocols
    stateless        = false
  }
  egress_security_rules {
    description      = "Allow Functions access to Internet"
    destination      = "0.0.0.0/0"
    destination_type = "CIDR_BLOCK"
    protocol         = local.all_protocols
    stateless        = false
  }
}

data "oci_core_services" "all_services" {
  filter {
    name   = "name"
    values = ["All .* Services In Oracle Services Network"]
    regex  = true
  }
}

locals {
  app_name_normalized = substr(replace(lower(var.app_name), " ", "-"), 0, 6)
  all_protocols       = "all"
  tcp_protocol_number = "6"
}
