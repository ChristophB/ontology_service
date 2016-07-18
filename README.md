# webprotege-rest-api

This is a Dropwizard bases REST-API for WebProtege. Specifications are not fixed yet, but the service should enable the user to query ontologies which where created with WebProtege.

# Installation

* Compile the Maven Project in Eclipse and run java -jar "{name of the created jar file}" on command line.
* Configurations should be made in config.yml (such as location of WebProteges data dictionary and ports to use).

# Usage

* "/projects" lists all public available projects with their id.
* Use the id to query a single project "/project/{id}/". 
* "/project/{id}/class{name}" searches for a class in specified project, which matches the given name and returns some properties of the class.
* other functions will be added later.