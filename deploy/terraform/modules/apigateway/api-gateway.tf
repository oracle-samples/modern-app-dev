resource "oci_core_subnet" "apigw_subnet" {
  cidr_block                 = var.apigw_cidr
  compartment_id             = var.compartment_id
  display_name               = "apigw-subnet-${local.app_name_normalized}-${var.label_prefix}"
  dns_label                  = "apigw${var.label_prefix}"
  vcn_id                     = var.vcn_id
  prohibit_public_ip_on_vnic = false
  route_table_id             = oci_core_route_table.apigw_route_table.id
}

resource "oci_core_network_security_group" "apigw_security_group" {
  compartment_id = var.compartment_id
  display_name   = "APIGWSecurityGroup-${var.label_prefix}"
  vcn_id         = var.vcn_id
}

#EGRESS
resource "oci_core_network_security_group_security_rule" "apigw_security_egress_group_rule" {
  network_security_group_id = oci_core_network_security_group.apigw_security_group.id
  direction                 = "EGRESS"
  protocol                  = local.tcp_protocol_number
  destination               = local.all_cidr
  destination_type          = "CIDR_BLOCK"
}

# INGRESS
resource "oci_core_network_security_group_security_rule" "apigw_security_ingress_group_rules" {
  network_security_group_id = oci_core_network_security_group.apigw_security_group.id
  direction                 = "INGRESS"
  protocol                  = local.tcp_protocol_number
  source                    = local.all_cidr
  source_type               = "CIDR_BLOCK"
  stateless                 = false
  tcp_options {
    destination_port_range {
      max = local.https_port_number
      min = local.https_port_number
    }
  }
}

resource "oci_apigateway_gateway" "api_gateway" {
  compartment_id             = var.compartment_id
  endpoint_type              = "PUBLIC"
  subnet_id                  = oci_core_subnet.apigw_subnet.id
  network_security_group_ids = [oci_core_network_security_group.apigw_security_group.id]
  display_name               = "${var.app_name} API Gateway - ${var.label_prefix}"

  response_cache_details {
    type = "NONE"
  }
}

resource "oci_core_route_table" "apigw_route_table" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id

  route_rules {
    destination       = local.all_cidr
    network_entity_id = oci_core_internet_gateway.internet_gateway.id
    description       = "Internet Gateway as default gateway for all traffic"
  }
}

resource "oci_core_internet_gateway" "internet_gateway" {
  compartment_id = var.compartment_id
  vcn_id         = var.vcn_id
  display_name   = "Internet Gateway"
}

resource "null_resource" "create_idcs_key_file" {
  provisioner "local-exec" {
    command = "chmod +x modules/apigateway/fetch_idcs_static_keys.sh && modules/apigateway/fetch_idcs_static_keys.sh"
    environment = {
      IDCS_URL                 = var.idcs_url
      IDCS_ADMIN_CLIENT_ID     = var.idcs_admin_client_id
      IDCS_ADMIN_CLIENT_SECRET = var.idcs_admin_client_secret
      BUCKET_NAME              = var.bucket_name
    }
  }
}

data "oci_objectstorage_object" "idcs_key_file" {
  bucket     = var.bucket_name
  namespace  = var.object_storage_namespace_name
  object     = "idcs_public_key"
  depends_on = [null_resource.create_idcs_key_file]
}

locals {
  http_port_number          = "80"
  https_port_number         = "443"
  tcp_protocol_number       = "6"
  all_protocols             = "all"
  app_name_normalized       = substr(replace(lower(var.app_name), " ", "-"), 0, 6)
  ingress_protocol          = "http"
  ingress_ip                = var.ingress_ip
  issuer                    = var.idcs_url
  public_keys_type          = "STATIC_KEYS"
  route_backend_type        = "HTTP_BACKEND"
  authentication_type       = "JWT_AUTHENTICATION"
  token_header              = "Authorization"
  token_auth_scheme         = "Bearer"
  authorization_type_any_of = "ANY_OF"
  all_cidr                  = "0.0.0.0/0"
  idcs_public_key           = jsondecode(data.oci_objectstorage_object.idcs_key_file.content)
}
