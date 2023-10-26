resource "oci_devops_trigger" "uho_master_push_trigger" {
  for_each = toset(local.projects)
  actions {
    build_pipeline_id = oci_devops_build_pipeline.uho_build_pipeline[each.value].id
    type              = "TRIGGER_BUILD_PIPELINE"

    filter {
      trigger_source = local.uho_build_trigger_source
      events         = ["PUSH"]
      include {
        head_ref = local.uho_repository_build_pipeline_branch
        file_filter {
          file_paths = ["src/${each.value}/**"]
        }
      }
    }
  }
  project_id     = oci_devops_project.uho_project.id
  trigger_source = local.uho_build_trigger_source

  display_name  = "uho-code-push-trigger-${each.value}"
  repository_id = var.create_external_connection ? oci_devops_repository.uho_github_repository[0].id : oci_devops_repository.uho_repository[0].id
}
