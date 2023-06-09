resource "null_resource" "run_helm_charts_on_operator" {
  connection {
    host        = module.oci_oke.operator_private_ip
    private_key = local.private_ssh_key
    timeout     = "40m"
    type        = "ssh"
    user        = "opc"

    bastion_host        = local.bastion_host_name
    bastion_user        = oci_bastion_session.operator_session_managed_ssh.id
    bastion_private_key = local.private_ssh_key
  }

  provisioner "file" {
    source      = "../helm-chart"
    destination = "/home/opc"
  }

  provisioner "file" {
    content     = local.generate_oke_setup_values_yaml
    destination = "/home/opc/oke-setup-values.yaml"
  }

  provisioner "file" {
    content     = local.generate_uho_values_yaml
    destination = "/home/opc/uho-values.yaml"
  }

  provisioner "file" {
    content     = local.generate_uho_values_yaml
    destination = "/home/opc/uho-values.yaml"
  }

  provisioner "file" {
    content     = local.generate_ingress_healthcheck
    destination = "/home/opc/ingress-healthcheck.sh"
  }

  provisioner "remote-exec" {
    inline = [
      "helm install -f /home/opc/oke-setup-values.yaml oke-setup /home/opc/helm-chart/oke-setup/",
      "sleep 180",
      "helm install -f /home/opc/uho-values.yaml uho /home/opc/helm-chart/uho/ -n uho",
      "chmod 777 /home/opc/ingress-healthcheck.sh",
      "/home/opc/ingress-healthcheck.sh"
    ]
  }

  depends_on = [null_resource.install_k8_tools_on_operator, null_resource.build-and-push-images]
}

data "oci_bastion_bastions" "bastions" {
  compartment_id          = var.compartment_ocid
  bastion_lifecycle_state = "ACTIVE"
  name                    = local.bastion_service_name
}


data "oci_load_balancer_load_balancers" "load_balancers" {
  compartment_id = var.compartment_ocid
  detail         = "full"
  state          = "ACTIVE"
  depends_on     = [null_resource.run_helm_charts_on_operator]
}

locals {
  generate_oke_setup_values_yaml = templatefile("${path.module}/scripts/oke_setup_values.tpl",
    {
      bootstrap_servers                  = module.streaming.bootstrap_servers
      sasl_connection_instance_principal = "com.oracle.bmc.auth.sasl.InstancePrincipalsLoginModule required intent='streamPoolId:${module.streaming.streampool_id}';"
      appointment_messages_stream_id     = module.streaming.appointment_messages_stream_id
      patient_messages_stream_id         = module.streaming.patient_messages_stream_id
      streaming_endpoint                 = module.streaming.endpoint
      appointment_messages_stream        = module.streaming.appointment_messages_stream_name
      patient_events_stream              = module.streaming.patient_messages_stream_name
      feedback_messages_stream           = module.streaming.feedback_messages_stream_name
      followup_messages_stream           = module.streaming.followup_messages_stream_name
      encounter_messages_stream          = module.streaming.encounter_messages_stream_name
      atp_admin_password                 = module.atp.admin_password
      atp_wallet_password                = module.atp.wallet_password
      atp_service                        = "${module.atp.db_name}_TP"
      wallet_zip_content                 = module.atp.wallet_zip_content
      object_storage_namespace           = module.object_storage.object_storage_namespace_name
      object_storage_bucket_name         = module.object_storage.object_storage_bucket_name
      region_id                          = var.region
      apigw_url                          = module.api_gateway.apigw_url
      idcs_url                           = var.idcs_url
      admin_client_id                    = var.idcs_admin_client_id
      admin_client_secret                = var.idcs_admin_client_secret
      compartment_ocid                   = var.compartment_ocid
      vault_ocid                         = local.uho_vault_id
      key_ocid                           = local.uho_key
      ajdb_admin_password                = module.ajdb.admin_password
      ajdb_wallet_password               = module.ajdb.wallet_password
      ajdb_service                       = "${module.ajdb.db_name}_TP"
      ajdb_wallet_zip_content            = module.ajdb.wallet_zip_content
      apm_data_upload_endpoint           = module.apm.data_upload_endpoint
      apm_data_upload_path               = "/20200101/observations/public-span?dataFormat=zipkin&dataFormatVersion=2&dataKey=${module.apm.public_data_key}"
      apm_data_private_key               = module.apm.private_data_key
      deploy_id                          = random_string.deploy_id.result
    }
  )
  generate_uho_values_yaml = templatefile("${path.module}/scripts/uho_values.tpl",
    {
      idcs_url                 = var.idcs_url
      idcs_keys                = "{\"keys\":[${module.api_gateway.idcs_key_content}]}"
      oci_vault_id             = local.uho_vault_id
      compartment_id           = var.compartment_ocid
      patient_atp_password     = random_string.patient_user_atp_password.result
      provider_atp_password    = random_string.provider_user_atp_password.result
      encounter_atp_password   = random_string.encounter_user_atp_password.result
      appointment_atp_password = random_string.appointment_user_atp_password.result
      encounter_ajdb_password  = random_string.encounter_user_ajdb_password.result
      deploy_id                = random_string.deploy_id.result
      lb_nsg_id                = module.oci_oke.int_lb_nsg
      lb_ingress_nsg_id        = oci_core_network_security_group.int_lb_ingress_security_group.id
      repo_path                = "${local.region_code}.ocir.io/${local.namespace}/"
      image_tag                = local.image_tag
      atp_ocid                 = module.atp.db_id
      ajdb_ocid                = module.ajdb.db_id
    }
  )
  generate_ingress_healthcheck = templatefile("${path.module}/scripts/ingress_healthcheck.sh", {})
  private_key_path             = var.generate_ssh_pair ? "~/.ssh/id_rsa" : var.private_key_path
}