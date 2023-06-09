resource "oci_logging_log_group" "object_storage_logging_group" {
  compartment_id = var.compartment_id
  display_name   = "${var.label_prefix}-${local.object_storage_logging_group_name}"
  description    = "${var.app_name} Object storage logging group"
}

resource "oci_logging_log" "object_storage_log" {
  for_each     = toset(local.categories)
  display_name = "${var.label_prefix}-Object-storage-log-${each.key}"
  log_group_id = oci_logging_log_group.object_storage_logging_group.id
  log_type     = local.log_type_service

  configuration {
    source {
      category    = each.key
      resource    = var.object_storage_bucket_name
      service     = local.service_objectstorage
      source_type = local.source_type
    }
    compartment_id = var.compartment_id
  }
  is_enabled = local.is_log_enabled
}

locals {
  object_storage_logging_group_name = "object-storage-logging-group"
  category_read                     = "read"
  category_write                    = "write"
  service_objectstorage             = "objectstorage"
  categories                        = [local.category_read, local.category_write]
}