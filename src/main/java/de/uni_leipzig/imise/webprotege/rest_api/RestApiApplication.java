package de.uni_leipzig.imise.webprotege.rest_api;

import de.uni_leipzig.imise.webprotege.rest_api.health.WebProtegeHealthCheck;
import de.uni_leipzig.imise.webprotege.rest_api.resources.ProjectResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

/**
 * This is the main application of the WebProtegé Rest-API.
 * 
 * @author Christoph Beger
 */
public class RestApiApplication extends Application<RestApiConfiguration>{

	public static void main(String[] args) throws Exception {
		new RestApiApplication().run(args);
	}
	
	
	@Override
	public String getName() {
		return "WebProtege REST-API";
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(Bootstrap<RestApiConfiguration> bootstrap) { 
		bootstrap.addBundle(new ViewBundle());
	}

	
	@Override
	public void run(RestApiConfiguration configuration, Environment environment) throws Exception {
		final WebProtegeHealthCheck webProtegeHealthCheck = new WebProtegeHealthCheck(configuration);
		final ProjectResource project = new ProjectResource(configuration.getDataPath()); 
		
		environment.healthChecks().register("template", webProtegeHealthCheck);
		environment.jersey().register(project);
	}

}
