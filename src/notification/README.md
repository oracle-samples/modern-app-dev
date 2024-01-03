# Notification Service

Notification is an Oracle Cloud Infrastructure (OCI) Function that manages notifications in Universal Healthcare
Organization reference application.

## Developer quick start

1. Do the following setups:
    - Set up your API signing key
    - Install
      the [Fn CLI](https://docs.oracle.com/en-us/iaas/Content/Functions/Tasks/functionsquickstartlocalhost.htm#functionsquickstartlocalhost)
    - Complete the CLI configuration steps
    - Set up the OCI Registry you want to use
1. Create a context for your compartment and select that context. Choose any value for context name. Examples:
    - ```fn create context <context-name> --provider oracle```
    - ```fn use context <context-name>```
    - ```fn update context oracle.compartment-id <compartment-id>```
    - ```fn update context api-url https://functions.<region>.oraclecloud.com``` (example for region, 'ap-melbourne-1')
1. Provide a unique repository name prefix to distinguish your function images from others. For example, with 'jdoe' as
   the prefix, the image path for a 'hello' function image is, '
   enter-oci-region-key.ocir.io/enter-tenancy-namespace/jdoe/hello:0.0.1':
   ```fn update context registry <region-key>.ocir.io/<tenancy-namespace>/[repo-name-prefix]``` (example for
   region-key, 'mel').
1. Log in to the Registry using the Auth token as your password:
   ```docker login -u '<tenancy-namespace>/<username>' <region-key>.ocir.io```
1. Deploy the following function:
   ```fn deploy --app <oci-application-name>```
1. Run the following function:
   ```cat input-data-file | fn invoke <oci-application-name> notification```



