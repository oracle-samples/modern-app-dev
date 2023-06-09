# Deployment

## Steps

To deploy the application to your tenancy, follow these steps:

1. Go to [Oracle Stacks](https://cloud.oracle.com/resourcemanager/stacks/):

   ![Images/stacks.png](Images/stacks.png)

1. Create a new stack:

   ![Images/deploy-stack.png](Images/deploy-stack.png)

1. Zip the `src` and `deploy` directory of the repository and upload it to the **Stack configuration** section.

1. Choose a **name** and **description**.

1. Select **Terraform version** as `1.2.x`

1. Add the configuration variables.

   - The Oracle Identity Cloud Service `(IDCS) URL` (For example, https://idcs-xxxx.identity.oraclecloud.com), `Client ID`, and `Client Secret` are generated during the IDCS configuration. For more information,
     see IDCS section in the
     [prerequisites and configurations](prereq-config.md)
     topic.
   - Select the option to create a vault or provide an existing vault and
     the encryption key if available. For more information, see [managing vaults](https://docs.oracle.com/en-us/iaas/Content/KeyManagement/Tasks/managingvaults.htm).

     ![Images/create-stack-2.png](Images/create-stack-2.png)

1. Enter the SMTP `username` and `password`.

   - To generate new SMTP credentials, go to **User Settings** from the user menu.

     ![Images/user-settings.png](Images/user-settings.png)

   - Click **Generate SMTP Credentials**. Copy the generated username and the password.

     ![Images/smtp-credentials.png](Images/smtp-credentials.png)

1. Enter the `Auth Token`.

   - To generate a new token, go to `User Settings` from the user menu.

   - Click **Generate Token**. Copy the generated auth token.

     ![Images/auth_token.png](Images/auth_token.png)

1. In addition to these steps, you can configure the following advanced options:

   - Select **Creation of DevOps project**. For configuration details, see [Deployment through DevOps](devops-deployment.md).
   - Provide custom encryption keys for OKE cluster.
   - Enable Cloud Guard, and Web Application Firewall (WAF).

   ![Images/create-stack-3.png](Images/create-stack-3.png)

   > **Note**: To enable Cloud Guard, select the appropriate reporting region. If Cloud Guard is already enabled in your tenancy, you must provide the reporting region specified in CloudGuard > Settings.
   > To know more about selecting a Cloud Guard reporting region, see
   > [Enabling Cloud Guard](https://docs.oracle.com/en-us/iaas/cloud-guard/using/part-start.htm#cg-access-enable-steps).

1. Review the configurations and click **Create**.

   ![Images/create-stack-4.png](Images/create-stack-4.png)

   A job is created and applied automatically. This job provisions the OCI resources and deploys the services to the OKE cluster.

   ![Images/rms-deploy.png](Images/rms-deploy.png)

1. Select the job and click **Outputs**.

   ![Images/rms-deploy-2.png](Images/rms-deploy-2.png)

   The Outputs table shows the `patient_username`, `provider_username`, `patient_password`, `provider_password`, and `uho_url` which are needed to login..

## Post Deployment

You can access the deployment by following the `uho_url` specified in the `Outputs` table:

![Images/url-deploy.png](Images/url-deploy.png)

## Destroying the deployment

To destroy the application from your tenancy, follow these steps:

1. Login to the OCI Console and select the region and compartment where the application is deployed.
1. Go to **Resource Manager** >> **Stacks**.
1. Select the deployed project.

   ![Images/resource-manager-stacks.png](Images/resource-manager-stacks.png)

1. The project details page opens. Click **Destroy**.

   ![Images/resources-jobs.png](Images/resources-jobs.png)

   You can view the job status on the Work Requests page.

   ![Images/destroy-status.png](Images/destroy-status.png)

**Note:** Oracle recommends destroying the stack after your usage concluded.
This action ensures that the cloud infrastructure resources used by the app do not continue
to incur usage charges and do not count against the service limits assigned for your tenancy.
