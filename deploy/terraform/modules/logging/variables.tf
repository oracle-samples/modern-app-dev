variable "compartment_id" {
  type = string
}

variable "tenancy_id" {
  type = string
}

variable "label_prefix" {
  type = string
}

variable "app_name" {
  type = string
}

variable "deployment" {
  type = object({
    appointment = object({
      id = string
    }),
    frontend = object({
      id = string
    }),
    encounter = object({
      id = string
    }),
    patient = object({
      id = string
    }),
    provider = object({
      id = string
    }),
  })
}

variable "email_delivery_application_id" {
  type = string
}

variable "object_storage_bucket_name" {
  type = string
}

variable "waf_id" {
  type = string
}

variable "enable_waf" {}

variable "apigw_subnet_id" {
  type = string
}

variable "functions_subnet_id" {
  type = string
}

variable "adb_endpoint_subnet_id" {
  type = string
}

variable "ajdb_endpoint_subnet_id" {
  type = string
}

variable "oke_subnet_ids" {

}

variable "dynamic_group_name"{
  type = string
  description = "Dynamic group for app logging policy"
}

variable "dynamic_group_id"{
  type = string
  description = "Dynamic group id for app logging policy"
}