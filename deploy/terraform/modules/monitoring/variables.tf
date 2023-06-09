variable "compartment_id" {
  type = string
}

variable "tenancy_id" {
  type = string
}

variable "label_prefix" {
  type = string
}

variable "alarm_notification_topic_name" {
  type    = string
  default = "monitoring-alarms"
}