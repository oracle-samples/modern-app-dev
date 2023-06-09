resource "oci_ons_notification_topic" "alarms_topic" {
  #Required
  compartment_id = var.compartment_id
  name           = "${var.alarm_notification_topic_name}-${var.label_prefix}"
}

resource "oci_ons_subscription" "alarm_topic_subscription" {
  #Required
  compartment_id = var.compartment_id
  endpoint       = local.uho_admin_email
  protocol       = "EMAIL"
  topic_id       = oci_ons_notification_topic.alarms_topic.id
}

resource "oci_monitoring_alarm" "api_error_alarm" {
  #Required
  compartment_id        = var.compartment_id
  destinations          = [oci_ons_notification_topic.alarms_topic.id]
  display_name          = "${var.label_prefix} - APIGW high failure rate"
  is_enabled            = true
  metric_compartment_id = var.compartment_id
  namespace             = "oci_apigateway"
  query                 = "HttpResponses[1m]{httpStatusCategory = \"4xx\"}.count() > 2"
  severity              = "CRITICAL"

  #Optional
  body = "Number of 4xx responses has crossed the threshold"
}


locals {
  uho_admin_email = "uho_alarms@mailinator.com"
}