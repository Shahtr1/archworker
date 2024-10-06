FROM openjdk:17-jdk-slim

MAINTAINER shahbytes.com

# Install curl and wget
RUN apt-get update && apt-get install -y curl wget && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY target/eurekaserver-0.0.1-SNAPSHOT.jar eurekaserver-0.0.1-SNAPSHOT.jar

# Create the directory for the OpenTelemetry Java agent
RUN mkdir -p /app/libs

# Download the OpenTelemetry Java agent JAR
RUN curl -L -o /app/libs/opentelemetry-javaagent-1.33.6.jar https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/1.33.6/opentelemetry-javaagent-1.33.6.jar


ENTRYPOINT ["java","-jar","eurekaserver-0.0.1-SNAPSHOT.jar"]
