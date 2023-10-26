data "oci_identity_regions" "region" {
  filter {
    name   = "name"
    values = [var.region]
  }
}

data "oci_objectstorage_namespace" "objectstorage_namespace" {
  compartment_id = var.compartment_id
}

resource "oci_devops_project" "uho_project" {
  compartment_id = var.compartment_id
  name           = "${var.devops_project_name}-${var.label_prefix}"
  notification_config {
    topic_id = oci_ons_notification_topic.devops_topic.id
  }
  depends_on = [oci_ons_notification_topic.devops_topic]
}

resource "oci_ons_notification_topic" "devops_topic" {
  compartment_id = var.compartment_id
  name           = "${var.devops_notification_topic_name}-${var.label_prefix}"
}

resource "oci_devops_connection" "uho_external_connection" {
  connection_type = "GITHUB_ACCESS_TOKEN"
  project_id      = oci_devops_project.uho_project.id
  access_token    = var.uho_devops_github_pat_secret
  display_name    = "uho-devops-github-${var.label_prefix}"
  depends_on      = [oci_devops_project.uho_project]
  count           = var.create_external_connection ? 1 : 0
}

resource "oci_devops_repository" "uho_repository" {
  name            = local.uho_repository_name
  project_id      = oci_devops_project.uho_project.id
  default_branch  = local.uho_repository_default_branch
  repository_type = local.repository_type
  depends_on      = [oci_devops_project.uho_project, data.oci_objectstorage_namespace.objectstorage_namespace, data.oci_identity_regions.region]
  count           = var.create_external_connection ? 0 : 1
}

resource "oci_devops_repository" "uho_github_repository" {
  name            = local.uho_repository_name
  project_id      = oci_devops_project.uho_project.id
  repository_type = "MIRRORED"
  default_branch  = "master"
  mirror_repository_config {
    connector_id   = oci_devops_connection.uho_external_connection[0].id
    repository_url = var.github_repo_url
    trigger_schedule {
      schedule_type = "DEFAULT"
    }
  }
  depends_on = [oci_devops_connection.uho_external_connection, data.oci_objectstorage_namespace.objectstorage_namespace, data.oci_identity_regions.region]
  count      = var.create_external_connection ? 1 : 0
}

resource "oci_devops_deploy_environment" "operator_instance_environment" {
  deploy_environment_type = "COMPUTE_INSTANCE_GROUP"
  project_id              = oci_devops_project.uho_project.id

  compute_instance_group_selectors {
    items {
      selector_type = "INSTANCE_IDS"
      compute_instance_ids = [
        data.oci_core_instances.operator_instances.instances.0.id
      ]
    }
  }
  description  = "Operator of private OKE environment"
  display_name = "oke-operator-environment"
  depends_on   = [oci_devops_project.uho_project, data.oci_core_instances.operator_instances]
}

data "oci_core_instances" "operator_instances" {
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-operator"
}

resource "oci_logging_log_group" "devops_log_group" {
  compartment_id = var.compartment_id
  display_name   = "devops-log-group-${var.label_prefix}"
  depends_on     = [oci_devops_project.uho_project]
}

resource "oci_logging_log" "devops_log" {
  #Required
  display_name = "devops-log-group-log-${var.label_prefix}"
  log_group_id = oci_logging_log_group.devops_log_group.id
  log_type     = "SERVICE"

  configuration {
    source {
      category    = "all"
      resource    = oci_devops_project.uho_project.id
      service     = "devops"
      source_type = "OCISERVICE"
    }
    compartment_id = var.compartment_id
  }
  is_enabled = true
  depends_on = [oci_logging_log_group.devops_log_group]
}

resource "oci_devops_deploy_environment" "function_environment" {
  deploy_environment_type = "FUNCTION"
  project_id              = oci_devops_project.uho_project.id
  function_id             = var.email_delivery_function_id

  description  = "Notification function environment"
  display_name = "notification-function-environment"
  depends_on   = [oci_devops_project.uho_project]
}

resource "oci_devops_build_pipeline" "uho_build_pipeline" {
  for_each = toset(local.projects)
  project_id   = oci_devops_project.uho_project.id
  display_name = "uho-build-pipeline-${each.key}"

  build_pipeline_parameters {
    items {
      name          = "VAULT_ID"
      default_value = var.vault_id
    }
    items {
      name          = "APIGW_URL"
      default_value = var.apigw_url
    }
    items {
      name          = "IDCS_URL"
      default_value = var.idcs_url
    }
    items {
      name          = "SECRET_NAME"
      default_value = var.secret_name
    }
    items {
      name          = "DEVOPS_PROJECT_ID"
      default_value = oci_devops_project.uho_project.id
    }
  }
}

locals {
  uho_repository_name                           = "uho-repo"
  uho_repository_default_branch                 = "master"
  repository_type                               = "HOSTED"
  uho_repo_connection_type                      = "DEVOPS_CODE_REPOSITORY"
  argument_substitution_mode                    = "SUBSTITUTE_PLACEHOLDERS"
  patient_deploy_artifact_source_image_uri      = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.patient_container_repository_name}:$${BUILDRUN_HASH}"
  provider_deploy_artifact_source_image_uri     = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.provider_container_repository_name}:$${BUILDRUN_HASH}"
  appointment_deploy_artifact_source_image_uri  = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.appointment_container_repository_name}:$${BUILDRUN_HASH}"
  encounter_deploy_artifact_source_image_uri    = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.encounter_container_repository_name}:$${BUILDRUN_HASH}"
  feedback_deploy_artifact_source_image_uri     = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.feedback_container_repository_name}:$${BUILDRUN_HASH}"
  followup_deploy_artifact_source_image_uri     = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.followup_container_repository_name}:$${BUILDRUN_HASH}"
  frontend_deploy_artifact_source_image_uri     = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.frontend_container_repository_name}:$${BUILDRUN_HASH}"
  notification_deploy_artifact_source_image_uri = "${local.ocir_docker_repository}/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/${var.notification_container_repository_name}:$${BUILDRUN_HASH}"
  uho_repo_url                                  = "https://devops.scmservice.${var.region}.oci.oraclecloud.com/namespaces/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/projects/${oci_devops_project.uho_project.name}/repositories/${var.create_external_connection ? oci_devops_repository.uho_github_repository[0].name : oci_devops_repository.uho_repository[0].name}"
  uho_build_trigger_source                      = "DEVOPS_CODE_REPOSITORY"
  build_pipeline_stage_image                    = var.build_pipeline_stage_image
  deploy_artifact_source_type                   = "INLINE"
  ocir_docker_repository                        = "${lower(data.oci_identity_regions.region.regions[0].key)}.ocir.io"
  build_source_name                             = "uho_build_source"
  build_stage_timeout_in_seconds                = "1800"
  uho_repository_build_pipeline_branch          = var.create_external_connection ? var.github_branch_name : "master"
  projects                                      = ["appointment","encounter","feedback","followup","frontend","notification","patient","provider"]
}