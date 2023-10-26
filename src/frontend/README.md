# Frontend Service

Frontend is a Micronaut based microservice for client request forwarding in the Universal Healthcare Organization reference application.


## Developer quick start

Building a docker image:
```shell
DOCKER_BUILDKIT=1 docker build -t frontend:latest -f Dockerfile .
```

Start the application through Java:

```shell
MICRONAUT_ENVIRONMENTS=dev mvn mn:run
```

Start the application in a Docker container:

```shell
mvn package 
DOCKER_BUILDKIT=1 docker build -t frontend:latest -f Dockerfile .
docker run -p 8080:8080 frontend:latest
```

Access the application at http://localhost:8080

## Add web app to frontend service

1. Build frontend `yarn prod` in `packages/uho-web`
2. Copy frontend (everything in `en-US`) to `/resources/static/`
3. Start server
4. Access http://localhost:8080
