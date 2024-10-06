FROM openjdk:17-jdk-slim

MAINTAINER shahbytes.com

# Install curl and wget
RUN apt-get update && apt-get install -y curl wget && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the jar file to the container
COPY target/api-0.0.1-SNAPSHOT.jar api-0.0.1-SNAPSHOT.jar

# Set the entry point for the container
ENTRYPOINT ["java","-jar","api-0.0.1-SNAPSHOT.jar"]
