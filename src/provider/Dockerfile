# syntax=docker/dockerfile:experimental

# Copyright (c) 2023 Oracle and/or its affiliates.
# Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracl.com/licenses/upl/

# 1st stage, build the app
FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.1 as build

# Install Apache Maven
RUN echo "Installing Apache Maven 3.9.2 ..." && \
  microdnf install gzip && \
  curl https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.9.2/apache-maven-3.9.2-bin.tar.gz --output /tmp/maven.tar.gz && \
  mkdir /opt/maven && \
  tar -zxvf /tmp/maven.tar.gz -C /opt/maven --strip-components=1

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}
ENV MAVEN_OPTS='-Xms256m -Xmx1024m -XX:-UseGCOverheadLimit'

WORKDIR /app

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
ADD target/specs target/specs

# Incremental docker builds will resume here when you change sources
ADD src src
RUN --mount=type=cache,target=/root/.m2 mvn package

# 2nd stage, build the runtime image
FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.1
WORKDIR /app

# Copy the binary built in the 1st stage
COPY --from=build /app/target/provider-1.0-SNAPSHOT.jar ./provider.jar

ENV MICRONAUT_ENVIRONMENTS=prod
CMD ["java", "-jar", "provider.jar"]

EXPOSE 8080
