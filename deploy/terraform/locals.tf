locals {
  private_ssh_key     = var.generate_ssh_pair ? tls_private_key.public_private_key_pair[0].private_key_pem : data.local_file.private_ssh_key_file[0].content
  public_ssh_key      = var.generate_ssh_pair ? tls_private_key.public_private_key_pair[0].public_key_openssh : data.local_file.public_ssh_key_file[0].content
  bastion_host_name   = "host.bastion.${var.region}.oci.oraclecloud.com"
  uho_key             = var.create_new_key ? oci_kms_key.uho_key[0].id : var.existent_kms_key_id
  uho_vault_id        = var.create_new_key ? oci_kms_vault.uho_vault[0].id : var.existent_kms_vault_id
  cluster_id          = module.oci_oke.cluster_id
  operator_private_ip = module.oci_oke.operator_private_ip
  bastion_svc_user    = oci_bastion_session.operator_session_managed_ssh.id
  ingress_ip          = local.ingress_lb.ip_address
  home_region         = data.oci_identity_regions.home-region.regions[0]["name"]
  region_code         = lower(data.oci_identity_regions.region.regions[0].key)
  namespace           = data.oci_objectstorage_namespace.objectstorage_namespace.namespace
  image_tag           = "rc_1_7"
  lblist = flatten([
    for lb in data.oci_load_balancer_load_balancers.load_balancers.load_balancers : [
      {
        id            = lb.id
        display_name  = lb.display_name
        freeform_tags = lb.freeform_tags
        ip_address    = lb.ip_address_details[0].ip_address
      }
    ]
  ])
  ingress_lb = element([for lb in local.lblist : lb if lookup(lb.freeform_tags, "Application", "") == local.freeform_tags.oke.service_lb.Application], 0)
}