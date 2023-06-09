output "data_upload_endpoint" {
  value = oci_apm_apm_domain.uho_apm_domain.data_upload_endpoint
}

output "public_data_key" {
  value = data.oci_apm_data_keys.uho_public_data_keys.data_keys.0.value
}

output "private_data_key" {
  value = data.oci_apm_data_keys.uho_private_data_keys.data_keys.0.value
}