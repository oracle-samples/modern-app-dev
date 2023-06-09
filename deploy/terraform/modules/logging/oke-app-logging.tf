resource "oci_logging_log_group" "oke_logging_group" {
  #Required
  compartment_id = var.compartment_id
  display_name   = "${local.oke_app_logging_group_name}-${var.label_prefix}"
  description    = "${var.app_name} OKE application logging group"
}
resource "oci_logging_log" "oke_log" {
  display_name       = "${local.oke_app_log_name}-${var.label_prefix}"
  log_group_id       = oci_logging_log_group.oke_logging_group.id
  log_type           = local.oke_log_type
  is_enabled         = true
  retention_duration = 30
}

resource "oci_logging_unified_agent_configuration" "custom_unified_agent_configuration" {
  #Required
  compartment_id = var.compartment_id
  is_enabled     = true
  description    = "${var.app_name} Agent configuration for oke application log "
  display_name   = "${local.oke_app_logging_agent_name}-${var.label_prefix}"

  service_configuration {

    configuration_type = local.oke_config_type

    sources {
      source_type = local.oke_source_type
      channels    = [local.oke_input_type]
      name        = local.oke_input_name
      paths       = [local.oke_log_file_path]

      parser {
        #Required
        parser_type = "NONE"
      }
    }
    destination {
      log_object_id = oci_logging_log.oke_log.id
    }
  }
  group_association {
    group_list = [var.dynamic_group_id]
  }
}

locals {
  oke_logging_group_name     = "oke-logging-group"
  oke_logging_policy_name    = "oke-logging-policy"
  oke_app_logging_group_name = "oke-app-logging-group"
  oke_app_log_name           = "oke-app-log"
  oke_app_logging_agent_name = "oke-app-logging-agent"

  oke_log_type      = "CUSTOM"
  oke_config_type   = "LOGGING"
  oke_source_type   = "LOG_TAIL"
  oke_input_type    = "LOG_PATH"
  oke_input_name    = "oke_pod_logs"
  oke_log_file_path = "/var/log/pods/*/*/*.log"
}

