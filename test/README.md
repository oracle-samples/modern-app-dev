**UHO Reference Application** 
**End 2 End API flow automation project**

_Developer quick start_

Run the tests with the following commands:

```
export IDCS_URL=<IDCS_BASE_URL>
export APIGW_URL=<APIGW_BASE_URL>
export IDCS_SECRET_BUNDLE=<IDCS_SECRET_IN_BASE64>
mvn clean test
```
***Note***
`IDCS_SECRET_BUNDLE` can be found by going to the vault that's associated with the deployment and searching for a secrete called `idcs`

