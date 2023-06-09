output "repositories" {
  value = [var.patient_container_repository_name, var.provider_container_repository_name, var.appointment_container_repository_name, var.frontend_container_repository_name]
}
output "build_pipeline_id" {
  value = oci_devops_build_pipeline.uho_build_pipeline.id
}