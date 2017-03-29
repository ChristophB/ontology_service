# webprotege-rest-api

This is a Dropwizard bases REST-API for WebProtegé. Specifications are not fixed yet, but the service should enable the user to query ontologies which are stored in WebProtege as binary-OWL files.

# Installation

* Java jre8 is required to start the service. (If you dont want to mess around with configurations consider using the Dockerfile below.)
* Use one of our released jar files or compile the Maven Project in Eclipse and create your own one.
* Place a file *config.yml* in the same directory as the jar file. *config.yml* should contain all configurations such as the location of WebProteges data dictionary and ports (use the configuration file of this git repository as reference).
* Run `java -jar service.jar server config.yml` on command line.

## Recommendation: Docker!
Use the following Dockerfile for an easy setup of our rest-api on a server with installed webprotege.

```Dockerfile
FROM openjdk:8-jre

WORKDIR /data/webprotege-rest-api

EXPOSE 8080 8081

CMD java -jar service.jar server config.yml
```

Assumption:
* Webprotegés data folder is `/data/webprotege` on the host file system.
* *service.jar* and *config.yml* are in a folder `/path/to/rest-api` on the host file system.

Run in a bash:
```bash
> docker build -t webprotege-rest-api .
> docker run --name webprotege-rest-api -d -p 8080:8080 -p 8081:8081 -v /data/webprotege:/data/webprotege -v /path/to/rest-api:/data/weprotege-rest-api webprotege-rest-api
```

# Usage

* Access "[host ip]:[allocated port]" to get a list of possible queries.

e.g.: `localhost:8080` for the documentation, `localhost:8080/projects` for a list of available project ontologies and their ids.