resource "oci_devops_build_pipeline_stage" "provider_build_pipeline_build_stage" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["provider"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline.uho_build_pipeline["provider"].id
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
  description                        = "Provider build stage"
  build_spec_file                    = local.provider_build_spec_file_path
  display_name                       = local.provider_build_pipeline_build_stage_name
  image                              = local.build_pipeline_stage_image
  stage_execution_timeout_in_seconds = local.build_stage_timeout_in_seconds
  depends_on                         = [oci_devops_build_pipeline.uho_build_pipeline["provider"]]
}

resource "oci_devops_deploy_artifact" "provider_docker_image_artifact" {
  argument_substitution_mode = local.argument_substitution_mode
  deploy_artifact_type       = "DOCKER_IMAGE"
  project_id                 = oci_devops_project.uho_project.id

  deploy_artifact_source {
    deploy_artifact_source_type = "OCIR"
    image_digest                = "provider"
    image_uri                   = local.provider_deploy_artifact_source_image_uri
  }
  display_name = "provider-artifact-docker-image"
  depends_on   = [oci_devops_build_pipeline_stage.provider_build_pipeline_build_stage]
}

resource "oci_devops_build_pipeline_stage" "provider_artifact_deliver_stage" {
  #Required
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["provider"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.provider_build_pipeline_build_stage.id
    }
  }
  build_pipeline_stage_type = "DELIVER_ARTIFACT"

  deliver_artifact_collection {

    items {
      artifact_id   = oci_devops_deploy_artifact.provider_docker_image_artifact.id
      artifact_name = local.provider_artifact_name
    }
  }
  display_name = local.provider_build_pipeline_deliver_artifacts_stage_name
  depends_on   = [oci_devops_deploy_artifact.provider_docker_image_artifact]
}

resource "oci_devops_build_pipeline_stage" "provider_build_stage_trigger_deploy_pipeline" {
  build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline["provider"].id
  build_pipeline_stage_predecessor_collection {
    items {
      id = oci_devops_build_pipeline_stage.provider_artifact_deliver_stage.id
    }
  }
  build_pipeline_stage_type      = "TRIGGER_DEPLOYMENT_PIPELINE"
  deploy_pipeline_id             = oci_devops_deploy_pipeline.provider_deploy_pipeline.id
  display_name                   = "provider-build-stage-trigger-deploy-pipeline"
  is_pass_all_parameters_enabled = true
  depends_on                     = [oci_devops_build_pipeline_stage.provider_artifact_deliver_stage]
}
resource "oci_devops_deploy_pipeline" "provider_deploy_pipeline" {
  project_id   = oci_devops_project.uho_project.id
  display_name = local.provider_deploy_pipeline_name
  depends_on   = [var.notification_container_repository_name]
}

resource "oci_devops_deploy_artifact" "provider_deploy_generic_artifact" {
  argument_substitution_mode = local.argument_substitution_mode
  deploy_artifact_type       = "DEPLOYMENT_SPEC"
  project_id                 = oci_devops_project.uho_project.id
  display_name               = "provider-deploy-spec"

  deploy_artifact_source {
    deploy_artifact_source_type = local.deploy_artifact_source_type
    base64encoded_content = templatefile("${path.module}/deployment/provider.yaml", {
      docker_repository = local.ocir_docker_repository,
      namespace         = data.oci_objectstorage_namespace.objectstorage_namespace.namespace,
      image             = var.provider_container_repository_name,
      cluster-id        = var.cluster_id,
      region            = var.region,
      hash              = "$${BUILDRUN_HASH}"
    })
  }
  depends_on = [oci_devops_deploy_pipeline.provider_deploy_pipeline]
}

resource "oci_devops_deploy_stage" "provider_deploy_pipeline_stage" {
  deploy_pipeline_id = oci_devops_deploy_pipeline.provider_deploy_pipeline.id
  deploy_stage_predecessor_collection {
    items {
      id = oci_devops_deploy_pipeline.provider_deploy_pipeline.id
    }
  }
  deploy_stage_type                            = "COMPUTE_INSTANCE_GROUP_ROLLING_DEPLOYMENT"
  display_name                                 = "provider-deploy-oke-stage"
  deployment_spec_deploy_artifact_id           = oci_devops_deploy_artifact.provider_deploy_generic_artifact.id
  compute_instance_group_deploy_environment_id = oci_devops_deploy_environment.operator_instance_environment.id
  rollout_policy {
    batch_percentage = 100
    policy_type      = "COMPUTE_INSTANCE_GROUP_LINEAR_ROLLOUT_POLICY_BY_PERCENTAGE"
  }
  rollback_policy {
    policy_type = "AUTOMATED_STAGE_ROLLBACK_POLICY"
  }
  depends_on = [oci_devops_deploy_artifact.provider_deploy_generic_artifact, oci_devops_deploy_environment.operator_instance_environment]
}

locals {
  provider_artifact_name                               = "provider-artifact"
  provider_build_pipeline_deliver_artifacts_stage_name = "provider-deliver-artifacts"
  provider_deploy_pipeline_name                        = "provider-deploy-pipeline"
  provider_build_pipeline_build_stage_name             = "provider-build-stage"
  provider_build_spec_file_path                        = "src/provider/build_spec.yaml"
}