########################################################################
################## VNC (subnets) logs ##################################
########################################################################

resource "oci_logging_log_group" "vcn_logging_group" {
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.vcn_logging_group_name}"
  description    = "${var.app_name} VCN logging group"
}

data "oci_core_vcns" "uho_vcns" {
  compartment_id = var.compartment_id

  display_name = "${var.label_prefix}-${var.app_name}-VCN"
}

###########################################################
################## OKE subnets logs #######################
###########################################################

data "oci_core_subnets" "oke_control_plane_subnet" {
  compartment_id = var.compartment_id

  display_name = "${var.label_prefix}-control-plane"
  vcn_id       = data.oci_core_vcns.uho_vcns.virtual_networks[0].id
}

resource "oci_logging_log" "oke_control_plane_subnet_log" {
  display_name = "${var.label_prefix}-oke-control-plane"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = lookup(var.oke_subnet_ids, "cp")
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

data "oci_core_subnets" "oke_operator_subnet" {
  compartment_id = var.compartment_id

  display_name = "${var.label_prefix}-operator"
  vcn_id       = data.oci_core_vcns.uho_vcns.virtual_networks[0].id
}

resource "oci_logging_log" "oke_operator_subnet_log" {
  display_name = "${var.label_prefix}-oke-operator"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = data.oci_core_subnets.oke_operator_subnet.subnets.0.id
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

data "oci_core_subnets" "oke_int_lb_subnet" {
  compartment_id = var.compartment_id

  display_name = "${var.label_prefix}-int_lb"
  vcn_id       = data.oci_core_vcns.uho_vcns.virtual_networks[0].id
}

resource "oci_logging_log" "oke_int_lb_subnet_log" {
  display_name = "${var.label_prefix}-oke-int_lb"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = lookup(var.oke_subnet_ids, "int_lb")
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

data "oci_core_subnets" "oke_workers_subnet" {
  compartment_id = var.compartment_id

  display_name = "${var.label_prefix}-workers"
  vcn_id       = data.oci_core_vcns.uho_vcns.virtual_networks[0].id
}

resource "oci_logging_log" "oke_workers_subnet_log" {
  display_name = "${var.label_prefix}-oke-workers"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = lookup(var.oke_subnet_ids, "workers")
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

###########################################################
################### APIGW subnet logs #####################
###########################################################

resource "oci_logging_log" "oke_apigw_subnet_log" {
  display_name = "${var.label_prefix}-apigw"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = var.apigw_subnet_id
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

###########################################################
################# Functions subnet logs ###################
###########################################################

resource "oci_logging_log" "oke_functions_subnet_log" {
  display_name = "${var.label_prefix}-functions"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = var.functions_subnet_id
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

###########################################################
#################### ADB subnet logs ######################
###########################################################

resource "oci_logging_log" "adb_endpoint_subnet_log" {
  display_name = "${var.label_prefix}-adb"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = var.adb_endpoint_subnet_id
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

###########################################################
#################### AJDB subnet logs #####################
###########################################################

resource "oci_logging_log" "ajdb_endpoint_subnet_log" {
  display_name = "${var.label_prefix}-ajdb"
  log_group_id = oci_logging_log_group.vcn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all
      resource    = var.ajdb_endpoint_subnet_id
      service     = local.service_flow_logs
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

locals {
  vcn_logging_group_name = "vcn-logging-group"
  category_all           = "all"
  service_flow_logs      = "flowlogs"
}