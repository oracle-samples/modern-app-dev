module "oci_oke" {
  source              = "oracle-terraform-modules/oke/oci"
  version             = "4.5.9"
  kubernetes_version  = "v1.26.2"
  compartment_id      = var.compartment_ocid
  region              = var.region
  home_region         = local.home_region
  tenancy_id          = var.tenancy_ocid
  ssh_private_key     = local.private_ssh_key
  ssh_public_key      = local.public_ssh_key
  vcn_name            = "${var.app_name}-VCN"
  vcn_dns_label       = "vcn${random_string.deploy_id.result}"
  create_bastion_host = local.create_bastion_host
  create_operator     = local.create_operator
  label_prefix        = random_string.deploy_id.result
  providers = {
    oci.home = oci.home
  }
  vcn_cidrs                                = local.vcn_cidrs
  control_plane_type                       = "private"
  pods_cidr                                = local.pods_cidr
  services_cidr                            = local.services_cidr
  use_cluster_encryption                   = var.use_cluster_encryption
  cluster_kms_key_id                       = var.use_cluster_encryption ? local.uho_key : null
  enable_pv_encryption_in_transit          = var.use_cluster_encryption
  node_pool_volume_kms_key_id              = var.use_cluster_encryption ? local.uho_key : null
  enable_operator_pv_encryption_in_transit = var.use_cluster_encryption
  use_node_pool_volume_encryption          = var.use_cluster_encryption
  operator_volume_kms_id                   = var.use_cluster_encryption ? local.uho_key : null
  freeform_tags                            = local.freeform_tags
  load_balancers                           = "internal"
  preferred_load_balancer                  = "internal"
  subnets                                  = local.subnets
  node_pools                               = local.node_pools
  create_bastion_service                   = local.create_bastion_service
  bastion_service_name                     = local.bastion_service_name
  bastion_service_target_subnet            = local.bastion_service_target_subnet
  depends_on                               = [time_sleep.wait_120_seconds]
}

# Workaround until the oke operator module is fixed
resource "time_sleep" "wait_120_seconds" {
  depends_on = [oci_identity_policy.app_compartment_policies]

  create_duration = "120s"
}

resource "null_resource" "uninstall_helm_releases" {
  triggers = {
    bastion_service_id       = module.oci_oke.bastion_service_instance_id
    operator_id              = data.oci_core_instances.test_instances.instances[0].id
    region                   = var.region
    bucket_name              = module.object_storage.object_storage_bucket_name
    idcs_url                 = var.idcs_url
    idcs_admin_client_id     = var.idcs_admin_client_id
    idcs_admin_client_secret = var.idcs_admin_client_secret
    prefix                   = random_string.deploy_id.result
  }

  lifecycle {
    ignore_changes = [
      triggers.bastion_service_id,
      triggers.operator_id,
      triggers.region,
      triggers.idcs_url,
      triggers.idcs_admin_client_id,
      triggers.idcs_admin_client_secret,
      triggers.prefix
    ]
  }

  provisioner "local-exec" {
    when    = destroy
    command = "chmod +x scripts/destroy_resources.sh && bash scripts/destroy_resources.sh"
    environment = {
      BASTION_SERVICE_OCID     = self.triggers.bastion_service_id
      OPERATOR_OCID            = self.triggers.operator_id
      REGION                   = self.triggers.region
      IDCS_URL                 = self.triggers.idcs_url
      IDCS_ADMIN_CLIENT_ID     = self.triggers.idcs_admin_client_id
      IDCS_ADMIN_CLIENT_SECRET = self.triggers.idcs_admin_client_secret
      PREFIX                   = self.triggers.prefix
    }
  }
  depends_on = [
    module.oci_oke
  ]
}

module "streaming" {
  source                           = "./modules/streaming"
  compartment_id                   = var.compartment_ocid
  tenancy_id                       = var.tenancy_ocid
  label_prefix                     = random_string.deploy_id.result
  uho_streampool_name              = "uho-streampool"
  stream_partitions                = 1
  appointment_messages_stream_name = "appointment-messages"
  patient_messages_stream_name     = "patient-messages"
  followup_messages_stream_name    = "followup-messages"
  feedback_messages_stream_name    = "feedback-messages"
  streaming_username               = "uho-streaming-user"
  stream_group_name                = "uho-stream-group"
  current_user_ocid                = var.current_user_ocid
  providers = {
    oci.home = oci.home
  }
  depends_on = [module.oci_oke]
}

module "object_storage" {
  source                     = "./modules/objectstorage"
  compartment_id             = var.compartment_ocid
  tenancy_id                 = var.tenancy_ocid
  label_prefix               = random_string.deploy_id.result
  object_storage_access_type = "NoPublicAccess"
  bucket_name                = "UHO-bucket"
  region                     = var.region
  kms_key_id                 = local.uho_key
  providers = {
    oci.home = oci.home
  }
  depends_on = [module.oci_oke]
}

module "notification" {
  source                     = "./modules/notification"
  compartment_id             = var.compartment_ocid
  tenancy_id                 = var.tenancy_ocid
  label_prefix               = random_string.deploy_id.result
  region                     = var.region
  appointment_stream_id      = module.streaming.appointment_messages_stream_id
  encounter_stream_id        = module.streaming.encounter_messages_stream_id
  followup_stream_id         = module.streaming.followup_messages_stream_id
  feedback_stream_id         = module.streaming.feedback_messages_stream_id
  functions_cidr             = lookup(var.network_cidrs, "FUNCTIONS-REGIONAL-CIDR")
  vcn_id                     = module.oci_oke.vcn_id
  nat_route_id               = module.oci_oke.nat_route_id
  app_name                   = local.app_name
  object_storage_bucket_name = module.object_storage.object_storage_bucket_name
  vault_id                   = local.uho_vault_id
  apigw_url                  = module.api_gateway.apigw_url
  idcs_url                   = var.idcs_url
  secret_name                = "idcs"
  current_user_ocid          = var.current_user_ocid
  smtp_username              = var.smtp_username
  smtp_password              = var.smtp_password
  user_id                    = var.current_user_ocid
  auth_token                 = var.auth_token
  image_tag                  = local.image_tag
  deploy_id                  = random_string.deploy_id.result
  providers = {
    oci.home = oci.home
  }
  depends_on = [module.oci_oke, module.api_gateway, null_resource.build-and-push-images]
}

module "events" {
  source         = "./modules/events"
  compartment_id = var.compartment_ocid
  label_prefix   = random_string.deploy_id.result
  stream_id      = module.streaming.encounter_messages_stream_id
  bucket_name    = module.object_storage.object_storage_bucket_name
  providers = {
    oci.home = oci.home
  }
  depends_on = [module.streaming, module.notification]
}

module "monitoring" {
  source                        = "./modules/monitoring"
  compartment_id                = var.compartment_ocid
  tenancy_id                    = var.tenancy_ocid
  label_prefix                  = random_string.deploy_id.result
  alarm_notification_topic_name = "monitoring-alarms"
  depends_on                    = [module.oci_oke]
  providers = {
    oci.home = oci.home
  }
}

module "cloud_guard" {
  source           = "./modules/cloudguard"
  compartment_id   = var.compartment_ocid
  tenancy_id       = var.tenancy_ocid
  reporting_region = var.reporting_region
  label_suffix     = random_string.deploy_id.result
  providers = {
    oci.home             = oci.home
    oci.reporting_region = oci.reporting_region
  }
  depends_on = [module.oci_oke]
  count      = var.enable_cloud_guard ? 1 : 0
}

module "web_application_firewall" {
  source         = "./modules/waf"
  compartment_id = var.compartment_ocid
  ingress_ip     = local.ingress_ip
  depends_on     = [module.oci_oke]
  count          = var.enable_waf ? 1 : 0
  providers = {
    oci.home = oci.home
  }
}


module "logging" {
  source         = "./modules/logging"
  compartment_id = var.compartment_ocid
  tenancy_id     = var.tenancy_ocid
  label_prefix   = random_string.deploy_id.result
  app_name       = var.app_name
  providers = {
    oci.home = oci.home
  }
  deployment = {
    appointment = module.api_gateway.deployment.appointment
    encounter   = module.api_gateway.deployment.encounter
    frontend    = module.api_gateway.deployment.frontend
    patient     = module.api_gateway.deployment.patient
    provider    = module.api_gateway.deployment.provider
  }
  email_delivery_application_id = module.notification.email_delivery_application_id
  object_storage_bucket_name    = module.object_storage.object_storage_bucket_name
  waf_id                        = var.enable_waf ? module.web_application_firewall[0].waf_id : null
  enable_waf                    = var.enable_waf
  apigw_subnet_id               = module.api_gateway.apigw_subnet_id
  functions_subnet_id           = module.notification.functions_subnet_id
  adb_endpoint_subnet_id        = module.atp.adb_endpoint_subnet_id
  ajdb_endpoint_subnet_id       = module.ajdb.ajdb_endpoint_subnet_id
  oke_subnet_ids                = module.oci_oke.subnet_ids
  dynamic_group_name            = module.streaming.dynamic_group_name
  dynamic_group_id              = module.streaming.dynamic_group_id
  depends_on                    = [module.oci_oke, module.api_gateway, module.notification, module.object_storage, module.streaming]
}

module "devops" {
  source                                 = "./modules/devops"
  compartment_id                         = var.compartment_ocid
  tenancy_id                             = var.tenancy_ocid
  label_prefix                           = random_string.deploy_id.result
  devops_project_name                    = "uho-devops"
  devops_notification_topic_name         = "uho-devops"
  container_repository_is_public         = var.container_repository_is_public
  build_pipeline_stage_image             = var.build_pipeline_stage_image
  cluster_id                             = module.oci_oke.cluster_id
  region                                 = var.region
  email_delivery_function_id             = module.notification.email_delivery_function_id
  smtp_username                          = var.smtp_username
  smtp_password                          = var.smtp_password
  object_storage_namespace_name          = module.object_storage.object_storage_namespace_name
  object_storage_bucket_name             = module.object_storage.object_storage_bucket_name
  appointment_container_repository_name  = oci_artifacts_container_repository.appointment_container_repository.display_name
  provider_container_repository_name     = oci_artifacts_container_repository.provider_container_repository.display_name
  patient_container_repository_name      = oci_artifacts_container_repository.patient_container_repository.display_name
  encounter_container_repository_name    = oci_artifacts_container_repository.encounter_container_repository.display_name
  frontend_container_repository_name     = oci_artifacts_container_repository.frontend_container_repository.display_name
  feedback_container_repository_name     = oci_artifacts_container_repository.feedback_container_repository.display_name
  followup_container_repository_name     = oci_artifacts_container_repository.followup_container_repository.display_name
  notification_container_repository_name = oci_artifacts_container_repository.notification_container_repository.display_name
  vault_id                               = local.uho_vault_id
  apigw_url                              = module.api_gateway.apigw_url
  idcs_url                               = var.idcs_url
  secret_name                            = "idcs"
  providers = {
    oci.home = oci.home
  }
  create_external_connection   = var.create_external_connection
  uho_devops_github_pat_secret = var.create_external_connection ? oci_vault_secret.uho_devops_github_pat_secret[0].id : ""
  github_repo_url              = var.github_repo_url
  github_branch_name           = var.github_branch_name
  depends_on                   = [module.oci_oke]
  count                        = var.create_devops_project ? 1 : 0
}

module "atp" {
  source               = "./modules/atp"
  compartment_id       = var.compartment_ocid
  tenancy_id           = var.tenancy_ocid
  label_suffix         = random_string.deploy_id.result
  app_name             = local.app_name
  vcn_id               = module.oci_oke.vcn_id
  adb_cidr_block       = lookup(var.network_cidrs, "ADB-SUBNET-REGIONAL-CIDR")
  nat_route_id         = module.oci_oke.nat_route_id
  ig_route_id          = module.oci_oke.ig_route_id
  dynamic_group_name   = module.streaming.dynamic_group_name
  subnet_regional_cidr = lookup(var.network_cidrs, "WORKERS-CIDR")
  providers = {
    oci.home = oci.home
  }
  depends_on           = [module.oci_oke, module.streaming]
}

module "ajdb" {
  source               = "./modules/ajdb"
  compartment_id       = var.compartment_ocid
  tenancy_id           = var.tenancy_ocid
  label_prefix         = random_string.deploy_id.result
  app_name             = local.app_name
  vcn_id               = module.oci_oke.vcn_id
  ajdb_cidr_block      = lookup(var.network_cidrs, "AJDB-SUBNET-REGIONAL-CIDR")
  nat_route_id         = module.oci_oke.nat_route_id
  ig_route_id          = module.oci_oke.ig_route_id
  subnet_regional_cidr = lookup(var.network_cidrs, "WORKERS-CIDR")
  depends_on           = [module.oci_oke]
  providers = {
    oci.home = oci.home
  }
}

module "api_gateway" {
  source                        = "./modules/apigateway"
  compartment_id                = local.apigw_compartment_id
  tenancy_id                    = var.tenancy_ocid
  label_prefix                  = random_string.deploy_id.result
  app_name                      = local.app_name
  apigw_cidr                    = lookup(var.network_cidrs, "APIGW-SUBNET-REGIONAL-CIDR")
  vcn_id                        = module.oci_oke.vcn_id
  ingress_ip                    = local.ingress_ip
  idcs_url                      = var.idcs_url
  idcs_admin_client_id          = var.idcs_admin_client_id
  idcs_admin_client_secret      = var.idcs_admin_client_secret
  bucket_name                   = module.object_storage.object_storage_bucket_name
  object_storage_namespace_name = module.object_storage.object_storage_namespace_name
  providers = {
    oci.home = oci.home
  }
}

module "data_safe" {
  source                = "./modules/datasafe"
  compartment_id        = var.compartment_ocid
  tenancy_id            = var.tenancy_ocid
  label_suffix          = random_string.deploy_id.result
  vcn_id                = module.oci_oke.vcn_id
  atp_id                = module.atp.db_id
  private_endpoint_ip   = lookup(var.network_cidrs, "PRIVATE-ENDPOINT-IP")
  atp_nsg_id            = module.atp.atp_nsg_id
  ajdb_nsg_id           = module.ajdb.ajdb_nsg_id
  ajdb_id               = module.ajdb.db_id
  atp_cidr              = lookup(var.network_cidrs, "ADB-SUBNET-REGIONAL-CIDR")
  ajdb_cidr             = lookup(var.network_cidrs, "AJDB-SUBNET-REGIONAL-CIDR")
  data_safe_subnet_cidr = lookup(var.network_cidrs, "DATASAFE-SUBNET-REGIONAL-CIDR")
  providers = {
    oci.home = oci.home
  }
  depends_on = [module.oci_oke, module.atp, module.ajdb]
  count      = var.enable_data_safe ? 1 : 0
}

module "vulnerability_scanning" {
  source         = "./modules/vulnerability"
  compartment_id = var.compartment_ocid
  tenancy_id     = var.tenancy_ocid
  region         = var.region
  repositories   = module.devops[0].repositories
  label_prefix   = random_string.deploy_id.result
  providers = {
    oci.home = oci.home
  }
  count = var.create_devops_project ? 1 : 0
}

module "apm" {
  source         = "./modules/apm"
  compartment_id = var.compartment_ocid
  label_prefix   = random_string.deploy_id.result
  providers = {
    oci.home = oci.home
  }
}

locals {
  vcn_cidrs     = [lookup(var.network_cidrs, "VCN-CIDR")]
  pods_cidr     = lookup(var.network_cidrs, "PODS-CIDR")
  services_cidr = lookup(var.network_cidrs, "KUBERNETES-SERVICE-CIDR")
  subnets = {
    pods= {
      netnum = 0,
      newbits = 0
    }
    bastion = {
      netnum  = 0,
      newbits = 13
    }
    operator = {
      netnum  = 1,
      newbits = 13
    }
    cp = {
      netnum  = 2,
      newbits = 13
    }
    int_lb = {
      netnum  = 16,
      newbits = 11
    }
    pub_lb = {
      netnum  = 17,
      newbits = 11
    }
    workers = {
      netnum  = 1,
      newbits = 2
    }
    fss = {
      netnum  = 0,
      newbits = 0
    }
  }
  freeform_tags = {
    vcn = {
      environment = "prod"
    }
    bastion = {
      environment = "prod"
      role        = "bastion"
    }
    operator = {
      environment = "prod"
      role        = "operator"
    }
    oke = {
      service_lb = {
        environment = "prod"
        role        = "load balancer"
        Application = "UHO-${random_string.deploy_id.result}"
      }
      cluster = {
        environment = "dev"
        role        = "cluster"
      }
      node_pool = {
        role = "workers_np"
      }
      node = {
        role = "workers"
      }
      persistent_volume = {
        environment = "dev"
      }
    }
  }
  create_bastion_host = false
  create_operator     = true
  app_name            = "uho-app"
  node_pools = {
    np1 = {
      shape = "VM.Standard.E4.Flex", ocpus = 1, memory = 16, node_pool_size = 3, boot_volume_size = 150,
      label = { app = "uho", pool = "np1" }
    }
  }
  create_bastion_service        = true
  bastion_service_name          = "bastion${random_string.deploy_id.result}"
  bastion_service_target_subnet = "operator"
  apigw_compartment_id          = var.deploy_using_msz_compartment ? var.normal_compartment_id : var.compartment_ocid
}