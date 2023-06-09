resource "oci_logging_log_group" "apigw_logging_group" {
  #Required
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.apigw_logging_group_name}"
  description    = "${var.app_name} APIGW logging group"
}

resource "oci_logging_log" "frontend_deployment_log" {
  display_name = "${var.label_prefix}-Frontend-service-deployment"
  log_group_id = oci_logging_log_group.apigw_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_access
      resource    = var.deployment.frontend.id
      service     = local.service_api_gateway
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

resource "oci_logging_log" "appointment_deployment_log" {
  display_name = "${var.label_prefix}-Appointment-service-deployment"
  log_group_id = oci_logging_log_group.apigw_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_access
      resource    = var.deployment.appointment.id
      service     = local.service_api_gateway
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

resource "oci_logging_log" "encounter_deployment_log" {
  display_name = "${var.label_prefix}-Encounter-service-deployment"
  log_group_id = oci_logging_log_group.apigw_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_access
      resource    = var.deployment.encounter.id
      service     = local.service_api_gateway
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

resource "oci_logging_log" "patient_deployment_log" {
  display_name = "${var.label_prefix}-Patient-service-deployment"
  log_group_id = oci_logging_log_group.apigw_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_access
      resource    = var.deployment.patient.id
      service     = local.service_api_gateway
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

resource "oci_logging_log" "provider_deployment_log" {
  display_name = "${var.label_prefix}-Provider-service-deployment"
  log_group_id = oci_logging_log_group.apigw_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_access
      resource    = var.deployment.provider.id
      service     = local.service_api_gateway
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

locals {
  apigw_logging_group_name = "apigw-logging-group"
  log_type_service         = "SERVICE"
  source_type              = "OCISERVICE"
  service_api_gateway      = "apigateway"
  category_access          = "access"
  is_log_enabled           = true
}