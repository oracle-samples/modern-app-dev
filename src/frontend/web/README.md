# Reference Implementation: Universal Healthcare Organization

## Project Structure

The `uho-app` project uses lerna. The web application is located in `packages/uho-web`. Additional packages will be added to the `packages/` directory.

To install dependencies:

- Run `yarn run build` in the `web` directory to generate the REST clients and the `uho-web` project.

Useful commands:

- Run `yarn run watch:dev`: This script will build the web application locally. You can then point in `application-dev.yml` to the build directory to immediately get changes applied in the frontend. Update the `application-dev.yml` such as (fill in path-to):

```yaml
micronaut:
  application:
    name: frontend
  router:
    static-resources:
      default:
        enabled: true
        paths: file:<path-to>/ReferenceApp/frontend/web/packages/uho-web/build/dev/en-US`
```

- In `packages/uho-web` run `yarn run serve`: This will start the frontend standalone.
- In `packages/uho-web` run `yarn run prod`: This will build the frontend in production mode. Copy it over to `frontend/src/main/resources/static` to start it as part of the Frontend service.

## Formatting

There are two scripts that can be used to format the project:

- `yarn format`: This script will format everything under src/ according to the `prettier` conventions.
- `yarn format:verify`: This script will verify that the project is correctly formatted and fail if errors occur.
