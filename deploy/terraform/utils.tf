resource "tls_private_key" "public_private_key_pair" {
  algorithm = "RSA"
  count     = var.generate_ssh_pair ? 1 : 0
}

resource "null_resource" "create_ssh_folder" {
  provisioner "local-exec" {
    command = "rm -rf ~/.ssh"
  }
  provisioner "local-exec" {
    command = "mkdir ~/.ssh"
  }
  provisioner "local-exec" {
    command = "touch ~/.ssh/id_rsa"
  }
  provisioner "local-exec" {
    command = "touch ~/.ssh/id_rsa.pub"
  }
  count = var.generate_ssh_pair ? 1 : 0
}

resource "local_file" "private_ssh_key" {
  content         = tls_private_key.public_private_key_pair[0].private_key_pem
  depends_on      = [null_resource.create_ssh_folder[0]]
  filename        = local.private_ssh_key_path
  file_permission = "0600"
  count           = var.generate_ssh_pair ? 1 : 0
}

resource "local_file" "public_ssh_key" {
  content         = tls_private_key.public_private_key_pair[0].public_key_openssh
  depends_on      = [null_resource.create_ssh_folder[0]]
  filename        = local.public_ssh_key_path
  file_permission = "0600"
  count           = var.generate_ssh_pair ? 1 : 0
}

# App Name Locals
locals {
  app_name_normalized = substr(replace(lower(var.app_name), " ", "-"), 0, 6)
  app_name_for_db     = regex("[[:alnum:]]{1,10}", var.app_name)
}

## Available Services
data "oci_core_services" "all_services" {
  filter {
    name   = "name"
    values = ["All .* Services In Oracle Services Network"]
    regex  = true
  }
}

locals {
  private_ssh_key_path = var.generate_ssh_pair ? pathexpand("~/.ssh/id_rsa") : pathexpand(var.private_key_path)
  public_ssh_key_path  = var.generate_ssh_pair ? pathexpand("~/.ssh/id_rsa.pub") : pathexpand(var.public_key_path)
}

resource "oci_core_network_security_group" "int_lb_ingress_security_group" {
  compartment_id = var.compartment_ocid
  display_name   = "${random_string.deploy_id.result}-IntLBIngress"
  vcn_id         = module.oci_oke.vcn_id
}

# INGRESS
resource "oci_core_network_security_group_security_rule" "int_lb_ingress_security_group_rule_http" {
  network_security_group_id = oci_core_network_security_group.int_lb_ingress_security_group.id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = lookup(var.network_cidrs, "APIGW-SUBNET-REGIONAL-CIDR")
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 80
      min = 80
    }
  }
}

resource "oci_core_network_security_group_security_rule" "int_lb_ingress_security_group_rule_https" {
  network_security_group_id = oci_core_network_security_group.int_lb_ingress_security_group.id
  direction                 = "INGRESS"
  protocol                  = "6"
  source                    = lookup(var.network_cidrs, "APIGW-SUBNET-REGIONAL-CIDR")
  source_type               = "CIDR_BLOCK"
  tcp_options {
    destination_port_range {
      max = 443
      min = 443
    }
  }
}