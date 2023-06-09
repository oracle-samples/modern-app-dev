# Deploying to OCI

## Developer quick start

**In case Oracle Data Safe is not enabled for your region, 
enable it for your region by going to OCI Console->Oracle Database->Date Safe 
and click on Enable Data Safe**

Step 1 : Create a testing.tfvars file for the variables required.
```shell
tenancy_ocid = <tenancy ocid>
compartment_id = <compartment ocid>
region = <region>
 
idcs_url = <url of the idcs instance>
idcs_admin_client_id = <admin app client id>
idcs_admin_client_secret = <admin app client secret>

# If you want to use existing encryption key use below flag and add existing existent_kms_vault_id and existent_kms_key_id
create_new_key = false
# Make sure the kms key id that you pass is in same compartment as compartment_id 
existent_kms_vault_id = <ocid of existing kms vault>
existent_kms_key_id = <ocid of existing kms key>

# If you want to use existing public and private key use generate_ssh_pair variable
generate_ssh_pair = <can be either true or false>
# Make sure the generate_ssh_pair variable is false inorder to enter the public_key_path and private_key_path variables
public_key_path = <path to public ssh key>
private_key_path = <path to private ssh key>

smtp_username = <smtp username of the current user>
smtp_password = <smtp password>
current_user_ocid = <current user ocid>
auth_token = <auth toke of the above current user>

#If cloud guard is already enabled in your tenancy, specify the reporting region as per the reporting region mentioned in Cloud Guard->Settings
#If cloud guard is not enabled, specify the reporting region where you want cloud guard to be enabled
reporting_region = <region>

# If you want to use the CI/CD pipeline using the GitHub repository, give the below flag as true  
# If you want to use the CI/CD pipeline with OCI Devops repository, give the below flag as false. By default, it is false
create_external_connection = false
# This has to be added only when create_external_connection is given as true
# Specify your GitHub personal access token created in GitHub
github_pat_token = <GitHub Personal access token of the user>
# This has to be added only when create_external_connection is given as true
# Specify your forked GitHub repository url
github_repo_url = <GitHub repo url>
# This has to be added only when create_external_connection is given as true
# Specify the GitHub branch name
github_branch_name = <GitHub branch name>


cluster_endpoint_visibility = <can be either "Private" or "Public">
```
Step 2. Place the testing.tfvars file under the deploy/terraform folder

Step 3. Open the terminal and navigate to ~/modern-app-dev/deploy/terraform folder

Step 4. Run the following command
```shell
terraform init
```

Step 5. Once Terraform is successfully initialised, run the Terraform plan
```shell
terraform plan -var-file="testing.tfvars"
```

Step 6: Run Terraform apply after the successful Terraform plan
```shell
terraform apply -var-file="testing.tfvars"
```


