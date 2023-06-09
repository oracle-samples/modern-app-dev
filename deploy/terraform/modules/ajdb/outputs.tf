output "admin_password" {
  value = random_string.autonomous_json_database_admin_password.result
}

output "wallet_password" {
  value = random_string.autonomous_json_database_wallet_password.result
}

output "wallet_zip_content" {
  value = oci_database_autonomous_database_wallet.autonomous_json_database_wallet.content
}

output "db_name" {
  value = oci_database_autonomous_database.uho_autonomous_json_database.db_name
}

output "ajdb_nsg_id" {
  value = oci_core_network_security_group.ajdb_security_group.id
}

output "db_id" {
  value = oci_database_autonomous_database.uho_autonomous_json_database.id
}

output "ajdb_endpoint_subnet_id" {
  value = oci_core_subnet.ajdb_endpoint_subnet.id
}