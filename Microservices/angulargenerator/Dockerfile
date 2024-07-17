FROM openjdk:17-jdk-slim

MAINTAINER shahbytes.com

# Install curl and wget
RUN apt-get update && apt-get install -y curl wget && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY target/angulargenerator-0.0.1-SNAPSHOT.jar angulargenerator-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","angulargenerator-0.0.1-SNAPSHOT.jar"]