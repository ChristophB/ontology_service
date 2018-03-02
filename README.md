# Ontology Service for WebProtégé

This is a DropWizard bases API for WebProtégé. Specifications are not fixed yet, but the service should enable the user to query ontologies which are stored in WebProtégé as binary-OWL files.

# Installation

* OWL2GraphML is required, but it is not yet available for public use. 
* Java jre8 is required to start the service. (If you do not want to mess around with configurations consider using the Dockerfile below.)
* Use one of our released jar files or compile the Maven Project in Eclipse and create your own one.
* Place a file *config.yml* in the same directory as the jar file. *config.yml* should contain all configurations such as the location of WebProtégé's data dictionary and ports (use the configuration file of this git repository as reference).
* Run `java -jar service.jar server config.yml` on command line.

## Recommendation: Docker!
Use the Dockerfile in this repository or pull my image from DockerHub for an easy setup of this rest-api on a server with installed WebProtégé.

Assumption:
* WebProtégé's data folder is `/data/webprotege` on the host file system.

Run in a bash:
```bash
> docker run --name ontology-service -d -p 8080:8080 -p 8081:8081 -e ROOT_PATH="/ontology-service" -e WEBPROTEGE="/webprotege" -v /data/webprotege:/data/webprotege ontomed/ontology-service
```

You can specify the following environment variables:
* ROOT_PATH: path relative to webroot, where the rest-API will be running (default: /ontology-service)
* WEBPROTEGE: path relative to webroot, where WebProtégé is running

## Or use docker-compose:
```yml
version: '2'

services:
  mongodb:
    image: mongo
    restart: always
  webprotege:
    image: christophbe/webprotege
    restart: always
    ports:
      - 80:8080
    volumes:
      - webprotege-data:/srv/webprotege
    links:
      - mongodb
  ontology-service:
    image: ontomed/ontology-service
    restart: always
    ports:
      - '81:8080'
    links:
      - mongodb
    volumes:
      - webprotege-data:/srv/webprotege
    environment:
      - ROOT_PATH:/ontology-service
      - WEBPROTEGE:/webprotege

volumes:
  webprotege-data:
```

# Usage

* The rootPath of the DropWizard application is set to "/ontology-service"
* Access "[host ip]:[allocated port]/ontology-service" to get a list of possible queries.

e.g.: `localhost:8080/ontology-service` for the documentation, `localhost:8080/ontology-service/projects` for a list of available project ontologies and their ids.
