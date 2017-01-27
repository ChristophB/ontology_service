package de.onto_med.webprotege_rest_api;

import de.onto_med.webprotege_rest_api.health.WebProtegeHealthCheck;
import de.onto_med.webprotege_rest_api.resources.MetaProjectResource;
import de.onto_med.webprotege_rest_api.resources.ProjectResource;
import de.onto_med.webprotege_rest_api.resources.StaticResource;
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
		environment.healthChecks().register("template", new WebProtegeHealthCheck(configuration));
		MetaProjectResource metaProjectResource = new MetaProjectResource(configuration.getDataPath());
		ProjectResource projectResource = new ProjectResource(configuration.getDataPath());
		
		metaProjectResource.setProjectResource(projectResource);
		projectResource.setMetaProjectManager(metaProjectResource.getMetaProjectManager());
		
		environment.jersey().register(metaProjectResource);
		environment.jersey().register(projectResource);
		environment.jersey().register(new StaticResource());
	}

}
