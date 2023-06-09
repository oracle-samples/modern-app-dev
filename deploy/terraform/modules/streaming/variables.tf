variable "compartment_id" {
  type = string
}

variable "tenancy_id" {
  type = string
}

variable "label_prefix" {
  type = string
}

variable "uho_streampool_name" {
  default = "uho-streampool"
  type    = string
}

variable "stream_retention_in_hours" {
  default = 24
  type    = number
}

variable "stream_partitions" {
  default = 3
  type    = number
}

variable "auto_create_topics_enable" {
  default = true
  type    = bool
}

variable "appointment_messages_stream_name" {
  default = "appointment-messages"
  type    = string
}

variable "patient_messages_stream_name" {
  default = "patient-messages"
  type    = string
}
variable "encounter_messages_stream_name" {
  default = "encounter-messages"
  type    = string
}

variable "followup_messages_stream_name" {
  default = "followup-messages"
  type    = string
}

variable "feedback_messages_stream_name" {
  default = "feedback-messages"
  type    = string
}

variable "streaming_username" {
  default = "uho-streaming-user"
  type    = string
}

variable "stream_group_name" {
  default = "uho-stream-group"
  type    = string
}

variable "current_user_ocid" {
  type = string
}