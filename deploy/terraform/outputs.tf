output "autonomous_database_password" {
  value     = module.atp.admin_password
  sensitive = true
}

output "autonomous_json_database_password" {
  value     = module.ajdb.admin_password
  sensitive = true
}

output "patient_username" {
  value = "${random_string.deploy_id.result}_patient"
}

output "provider_username" {
  value = "${random_string.deploy_id.result}_provider"
}

output "patient_password" {
  value     = "UH01_patient"
  # sensitive = true
}

output "provider_password" {
  value     = "UH01_provider"
  # sensitive = true
}

output "private_ssh_key" {
  value     = local.private_ssh_key
  sensitive = true
}

output "public_ssh_key" {
  value     = local.public_ssh_key
  sensitive = true
}

output "uho_url" {
  value = "${module.api_gateway.apigw_url}/home/"
}
