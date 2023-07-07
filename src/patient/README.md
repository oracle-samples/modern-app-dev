# Patient Service

Patient is a Micronaut-based microservice that exposes APIs for managing patient details in the Universal Healthcare Organization reference application.


## Developer quick start

Start the application through Java:
```shell
mvn clean install
MICRONAUT_ENVIRONMENTS=dev java -jar target/patient-1.0-SNAPSHOT.jar
```

Start the application in a Docker container by passing required variables that are defined in 
your application-env.yml file.

```shell
docker build -t patient:latest .
docker run -p 8080:8080 --env MICRONAUT_ENVIRONMENTS=<env> --mount type=bind,source=<path-to-.oci>,target=/root/.oci patient:latest
```
Access the application at http://localhost:8080

**Note**: You can find the jacoco report in target/site/jacoco/index.html.