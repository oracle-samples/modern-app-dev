output "apigw_url" {
  value = "https://${oci_apigateway_gateway.api_gateway.hostname}"
}

output "deployment" {
  value = {
    frontend    = oci_apigateway_deployment.home
    appointment = oci_apigateway_deployment.appointment
    encounter   = oci_apigateway_deployment.encounter
    patient     = oci_apigateway_deployment.patient
    provider    = oci_apigateway_deployment.provider
  }
}

output "apigw_subnet_id" {
  value = oci_core_subnet.apigw_subnet.id
}

output "idcs_key_content" {
  value = data.oci_objectstorage_object.idcs_key_file.content
}