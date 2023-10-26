output "email_delivery_function_id" {
  value = oci_functions_function.email_delivery_function.id
}

output "email_delivery_application_id" {
  value = oci_functions_application.email_delivery_application.id
}

output "functions_subnet_id" {
  value = oci_core_subnet.functions_subnet.id
}
