variable "compartment_id" {
  type = string
}

variable "tenancy_id" {
  type = string
}

variable "label_prefix" {
  type = string
}

variable "object_storage_access_type" {
  default = "NoPublicAccess"
  type    = string
}

variable "bucket_name" {
  default = "UHO-bucket"
  type    = string
}

variable "kms_key_id" {
  type = string
}

variable "region" {
  type = string
}