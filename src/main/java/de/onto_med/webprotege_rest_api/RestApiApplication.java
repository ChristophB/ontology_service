package de.onto_med.webprotege_rest_api;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.WebApplicationException;

//import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.onto_med.webprotege_rest_api.health.WebProtegeHealthCheck;
import de.onto_med.webprotege_rest_api.resources.MetaProjectResource;
import de.onto_med.webprotege_rest_api.resources.ProjectResource;
import de.onto_med.webprotege_rest_api.resources.StaticResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
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
		bootstrap.addBundle(new AssetsBundle());
	}

	
	@Override
	public void run(RestApiConfiguration configuration, Environment environment) throws Exception {
		environment.healthChecks().register("template", new WebProtegeHealthCheck(configuration));
		
		if (Files.notExists(Paths.get(configuration.getDataPath())))
			throw new WebApplicationException("The specified WebProtégé data folder does not exist.");
		
		MetaProjectResource metaProjectResource = new MetaProjectResource(configuration.getDataPath());
		ProjectResource projectResource = new ProjectResource(configuration.getWebprotegeRelativeToWebroot());
		StaticResource staticResource = new StaticResource();
		
		metaProjectResource.setProjectResource(projectResource).setRootPath(configuration.getRootPath());
		projectResource.setMetaProjectManager(metaProjectResource.getMetaProjectManager()).setRootPath(configuration.getRootPath());
		staticResource.setRootPath(configuration.getRootPath());
		
		environment.jersey().register(metaProjectResource);
		environment.jersey().register(projectResource);
		environment.jersey().register(staticResource);
//		environment.jersey().register(MultiPartFeature.class);
	}

}
