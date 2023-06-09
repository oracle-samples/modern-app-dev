output "patient_messages_stream_id" {
  value = oci_streaming_stream.patient_messages.id
}

output "patient_messages_stream_name" {
  value = oci_streaming_stream.patient_messages.name
}

output "appointment_messages_stream_id" {
  value = oci_streaming_stream.appointment_messages.id
}

output "appointment_messages_stream_name" {
  value = oci_streaming_stream.appointment_messages.name
}

output "encounter_messages_stream_id" {
  value = oci_streaming_stream.encounter_messages.id
}

output "encounter_messages_stream_name" {
  value = oci_streaming_stream.encounter_messages.name
}

output "feedback_messages_stream_name" {
  value = oci_streaming_stream.feedback_messages.name
}

output "feedback_messages_stream_id" {
  value = oci_streaming_stream.feedback_messages.id
}

output "followup_messages_stream_name" {
  value = oci_streaming_stream.followup_messages.name
}

output "followup_messages_stream_id" {
  value = oci_streaming_stream.followup_messages.id
}

output "bootstrap_servers" {
  value = oci_streaming_stream_pool.uho_streampool.kafka_settings[0].bootstrap_servers
}

output "endpoint" {
  value = "https://${oci_streaming_stream_pool.uho_streampool.endpoint_fqdn}"
}

output "streaming_user_name" {
  value = data.oci_identity_user.current_user.name
}

output "streampool_id" {
  value = oci_streaming_stream_pool.uho_streampool.id
}

output "dynamic_group_name" {
  value = oci_identity_dynamic_group.uho_dynamic_group.name
}

output "dynamic_group_id" {
  value = oci_identity_dynamic_group.uho_dynamic_group.id
}
