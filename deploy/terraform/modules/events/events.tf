resource "oci_events_rule" "encounter_object_event_to_streaming_rule" {
  compartment_id = var.compartment_id
  is_enabled     = true
  display_name   = "${local.rule_display_name}-${var.label_prefix}"
  description    = local.rule_description

  condition = "{\"eventType\":\"com.oraclecloud.objectstorage.createobject\",\"data\":{\"additionalDetails\":{\"bucketName\":\"${var.bucket_name}\"}}}"

  actions {
    actions {
      action_type = "OSS"
      is_enabled  = true
      stream_id   = var.stream_id
    }
  }
}

locals {
  rule_display_name = "EncounterObjectEventStreamingRule"
  rule_description  = "Rule to generate events on object create in bucket and send to streaming"
}