data "oci_objectstorage_namespace" "objectstorage_namespace" {
  compartment_id = var.compartment_id
}

data "oci_identity_regions" "region" {
  filter {
    name   = "name"
    values = [var.region]
  }
}

resource "oci_ons_notification_topic" "appointments_topic" {
  compartment_id = var.compartment_id
  name           = "${local.appointments_notification_topic_name}-${var.label_prefix}"
}

resource "oci_ons_notification_topic" "encounters_topic" {
  compartment_id = var.compartment_id
  name           = "${local.encounters_notification_topic_name}-${var.label_prefix}"
}

resource "oci_ons_notification_topic" "followup_topic" {
  compartment_id = var.compartment_id
  name           = "${local.followup_notification_topic_name}-${var.label_prefix}"
}

resource "oci_ons_notification_topic" "feedback_topic" {
  compartment_id = var.compartment_id
  name           = "${local.feedback_notification_topic_name}-${var.label_prefix}"
}

resource "oci_ons_subscription" "appointment_topic_subscription" {
  compartment_id = var.compartment_id
  endpoint       = oci_functions_function.email_delivery_function.id
  protocol       = "ORACLE_FUNCTIONS"
  topic_id       = oci_ons_notification_topic.appointments_topic.id
}

resource "oci_ons_subscription" "encounter_topic_subscription" {
  compartment_id = var.compartment_id
  endpoint       = oci_functions_function.email_delivery_function.id
  protocol       = "ORACLE_FUNCTIONS"
  topic_id       = oci_ons_notification_topic.encounters_topic.id
}

resource "oci_ons_subscription" "followup_topic_subscription" {
  compartment_id = var.compartment_id
  endpoint       = oci_functions_function.email_delivery_function.id
  protocol       = "ORACLE_FUNCTIONS"
  topic_id       = oci_ons_notification_topic.followup_topic.id
}

resource "oci_ons_subscription" "feedback_topic_subscription" {
  compartment_id = var.compartment_id
  endpoint       = oci_functions_function.email_delivery_function.id
  protocol       = "ORACLE_FUNCTIONS"
  topic_id       = oci_ons_notification_topic.feedback_topic.id
}


resource "oci_functions_application" "email_delivery_application" {
  compartment_id             = var.compartment_id
  display_name               = "email-delivery-application-${var.label_prefix}"
  subnet_ids                 = [oci_core_subnet.functions_subnet.id]
  network_security_group_ids = [oci_core_network_security_group.function_security_group.id]
}

resource "oci_functions_function" "email_delivery_function" {
  application_id = oci_functions_application.email_delivery_application.id
  display_name   = "email-delivery-function-${var.label_prefix}"
  image          = "${lower(data.oci_identity_regions.region.regions[0].key)}.ocir.io/${data.oci_objectstorage_namespace.objectstorage_namespace.namespace}/uho-notification-${var.deploy_id}:${var.image_tag}"
  memory_in_mbs  = "256"
  config = {
    "SMTP_USERNAME" : var.smtp_username
    "SMTP_PASSWORD" : var.smtp_password
    "SMTP_HOST" : "smtp.email.${var.region}.oci.oraclecloud.com",
    "FROM_ADDRESS" : local.sender_email_address,
    "NAMESPACE" : data.oci_objectstorage_namespace.objectstorage_namespace.namespace,
    "BUCKET_NAME" : var.object_storage_bucket_name,
    "VAULT_ID" : var.vault_id,
    "SECRET_NAME" : var.secret_name,
    "IDCS_URL" : var.idcs_url,
    "APIGW_URL" : var.apigw_url
  }
  depends_on = [oci_functions_application.email_delivery_application]
}

resource "oci_sch_service_connector" "appointment_service_connector" {
  compartment_id = var.compartment_id
  display_name   = "${local.appointment_service_connector_display_name}-${var.label_prefix}"
  source {
    kind = "streaming"

    cursor {
      kind = "TRIM_HORIZON"
    }
    stream_id = var.appointment_stream_id
  }
  target {
    kind                       = "notifications"
    enable_formatted_messaging = false
    topic_id                   = oci_ons_notification_topic.appointments_topic.id
  }
  description = local.appointment_service_connector_description
}

resource "oci_sch_service_connector" "encounter_service_connector" {
  compartment_id = var.compartment_id
  display_name   = "${local.encounter_service_connector_display_name}-${var.label_prefix}"
  source {
    kind = "streaming"

    cursor {
      kind = "TRIM_HORIZON"
    }
    stream_id = var.encounter_stream_id
  }
  target {
    kind                       = "notifications"
    enable_formatted_messaging = false
    topic_id                   = oci_ons_notification_topic.encounters_topic.id
  }
  description = local.encounter_service_connector_description
}

resource "oci_sch_service_connector" "followup_service_connector" {
  compartment_id = var.compartment_id
  display_name   = "${local.followup_service_connector_display_name}-${var.label_prefix}"
  source {
    kind = "streaming"

    cursor {
      kind = "TRIM_HORIZON"
    }
    stream_id = var.followup_stream_id
  }
  target {
    kind                       = "notifications"
    enable_formatted_messaging = false
    topic_id                   = oci_ons_notification_topic.followup_topic.id
  }
  description = local.followup_service_connector_description
}

resource "oci_sch_service_connector" "feedback_service_connector" {
  compartment_id = var.compartment_id
  display_name   = "${local.feedback_service_connector_display_name}-${var.label_prefix}"
  source {
    kind = "streaming"

    cursor {
      kind = "TRIM_HORIZON"
    }
    stream_id = var.feedback_stream_id
  }
  target {
    kind                       = "notifications"
    enable_formatted_messaging = false
    topic_id                   = oci_ons_notification_topic.feedback_topic.id
  }
  description = local.feedback_service_connector_description
}

locals {
  encounters_notification_topic_name         = "encounters-topic"
  appointments_notification_topic_name       = "appointments-topic"
  followup_notification_topic_name           = "followup-topic"
  feedback_notification_topic_name           = "feedback-topic"
  appointment_service_connector_display_name = "appointments-notifications-connector"
  encounter_service_connector_display_name   = "encounters-notifications-connector"
  followup_service_connector_display_name    = "followup-notifications-connector"
  feedback_service_connector_display_name    = "feedback-notifications-connector"
  appointment_service_connector_description  = "Service Connector Hub for connecting appointment-messages to appointment notifications topic"
  encounter_service_connector_description    = "Service Connector Hub for connecting encounter-messages to encounter notifications topic"
  followup_service_connector_description     = "Service Connector Hub for connecting followup-messages to followup notifications topic"
  feedback_service_connector_description     = "Service Connector Hub for connecting feedback-messages to feedback notifications topic"
}