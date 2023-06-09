output "admin_password" {
  value = random_string.autonomous_database_admin_password.result
}

output "wallet_password" {
  value = random_string.autonomous_database_wallet_password.result
}

output "wallet_zip_content" {
  value = oci_database_autonomous_database_wallet.autonomous_database_wallet.content
}

output "db_name" {
  value = oci_database_autonomous_database.uho_autonomous_database.db_name
}

output "db_id" {
  value = oci_database_autonomous_database.uho_autonomous_database.id
}

output "atp_nsg_id" {
  value = oci_core_network_security_group.adb_security_group.id
}

output "adb_endpoint_subnet_id" {
  value = oci_core_subnet.adb_endpoint_subnet.id
}