# Appointment Service

## Developer quick start

Appointment is a Micronaut-based microservice that exposes APIs for managing appointment bookings in the Universal Healthcare Organization reference application.

Start the application through Java:
```shell
mvn clean install
MICRONAUT_ENVIRONMENTS=dev java -jar target/appointment-1.0-SNAPSHOT.jar
```

Start the application in a Docker container by passing required variables that are defined in 
your application-env.yml file.

```shell
DOCKER_BUILDKIT=1 docker build -t appointment:latest .
docker run -p 8080:8080 --env MICRONAUT_ENVIRONMENTS=<env> --mount type=bind,source=<path-to-.oci>,target=/root/.oci appointment:latest
```
Access the application at http://localhost:8080


**Note**: You can find the jacoco report in target/site/jacoco/index.html.