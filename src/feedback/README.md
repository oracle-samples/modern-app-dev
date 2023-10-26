# Feedback Service

Helidon MP-based microservice, which generates feedback message for outgoing stream-based encounter message received on incoming stream.

## Developer quick start

### Code Compilation

Checked-in code uses OpenAPI generated client code. To generate this client code:
```shell
mvn generate-sources
```

### Start the application through Java
```shell
mvn package
java -Dmp.config.profile="dev" -jar ./target/feedback.jar
```

To build without tests:
```shell
mvn package -DskipTests
```

### Start the application through Docker
```shell
DOCKER_BUILDKIT=1 docker build -t feedback:java -f Dockerfile.dev .
docker run -p 8080:8080 feedback:java
```

## Exercise the application
REST endpoint is not applicable as this service is a messaging application.
You can invoke the flow by sending a sample message to "encounter-messages" stream.