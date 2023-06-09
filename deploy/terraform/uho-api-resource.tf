resource "oci_apigateway_api" "patient_api" {

  compartment_id = var.compartment_ocid
  content        = file("${path.module}/specs/Patient.yml")
  display_name   = "Patient"
}
resource "oci_apigateway_api" "provider_api" {

  compartment_id = var.compartment_ocid
  content        = file("${path.module}/specs/Provider.yml")
  display_name   = "Provider"
}
resource "oci_apigateway_api" "appointment_api" {

  compartment_id = var.compartment_ocid
  content        = file("${path.module}/specs/Appointment.yml")
  display_name   = "Appointment"
}
resource "oci_apigateway_api" "encounter_api" {

  compartment_id = var.compartment_ocid
  content        = file("${path.module}/specs/Encounter.yml")
  display_name   = "Encounter"
}