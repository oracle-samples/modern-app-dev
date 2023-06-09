data "oci_core_instances" "test_instances" {
  compartment_id = var.compartment_ocid
  display_name   = local.operator_instance_display_name
  filter {
    name   = "state"
    values = ["RUNNING"]
  }
  depends_on = [module.oci_oke]
}

resource "oci_bastion_session" "operator_session_managed_ssh" {
  bastion_id = module.oci_oke.bastion_service_instance_id
  key_details {
    public_key_content = local.public_ssh_key
  }
  target_resource_details {
    session_type                               = "MANAGED_SSH"
    target_resource_id                         = data.oci_core_instances.test_instances.instances[0].id
    target_resource_operating_system_user_name = "opc"
    target_resource_port = 22
  }

  display_name           = local.session_display_name
  key_type               = local.session_key_type
  session_ttl_in_seconds = local.session_session_ttl_in_seconds
}

resource "null_resource" "install_k8_tools_on_operator" {
  triggers = {
    operator_ocid = data.oci_core_instances.test_instances.id
  }
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
    content     = local.install_kubectl_template
    destination = "/home/opc/install_kubectl.sh"
  }

  provisioner "file" {
    content     = local.generate_kubeconfig_template
    destination = "/home/opc/generate_kubeconfig.sh"
  }

  provisioner "file" {
    content     = local.token_helper_template
    destination = "/home/opc/token_helper.sh"
  }

  provisioner "file" {
    content     = local.set_credentials_template
    destination = "/home/opc/kubeconfig_set_credentials.sh"
  }

  provisioner "file" {
    content     = local.install_helm_template
    destination = "/home/opc/install_helm.sh"
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x $HOME/install_kubectl.sh",
      "$HOME/install_kubectl.sh",
      "rm -f $HOME/install_kubectl.sh",
      "chmod +x $HOME/generate_kubeconfig.sh",
      "$HOME/generate_kubeconfig.sh",
      "mkdir $HOME/bin",
      "chmod +x $HOME/token_helper.sh",
      "mv $HOME/token_helper.sh $HOME/bin",
      "chmod +x $HOME/kubeconfig_set_credentials.sh",
      "$HOME/kubeconfig_set_credentials.sh",
      "rm -f $HOME/generate_kubeconfig.sh",
      "rm -f $HOME/kubeconfig_set_credentials.sh",
      "chmod +x $HOME/install_helm.sh",
      "bash $HOME/install_helm.sh",
      "rm -f $HOME/install_helm.sh"

    ]
  }

  depends_on = [oci_core_network_security_group.int_lb_ingress_security_group]
}

locals {
  operator_instance_display_name = "${random_string.deploy_id.result}-operator"
  session_session_ttl_in_seconds = 3000
  session_key_type               = "PUB"
  session_display_name           = "operator-bastion-session"
  operator_os_version            = "8"
  install_helm_template          = templatefile("${path.module}/scripts/install_helm.template.sh", {})
  install_kubectl_template = templatefile("${path.module}/scripts/install_kubectl.template.sh",
    {
      ol = local.operator_os_version
    }
  )
  generate_kubeconfig_template = templatefile("${path.module}/scripts/generate_kubeconfig.template.sh",
    {
      cluster-id = local.cluster_id
      region     = var.region
    }
  )
  token_helper_template = templatefile("${path.module}/scripts/token_helper.template.sh",
    {
      cluster-id = local.cluster_id
      region     = var.region
    }
  )
  set_credentials_template = templatefile("${path.module}/scripts/kubeconfig_set_credentials.template.sh",
    {
      cluster-id    = local.cluster_id
      cluster-id-11 = substr(local.cluster_id, (length(local.cluster_id) - 11), length(local.cluster_id))
      region        = var.region
    }
  )
}
