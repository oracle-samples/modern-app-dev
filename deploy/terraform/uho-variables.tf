variable "tenancy_ocid" {
}

variable "compartment_ocid" {
}

variable "normal_compartment_id" {
  default = ""
}

variable "region" {
}

variable "reporting_region" {
  default = ""
}

variable "deploy_using_msz_compartment" {
  default = false
  type    = bool
}

## App name variables
variable "app_name" {
  default     = "UHO App"
  description = "Application name. Will be used as prefix to identify resources, such as OKE, VCN, ATP, and others"
}

## Autonomous Database

variable "private_key_path" {
  default = ""
}

variable "public_key_path" {
  default = ""
}

variable "create_devops_project" {
  default = true
}

variable "enable_waf" {
  default = false
}

variable "idcs_url" {
  validation {
    condition     = length(var.idcs_url) > 0
    error_message = "IDCS url must be valid."
  }
}

variable "idcs_admin_client_id" {
  validation {
    condition     = length(var.idcs_admin_client_id) > 0
    error_message = "IDCS admin client id must be valid."
  }
}

variable "idcs_admin_client_secret" {
  validation {
    condition     = length(var.idcs_admin_client_secret) > 0
    error_message = "IDCS admin client secret must be valid."
  }
}

variable "build_pipeline_stage_image" {
  default = "OL7_X86_64_STANDARD_10"
}

variable "container_repository_is_public" {
  default = true
}

variable "generate_ssh_pair" {
  default = true
}

# Network Details
## CIDRs
variable "network_cidrs" {
  type = map(string)

  default = {
    VCN-CIDR                      = "10.20.0.0/16"
    APIGW-SUBNET-REGIONAL-CIDR    = "10.20.3.0/24"
    ALL-CIDR                      = "0.0.0.0/0"
    PODS-CIDR                     = "10.244.0.0/16"
    KUBERNETES-SERVICE-CIDR       = "10.96.0.0/16"
    ADB-SUBNET-REGIONAL-CIDR      = "10.20.6.0/30"
    FUNCTIONS-REGIONAL-CIDR       = "10.20.5.0/24"
    AJDB-SUBNET-REGIONAL-CIDR     = "10.20.7.0/30"
    WORKERS-CIDR                  = "10.20.64.0/18"
    PRIVATE-ENDPOINT-IP           = "10.20.8.16"
    DATASAFE-SUBNET-REGIONAL-CIDR = "10.20.8.0/24"
    LB-CIDR                       = "10.20.9.0/24"
  }
}

# OKE Visibility
variable "cluster_endpoint_visibility" {
  default     = "Private"
  description = "The Kubernetes cluster that is created will be hosted on a public subnet with a public IP address auto-assigned or on a private subnet. If Private, additional configuration will be necessary to run kubectl commands"

  validation {
    condition     = var.cluster_endpoint_visibility == "Private" || var.cluster_endpoint_visibility == "Public"
    error_message = "Sorry, but cluster endpoint visibility can only be Private or Public."
  }
}

variable "cluster_loadbalancer_visibility" {
  default     = "Private"
  description = "The OCI Loadbalancer that is created by OKE will be hosted on a public subnet with a public IP address auto-assigned or on a private subnet. If Private, additional configuration will be necessary to run kubectl commands"

  validation {
    condition     = var.cluster_loadbalancer_visibility == "Private" || var.cluster_loadbalancer_visibility == "Public"
    error_message = "Sorry, but cluster loadbalancer visibility can only be Private or Public."
  }
}

variable "cluster_workers_visibility" {
  default     = "Private"
  description = "The Kubernetes worker nodes that are created will be hosted in public or private subnet(s)"

  validation {
    condition     = var.cluster_workers_visibility == "Private" || var.cluster_workers_visibility == "Public"
    error_message = "Sorry, but cluster visibility can only be Private or Public."
  }
}

## OKE Encryption details
variable "use_cluster_encryption" {
  default     = false
  description = "Use oke cluster encryption using user managed keys from OCI vault."
}

# OCI KMS details
variable "create_new_key" {
  default     = false
  description = "Creates a new key inside a new vault in user's compartment"
}

variable "existent_kms_key_id" {
  default     = ""
  description = "Use an existing master encryption key to encrypt"
}

variable "existent_kms_vault_id" {
  default     = ""
  description = "Use an existent vault to store encryption keys and sensitive data"
}


variable "create_compartment_policies" {
  default     = true
  description = "Creates policies that will reside on the compartment. e.g.: Policies to support Cluster Autoscaler, OCI Logging datasource on Grafana"
}

variable "auth_token" {
  default     = ""
  description = "Auth token of the current user."
}
variable "enable_cloud_guard" {
  default     = false
  description = "Enable cloud guard for the tenancy"
}

variable "enable_data_safe" {
  default     = false
  description = "Enable data safe for ADBs"
}

variable "current_user_ocid" {
  description = "ocid of the current user"
}

variable "smtp_username" {
  description = "smtp password"
}

variable "smtp_password" {
  description = "smtp password"
}

# OCI Devops - Github PAT token
variable "create_external_connection" {
  default     = "false"
  description = "Create external connection to connect to Github if you want to work with the devops pipeline"
}

variable "github_pat_token" {
  default     = ""
  description = "Provide your github PAT token to pull code from Github repository if you want to work with the devops pipeline"
}

variable "github_repo_url" {
  default     = ""
  description = "Provide the Github repository url if you want to work with the devops pipeline"
}

variable "github_branch_name" {
  default     = "master"
  description = "Provide the Github branch name if you want to work with the devops pipeline"
}
