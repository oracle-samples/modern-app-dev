# syntax=docker/dockerfile:experimental

# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/

FROM fnproject/fn-java-fdk-build:jdk17-1.0.174 as build-stage
WORKDIR /function
ADD pom.xml /function/pom.xml
ADD target/specs target/specs
RUN --mount=type=cache,target=/root/.m2 mvn package dependency:copy-dependencies -DincludeScope=runtime -DskipTests=true -Dmdep.prependGroupId=true -DoutputDirectory=target --fail-never
ADD src /function/src
RUN --mount=type=cache,target=/root/.m2 mvn package

FROM fnproject/fn-java-fdk:jre17-1.0.174
WORKDIR /function
COPY --from=build-stage /function/target/*.jar /function/app/
CMD ["com.oracle.refapp.notification.EmailFunction::handleRequest"]
