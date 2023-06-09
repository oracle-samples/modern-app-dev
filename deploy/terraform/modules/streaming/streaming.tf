resource "oci_streaming_stream_pool" "uho_streampool" {
  compartment_id = var.compartment_id
  name           = "${var.uho_streampool_name}-${var.label_prefix}"
  freeform_tags  = { "Category" = "uho-events-and-messages" }
}

resource "oci_streaming_stream" "appointment_messages" {
  name               = "${var.appointment_messages_stream_name}-${var.label_prefix}"
  partitions         = var.stream_partitions
  retention_in_hours = var.stream_retention_in_hours
  stream_pool_id     = oci_streaming_stream_pool.uho_streampool.id
}

resource "oci_streaming_stream" "patient_messages" {
  name               = "${var.patient_messages_stream_name}-${var.label_prefix}"
  partitions         = var.stream_partitions
  retention_in_hours = var.stream_retention_in_hours
  stream_pool_id     = oci_streaming_stream_pool.uho_streampool.id
}

resource "oci_streaming_stream" "encounter_messages" {
  name               = "${var.encounter_messages_stream_name}-${var.label_prefix}"
  partitions         = var.stream_partitions
  retention_in_hours = var.stream_retention_in_hours
  stream_pool_id     = oci_streaming_stream_pool.uho_streampool.id
}

resource "oci_streaming_stream" "followup_messages" {
  name               = "${var.followup_messages_stream_name}-${var.label_prefix}"
  partitions         = var.stream_partitions
  retention_in_hours = var.stream_retention_in_hours
  stream_pool_id     = oci_streaming_stream_pool.uho_streampool.id
}

resource "oci_streaming_stream" "feedback_messages" {
  name               = "${var.feedback_messages_stream_name}-${var.label_prefix}"
  partitions         = var.stream_partitions
  retention_in_hours = var.stream_retention_in_hours
  stream_pool_id     = oci_streaming_stream_pool.uho_streampool.id
}
