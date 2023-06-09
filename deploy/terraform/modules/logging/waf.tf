resource "oci_logging_log_group" "waf_logging_group" {
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.waf_logging_group_name}"
  description    = "${var.app_name} WAF logging group"
}

resource "oci_logging_log" "waf_log" {
  display_name = "${var.label_prefix}-WAF-log"
  log_group_id = oci_logging_log_group.waf_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = local.category_all_logs
      resource    = var.waf_id
      service     = local.service_waf
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
  count      = var.enable_waf ? 1 : 0
}

locals {
  waf_logging_group_name = "waf-logging-group"
  category_all_logs      = "all"
  service_waf            = "waf"
}