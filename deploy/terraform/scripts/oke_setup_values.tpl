${yamlencode({
  secrets = {
    stream = {
        bootstrap_servers = bootstrap_servers
        sasl_connection_instance_principal = sasl_connection_instance_principal
        appointment_messages_stream = appointment_messages_stream
        patient_events_stream = patient_events_stream
        feedback_messages_stream = feedback_messages_stream
        followup_messages_stream = followup_messages_stream
        encounter_messages_stream = encounter_messages_stream
        appointment_messages_stream_id = appointment_messages_stream_id
        patient_messages_stream_id = patient_messages_stream_id
        endpoint = streaming_endpoint
    }
    atp = {
        admin = {
            password = atp_admin_password
        }
        connection = {
            wallet_password = atp_wallet_password
            atp_service = atp_service
        }
        wallet_zip = {
            content = wallet_zip_content
        }
    }
    ajdb = {
        admin = {
            password = ajdb_admin_password
        }
        connection = {
            wallet_password = ajdb_wallet_password
            ajdb_service = ajdb_service
        }
        wallet_zip = {
            content = ajdb_wallet_zip_content
        }
    }
    apm = {
      data_upload_endpoint = apm_data_upload_endpoint
      data_upload_path = apm_data_upload_path
      data_private_key = apm_data_private_key
    }
  }
  config_maps = {
    object_storage = {
        namespace = object_storage_namespace
        bucket_name = object_storage_bucket_name
        region_id = region_id
    }
  }
  apigw = {
    url = apigw_url
  }
  idcs = {
    url = idcs_url
    admin = {
        client_id = admin_client_id
        client_secret = admin_client_secret
    }
  }
  compartment_ocid = compartment_ocid
  vault_ocid = vault_ocid
  key_ocid = key_ocid
  deploy_id = deploy_id
})}