resource "oci_logging_log_group" "fn_logging_group" {
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.fn_logging_group_name}"
  description    = "${var.app_name} Fn logging group"
}

resource "oci_logging_log" "fn_log" {
  display_name = "${var.label_prefix}-Fn-log"
  log_group_id = oci_logging_log_group.fn_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_invoke
      resource    = var.email_delivery_application_id
      service     = local.service_functions
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

locals {
  fn_logging_group_name = "fn-logging-group"
  category_invoke       = "invoke"
  service_functions     = "functions"
}