resource "oci_artifacts_container_repository" "patient_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-patient-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.provider_container_repository]
}

resource "oci_artifacts_container_repository" "provider_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-provider-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
}

resource "oci_artifacts_container_repository" "appointment_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-appointment-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.patient_container_repository]
}

resource "oci_artifacts_container_repository" "encounter_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-encounter-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.appointment_container_repository]
}

resource "oci_artifacts_container_repository" "feedback_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-feedback-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.encounter_container_repository]
}

resource "oci_artifacts_container_repository" "followup_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-followup-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.feedback_container_repository]
}

resource "oci_artifacts_container_repository" "frontend_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-frontend-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.followup_container_repository]
}

resource "oci_artifacts_container_repository" "notification_container_repository" {
  compartment_id = var.compartment_ocid
  display_name   = "uho-notification-${random_string.deploy_id.result}"
  is_public      = var.container_repository_is_public
  depends_on     = [oci_artifacts_container_repository.frontend_container_repository]
}