# Introduction

This application implements the [GITB test service APIs](https://www.itb.ec.europa.eu/docs/services/latest/) in a  
[Spring Boot](https://spring.io/projects/spring-boot) web application that is meant to support
[GITB TDL test cases](https://www.itb.ec.europa.eu/docs/tdl/latest/) running in the Interoperability Test Bed. 

## Messaging service implementation

Once running, the messaging endpoint's WDSL is available at http://localhost:8080/services/messaging?WSDL. See
[here](https://www.itb.ec.europa.eu/docs/services/latest/messaging/) for further information on messaging service implementations.

# Prerequisites

The following prerequisites are required:
* To build: JDK 17+, Maven 3.8+.
* To run: JRE 17+.

# Building and running

1. Build using `mvn clean package`.
2. Once built you can run the application in two ways:  
  a. With maven: `mvn spring-boot:run`.  
  b. Standalone: `java -jar ./target/po-test-services-VERSION.jar`.

## Live reload for development

This project uses Spring Boot's live reloading capabilities. When running the application from your IDE or through
Maven, any change in classpath resources is automatically detected to restart the application.

## Packaging using Docker

Running this application as a [Docker](https://www.docker.com/) container is very simple as described in Spring Boot's
[Docker documentation](https://spring.io/guides/gs/spring-boot-docker/). The first step is to 
[Install Docker](https://docs.docker.com/install/) and ensure it is up and running. You can now build the Docker image
using the approach that best suits you. Note that in both cases you can adapt as you want the resulting image name.

**Option 1: Using the provided Dockerfile** 

First make sure you build the app by issuing `mvn package`. Once built you can create the image using:
```
docker build -t local/po-test-services --build-arg JAR_FILE=./target/po-test-services-1.0-SNAPSHOT.jar .
```

**Option 2: Using the Spring Boot Maven plugin**
```
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=local/po-test-services
```

### Running the Docker container

Assuming an image name of `local/po-test-services`, it can be ran using `docker --name po-test-services -p 8080:8080 -d local/po-test-services`.
