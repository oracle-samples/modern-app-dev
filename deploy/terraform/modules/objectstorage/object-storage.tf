data "oci_objectstorage_namespace" "objectstorage_namespace" {
  compartment_id = var.compartment_id
}
resource "oci_objectstorage_bucket" "uho_object_storage" {
  compartment_id        = var.compartment_id
  name                  = "${var.bucket_name}-${var.label_prefix}"
  namespace             = data.oci_objectstorage_namespace.objectstorage_namespace.namespace
  access_type           = var.object_storage_access_type
  kms_key_id            = var.kms_key_id
  object_events_enabled = true

  lifecycle {
    ignore_changes = [namespace]
  }
  provisioner "local-exec" {
    when    = destroy
    command = "chmod +x scripts/destroy_object_storage_objects.sh && bash scripts/destroy_object_storage_objects.sh"
    environment = {
      BUCKET_NAME       = self.name
    }
  }
}

resource "oci_identity_policy" "uho_dynamic_policy" {
  name           = "OSPolicy-${var.label_prefix}"
  description    = "Policy for object storage management"
  compartment_id = var.compartment_id
  provider       = oci.home
  statements = [
    "Allow any-user to manage buckets in compartment id ${var.compartment_id} where ANY {instance.compartment.id = '${var.compartment_id}'}",
    "Allow any-user to manage object-family in compartment id ${var.compartment_id} where ANY {instance.compartment.id = '${var.compartment_id}'}",
    "Allow service objectstorage-${var.region} to use keys in compartment id ${var.compartment_id} where target.key.id = '${var.kms_key_id}'",
    "Allow any-user to manage objects in compartment id ${var.compartment_id}"
  ]
}