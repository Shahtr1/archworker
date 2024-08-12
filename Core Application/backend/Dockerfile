FROM openjdk:17-jdk-slim

MAINTAINER shahbytes.com

# Define the ARG and ENV variables
ARG TOKEN_SECRET
ENV TOKEN_SECRET=$TOKEN_SECRET

# Fail the build if TOKEN_SECRET is not provided
# if using windows cmd, you can use this command. where TOKEN_SECRET is environment variable
# docker build . --build-arg TOKEN_SECRET=%TOKEN_SECRET%  -t clay404/core-application:v1
RUN if [ -z "$TOKEN_SECRET" ]; then \
    echo "ERROR: TOKEN_SECRET is not set. Please provide the TOKEN_SECRET as a build argument."; \
    exit 1; \
    fi

# Install curl and wget
RUN apt-get update && apt-get install -y curl wget && apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy the jar file to the container
COPY target/core-application-0.0.1-SNAPSHOT.jar core-application-0.0.1-SNAPSHOT.jar

# Set the entry point for the container
ENTRYPOINT ["java","-jar","core-application-0.0.1-SNAPSHOT.jar"]
