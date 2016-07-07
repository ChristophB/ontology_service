package de.uni_leipzig.imise.webprotege.rest_api;

import de.uni_leipzig.imise.webprotege.rest_api.health.WebProtegeHealthCheck;
import de.uni_leipzig.imise.webprotege.rest_api.resources.Project;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class RestApiApplication extends Application<RestApiConfiguration>{

	public static void main(String[] args) throws Exception {
		new RestApiApplication().run(args);
	}
	
	@Override
	public String getName() {
		return "REST-API";
	}
	
	@Override
	public void initialize(Bootstrap<RestApiConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle<RestApiConfiguration>());
	}

	@Override
	public void run(RestApiConfiguration configuration, Environment environment) throws Exception {
		final WebProtegeHealthCheck webProtegeHealthCheck = new WebProtegeHealthCheck(configuration);
		final Project ontology = new Project(configuration.getDataPath()); 
		
		environment.healthChecks().register("template", webProtegeHealthCheck);
		environment.jersey().register(ontology);
	}

}
