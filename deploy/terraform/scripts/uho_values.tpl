${yamlencode({
    global = {
        idcs = {
            url = idcs_url
            keys = idcs_keys
        }
        oci_vault_id = oci_vault_id
        compartment_id = compartment_id
        secrets = {
            atp = {
                patient = {
                    password = patient_atp_password
                }
                provider = {
                    password = provider_atp_password
                }
                encounter = {
                    password = encounter_atp_password
                }
                appointment = {
                    password = appointment_atp_password
                }
            }
            ajdb = {
                encounter = {
                    password = encounter_ajdb_password
                }
            }
        }
        deploy_id = deploy_id
        lb_nsg_id = lb_nsg_id
        lb_ingress_nsg_id = lb_ingress_nsg_id
        repo_path =  repo_path
        image_tag = image_tag
        atp_ocid = atp_ocid
        ajdb_ocid = ajdb_ocid
    }
})}