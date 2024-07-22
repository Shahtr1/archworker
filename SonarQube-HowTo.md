# SonarQube Setup using Docker

This guide provides instructions to set up SonarQube with a PostgreSQL database using Docker.

## Prerequisites

- Docker installed on your machine.

## Steps

### 1. Pull SonarQube Docker Image

First, pull the latest SonarQube Docker image:

```sh
docker pull sonarqube
```

### 2. Run PostgreSQL Database Container

Next, run a PostgreSQL container for SonarQube:
```sh
docker run -d --name sonarqube-db -e POSTGRES_USER=sonar -e POSTGRES_PASSWORD=sonar -e POSTGRES_DB=sonarqube postgres:alpine
```
This command will:

-   Run the PostgreSQL container in detached mode (-d).
-   Set the container name to sonarqube-db.
- Set the PostgreSQL user to sonar (-e POSTGRES_USER=sonar).
- Set the PostgreSQL password to sonar (-e POSTGRES_PASSWORD=sonar).
- Set the PostgreSQL database name to sonarqube (-e POSTGRES_DB=sonarqube).

### 3. Run SonarQube Container
Now, run the SonarQube container and link it to the PostgreSQL container:

```sh
docker run -d --name sonarqube -p 9000:9000 --link sonarqube-db:db -e SONAR_JDBC_URL=jdbc:postgresql://db:5432/sonarqube -e SONAR_JDBC_USERNAME=sonar -e SONAR_JDBC_PASSWORD=sonar sonarqube
```

This command will:

- Run the SonarQube container in detached mode (-d).
- Set the container name to sonarqube.
- Expose port 9000 on the host to port 9000 on the container (-p 9000:9000).
- Link the SonarQube container to the PostgreSQL container (--link sonarqube-db:db).
- Set the JDBC URL for SonarQube to connect to the PostgreSQL container (-e SONAR_JDBC_URL=jdbc:postgresql://db:5432/sonarqube).
- Set the JDBC username for SonarQube to sonar (-e SONAR_JDBC_USERNAME=sonar).
- Set the JDBC password for SonarQube to sonar (-e SONAR_JDBC_PASSWORD=sonar).

## Applications:

### Using SonarQube with Maven project and Jacoco

Add these jacoco properties
```xml
<!-- JaCoCo Properties -->
<jacoco.version>0.8.7</jacoco.version>
<sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
<sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
<sonar.jacoco.reportPath>${project.basedir}/../target/jacoco.exec</sonar.jacoco.reportPath>
<sonar.language>java</sonar.language>
```

Add this dependency of jacoco maven plugin
```xml
<dependency>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</dependency>
```

Add the plugin in pom
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>${jacoco.version}</version>
    <executions>
        <execution>
            <id>jacoco-initialize</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>jacoco-site</id>
            <phase>package</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Now run
```sh 
mvn clean verify sonar:sonar -Dsonar.projectKey=archworker -Dsonar.projectName='archworker' -Dsonar.host.url=http://localhost:9000 -Dsonar.token=<Your-token>
```

If error persists:

try to use this in your settings.xml
```xml
<pluginGroups>
	<pluginGroup>org.sonarsource.scanner.maven</pluginGroup>
  </pluginGroups>

<!-- ... -->

<profiles>
    <profile>
        <id>sonar</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <!-- Optional URL to server. Default value is http://localhost:9000 -->
            <sonar.host.url>
                http://localhost:9000
            </sonar.host.url>
        </properties>
    </profile>
</profiles>
```