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
  default     = "UHO App"
  description = "Application name. Will be used as prefix to identify resources, such as OKE, VCN, ATP, and others"
  type        = string
}

variable "vcn_id" {
  type = string
}

variable "ajdb_cidr_block" {
  type = string
}

variable "nat_route_id" {
  type = string
}

variable "ig_route_id" {
  type = string
}

variable "subnet_regional_cidr" {
  type = string
}