resource "oci_devops_build_pipeline_stage" "appointment_build_pipeline_build_stage" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["appointment"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline.uho_build_pipeline["appointment"].id
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
  description                        = "Appointment build stage"
  build_spec_file                    = local.appointment_build_spec_file_path
  display_name                       = local.appointment_build_pipeline_build_stage_name
  image                              = local.build_pipeline_stage_image
  stage_execution_timeout_in_seconds = local.build_stage_timeout_in_seconds
  depends_on                         = [oci_devops_build_pipeline.uho_build_pipeline["appointment"]]
}

resource "oci_devops_deploy_artifact" "appointment_docker_image_artifact" {
  argument_substitution_mode = local.argument_substitution_mode
  deploy_artifact_type       = "DOCKER_IMAGE"
  project_id                 = oci_devops_project.uho_project.id

  deploy_artifact_source {
    deploy_artifact_source_type = "OCIR"
    image_digest                = "appointment"
    image_uri                   = local.appointment_deploy_artifact_source_image_uri
  }
  display_name = "appointment-artifact-docker-image"
  depends_on   = [oci_devops_build_pipeline_stage.appointment_build_pipeline_build_stage]
}

resource "oci_devops_build_pipeline_stage" "appointment_artifact_deliver_stage" {
  #Required
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["appointment"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.appointment_build_pipeline_build_stage.id
    }
  }
  build_pipeline_stage_type = "DELIVER_ARTIFACT"

  deliver_artifact_collection {

    items {
      artifact_id   = oci_devops_deploy_artifact.appointment_docker_image_artifact.id
      artifact_name = local.appointment_artifact_name
    }
  }
  display_name = local.appointment_build_pipeline_deliver_artifacts_stage_name
  depends_on   = [oci_devops_deploy_artifact.appointment_docker_image_artifact]
}

resource "oci_devops_build_pipeline_stage" "appointment_build_stage_trigger_deploy_pipeline" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["appointment"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.appointment_artifact_deliver_stage.id
    }
  }
  build_pipeline_stage_type      = "TRIGGER_DEPLOYMENT_PIPELINE"
  deploy_pipeline_id             = oci_devops_deploy_pipeline.appointment_deploy_pipeline.id
  display_name                   = "appointment-build-stage-trigger-deploy-pipeline"
  is_pass_all_parameters_enabled = true
  depends_on                     = [oci_devops_build_pipeline_stage.appointment_artifact_deliver_stage]
}
resource "oci_devops_deploy_pipeline" "appointment_deploy_pipeline" {
  project_id   = oci_devops_project.uho_project.id
  display_name = local.appointment_deploy_pipeline_name
  depends_on   = [oci_devops_deploy_stage.patient_deploy_pipeline_stage]
}

resource "oci_devops_deploy_artifact" "appointment_deploy_generic_artifact" {
  argument_substitution_mode = local.argument_substitution_mode
  deploy_artifact_type       = "DEPLOYMENT_SPEC"
  project_id                 = oci_devops_project.uho_project.id
  display_name               = "appointment-deploy-spec"

  deploy_artifact_source {
    deploy_artifact_source_type = local.deploy_artifact_source_type
    base64encoded_content = templatefile("${path.module}/deployment/appointment.yaml", {
      docker_repository = local.ocir_docker_repository,
      namespace         = data.oci_objectstorage_namespace.objectstorage_namespace.namespace
      image             = var.appointment_container_repository_name,
      cluster-id        = var.cluster_id,
      region            = var.region,
      hash              = "$${BUILDRUN_HASH}"
    })
  }
  depends_on = [oci_devops_deploy_pipeline.appointment_deploy_pipeline]
}

resource "oci_devops_deploy_stage" "appointment_deploy_pipeline_stage" {
  deploy_pipeline_id = oci_devops_deploy_pipeline.appointment_deploy_pipeline.id
  deploy_stage_predecessor_collection {
    items {
      id = oci_devops_deploy_pipeline.appointment_deploy_pipeline.id
    }
  }
  deploy_stage_type                            = "COMPUTE_INSTANCE_GROUP_ROLLING_DEPLOYMENT"
  display_name                                 = "appointment-deploy-oke-stage"
  deployment_spec_deploy_artifact_id           = oci_devops_deploy_artifact.appointment_deploy_generic_artifact.id
  compute_instance_group_deploy_environment_id = oci_devops_deploy_environment.operator_instance_environment.id
  rollout_policy {
    batch_percentage = 100
    policy_type      = "COMPUTE_INSTANCE_GROUP_LINEAR_ROLLOUT_POLICY_BY_PERCENTAGE"
  }
  rollback_policy {
    policy_type = "AUTOMATED_STAGE_ROLLBACK_POLICY"
  }
  depends_on = [oci_devops_deploy_artifact.appointment_deploy_generic_artifact]
}

locals {
  appointment_artifact_name                               = "appointment-artifact"
  appointment_build_pipeline_deliver_artifacts_stage_name = "appointment-deliver-artifacts"
  appointment_deploy_pipeline_name                        = "appointment-deploy-pipeline"
  appointment_build_pipeline_build_stage_name             = "appointment-build-stage"
  appointment_build_spec_file_path                        = "src/appointment/build_spec.yaml"
}