output "object_storage_bucket_name" {
  value = oci_objectstorage_bucket.uho_object_storage.name
}

output "object_storage_namespace_name" {
  value = data.oci_objectstorage_namespace.objectstorage_namespace.namespace
}