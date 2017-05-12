# webprotege-rest-api

This is a Dropwizard bases REST-API for WebProtegé. Specifications are not fixed yet, but the service should enable the user to query ontologies which are stored in WebProtege as binary-OWL files.

# Installation

* Java jre8 is required to start the service. (If you dont want to mess around with configurations consider using the Dockerfile below.)
* Use one of our released jar files or compile the Maven Project in Eclipse and create your own one.
* Place a file *config.yml* in the same directory as the jar file. *config.yml* should contain all configurations such as the location of WebProteges data dictionary and ports (use the configuration file of this git repository as reference).
* Run `java -jar service.jar server config.yml` on command line.

## Recommendation: Docker!
Use the Dockerfile in this repository or pull my image from DockerHub for an easy setup of this rest-api on a server with installed webprotege.

Assumption:
* Webprotegés data folder is `/data/webprotege` on the host file system.

Run in a bash:
```bash
> docker run --name webprotege-rest-api -d -p 8080:8080 -p 8081:8081 -v /data/webprotege:/data/webprotege christophbe/webprotege-rest-api
```

##Or use docker-compose:
```yml
version: '2'

services:
  mongodb:
    container_name: mongodb
    image: 'mongo'
    restart: always
  webprotege:
    container_name: webprotege
    image: 'christophbe/webprotege'
    restart: always
    ports:
     - '80:8080'
    volumes:
     - webprotege-data:/data/webprotege
    links:
     - mongodb
  webprotege-rest-api:
    container_name: webprotege-rest-api
    image: 'christophbe/webprotege-rest-api'
    restart: always
    ports:
     - '81:8080'
    volumes:
     - webprotege-data:/data/webprotege

volumes:
  webprotege-data:
```

# Usage

* The rootPath of the Dropwizard application is set to "/webprotege-rest-api"
* Access "[host ip]:[allocated port]/webprotege-rest-api" to get a list of possible queries.

e.g.: `localhost:8080/webprotege-rest-api` for the documentation, `localhost:8080/webprotege-rest-api/projects` for a list of available project ontologies and their ids.
