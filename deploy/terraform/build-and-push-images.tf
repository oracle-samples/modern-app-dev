data "oci_objectstorage_namespace" "objectstorage_namespace" {
  compartment_id = var.compartment_ocid
}

data "oci_identity_regions" "region" {
  filter {
    name   = "name"
    values = [var.region]
  }
}
data "oci_identity_user" "user" {
  user_id = var.current_user_ocid
}
resource "null_resource" "build-and-push-images" {
  provisioner "local-exec" {
    command = "chmod +x scripts/build_and_push_images.sh && bash scripts/build_and_push_images.sh"
    environment = {
      REGION_CODE = lower(data.oci_identity_regions.region.regions[0].key)
      NAMESPACE   = data.oci_objectstorage_namespace.objectstorage_namespace.namespace
      USERNAME    = data.oci_identity_user.user.name
      AUTHTOKEN   = var.auth_token
      IMAGE_TAG   = local.image_tag
      DEPLOY_ID   = random_string.deploy_id.result
    }
  }
  depends_on = [oci_artifacts_container_repository.notification_container_repository]
}