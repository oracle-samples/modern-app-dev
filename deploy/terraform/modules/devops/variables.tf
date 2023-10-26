variable "compartment_id" {
  type = string
}

variable "tenancy_id" {
  type = string
}

variable "label_prefix" {
  type = string
}

variable "devops_project_name" {
  type = string
}

variable "build_pipeline_stage_image" {
  default = "OL7_X86_64_STANDARD_10"
  type    = string
}

variable "devops_notification_topic_name" {
  default = "uho-devops"
  type    = string
}

variable "container_repository_is_public" {
  default = true
  type    = bool
}

variable "cluster_id" {
  type = string
}

variable "region" {
  type = string
}

variable "email_delivery_function_id" {
  type = string
}

variable "smtp_username" {
  type = string
}

variable "smtp_password" {
  type = string
}

variable "object_storage_namespace_name" {
  type = string
}

variable "object_storage_bucket_name" {
  type = string
}

variable "appointment_container_repository_name" {
  type = string
}

variable "provider_container_repository_name" {
  type = string
}

variable "patient_container_repository_name" {
  type = string
}

variable "encounter_container_repository_name" {
  type = string
}

variable "frontend_container_repository_name" {
  type = string
}

variable "feedback_container_repository_name" {
  type = string
}

variable "followup_container_repository_name" {
  type = string
}

variable "notification_container_repository_name" {
  type = string
}

variable "vault_id" {
  type = string
}
variable "apigw_url" {
  type = string
}

variable "idcs_url" {
  type = string
}

variable "secret_name" {
  type = string
}

variable "uho_devops_github_pat_secret" {
  default = ""
  type    = string
}

variable "github_repo_url" {
  default = ""
  type    = string
}

variable "github_branch_name" {
  default = "master"
  type    = string
}

variable "create_external_connection" {
  default = "false"
  type    = string
}