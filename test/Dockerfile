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

ARG IDCS_URL
ARG APIGW_URL
ARG IDCS_SECRET_BUNDLE

ENV M2_HOME=/opt/maven
ENV MAVEN_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}
ENV MAVEN_OPTS='-Xms256m -Xmx1024m -XX:-UseGCOverheadLimit'
ENV IDCS_URL=${IDCS_URL}
ENV APIGW_URL=${APIGW_URL}
ENV IDCS_SECRET_BUNDLE=${IDCS_SECRET_BUNDLE}

WORKDIR /app

ADD pom.xml .
ADD src src
RUN mvn clean test

