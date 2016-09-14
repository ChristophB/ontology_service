# webprotege-rest-api

This is a Dropwizard bases REST-API for WebProtegé. Specifications are not fixed yet, but the service should enable the user to query ontologies which are stored in WebProtege as binary-OWL files.

# Installation

* Use one of our released jar files or compile the Maven Project in Eclipse and create your own one.
* Place a file *config.yml* in the same directory as the jar file. *config.yml* should contain all configurations such as the location of WebProteges data dictionary and ports (use the configuration file of this git repository as reference).
* Run `java -jar "{name of the created jar file}" server config.yml` on command line.


# Usage

* Access "[host ip]:[allocated port]/" to get a list of possible queries.

e.g.: `localhost:8080/` for the documentation, `localhost:8080/ontologies` for a list of available ontologies an their ids.