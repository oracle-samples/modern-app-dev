resource "oci_devops_build_pipeline_stage" "notification_build_pipeline_build_stage" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["notification"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline.uho_build_pipeline["notification"].id
    }
  }
  build_pipeline_stage_type = "BUILD"

  build_source_collection {

    items {
      connection_type = local.uho_repo_connection_type
      branch          = local.uho_repository_build_pipeline_branch
      name            = local.build_source_name
      repository_id   = var.create_external_connection ? oci_devops_repository.uho_github_repository[0].id : oci_devops_repository.uho_repository[0].id
      repository_url  = local.uho_repo_url
    }
  }
  description                        = "Notification build stage"
  build_spec_file                    = local.notification_build_spec_file_path
  display_name                       = local.notification_build_pipeline_build_stage_name
  image                              = local.build_pipeline_stage_image
  stage_execution_timeout_in_seconds = local.build_stage_timeout_in_seconds
  depends_on                         = [oci_devops_build_pipeline.uho_build_pipeline["notification"]]
}

resource "oci_devops_deploy_artifact" "notification_docker_image_artifact" {
  argument_substitution_mode = local.argument_substitution_mode
  deploy_artifact_type       = "DOCKER_IMAGE"
  project_id                 = oci_devops_project.uho_project.id

  deploy_artifact_source {
    deploy_artifact_source_type = "OCIR"
    image_uri                   = local.notification_deploy_artifact_source_image_uri
  }
  display_name = "notification-artifact-docker-image"
  depends_on   = [oci_devops_deploy_pipeline.notification_deploy_pipeline]
}

resource "oci_devops_build_pipeline_stage" "notification_artifact_deliver_stage" {

  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["notification"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.notification_build_pipeline_build_stage.id
    }
  }
  build_pipeline_stage_type = "DELIVER_ARTIFACT"

  deliver_artifact_collection {

    items {
      artifact_id   = oci_devops_deploy_artifact.notification_docker_image_artifact.id
      artifact_name = local.notification_artifact_name
    }
  }
  display_name = local.notification_build_pipeline_deliver_artifacts_stage_name
  depends_on   = [oci_devops_build_pipeline_stage.notification_build_pipeline_build_stage]
}

resource "oci_devops_build_pipeline_stage" "notification_build_stage_trigger_deploy_pipeline" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["notification"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.notification_artifact_deliver_stage.id
    }
  }
  build_pipeline_stage_type      = "TRIGGER_DEPLOYMENT_PIPELINE"
  deploy_pipeline_id             = oci_devops_deploy_pipeline.notification_deploy_pipeline.id
  display_name                   = "notification-build-stage-trigger-deploy-pipeline"
  is_pass_all_parameters_enabled = true
  depends_on                     = [oci_devops_build_pipeline_stage.notification_artifact_deliver_stage]
}

## Deploy pipieline

resource "oci_devops_deploy_pipeline" "notification_deploy_pipeline" {
  project_id   = oci_devops_project.uho_project.id
  display_name = local.notification_deploy_pipeline_name
  depends_on   = [oci_devops_deploy_stage.frontend_deploy_pipeline_stage]
}

resource "oci_devops_deploy_stage" "notification_deploy_pipeline_stage" {
  deploy_pipeline_id = oci_devops_deploy_pipeline.notification_deploy_pipeline.id
  deploy_stage_predecessor_collection {
    items {
      id = oci_devops_deploy_pipeline.notification_deploy_pipeline.id
    }
  }
  deploy_stage_type               = "DEPLOY_FUNCTION"
  display_name                    = "notification-deploy-function-stage"
  function_deploy_environment_id  = oci_devops_deploy_environment.function_environment.id
  docker_image_deploy_artifact_id = oci_devops_deploy_artifact.notification_docker_image_artifact.id
  config = {
    "SMTP_USERNAME" : var.smtp_username,
    "SMTP_PASSWORD" : var.smtp_password,
    "SMTP_HOST" : "smtp.email.${var.region}.oci.oraclecloud.com",
    "FROM_ADDRESS" : local.sender_email_address,
    "NAMESPACE" : var.object_storage_namespace_name,
    "BUCKET_NAME" : var.object_storage_bucket_name,
    "VAULT_ID" : var.vault_id,
    "COMPARTMENT_ID": var.compartment_id,
    "SECRET_NAME" : var.secret_name,
    "IDCS_URL" : var.idcs_url,
    "APIGW_URL" : var.apigw_url
  }
  depends_on = [oci_devops_deploy_environment.function_environment, oci_devops_deploy_artifact.notification_docker_image_artifact]
}

locals {
  notification_artifact_name                               = "notification-artifact"
  notification_build_pipeline_deliver_artifacts_stage_name = "notification-deliver-artifacts"
  notification_deploy_pipeline_name                        = "notification-deploy-pipeline"
  notification_build_pipeline_build_stage_name             = "notification-build-stage"
  notification_build_spec_file_path                        = "src/notification/build_spec.yaml"
  sender_email_address                                     = "help@uho.com"
}