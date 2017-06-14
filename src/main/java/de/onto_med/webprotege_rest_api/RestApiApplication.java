package de.onto_med.webprotege_rest_api;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import com.esotericsoftware.yamlbeans.YamlReader;

//import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.onto_med.webprotege_rest_api.health.WebProtegeHealthCheck;
import de.onto_med.webprotege_rest_api.resources.AnnotationResource;
import de.onto_med.webprotege_rest_api.resources.MetaProjectResource;
import de.onto_med.webprotege_rest_api.resources.ProjectResource;
import de.onto_med.webprotege_rest_api.resources.StaticResource;
import de.onto_med.webprotege_rest_api.tasks.ClearCacheTask;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

/**
 * This is the main application of the WebProtegé Rest-API.
 * @author Christoph Beger
 */
public class RestApiApplication extends Application<RestApiConfiguration>{
	private String rootPath = "";
	private MetaProjectResource metaProjectResource;
	private ProjectResource projectResource;
	private StaticResource staticResource;
	
	/**
	 * Main method, wich starts the service.
	 * @param args Dropwizard arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new RestApiApplication().run(args);
	}
	
	/**
	 * Returns the name of this service.
	 */
	@Override
	public String getName() {
		return "WebProtégé REST-API";
	}
	
	/**
	 * Initializes the service.
	 * Add additional bundles here.
	 * 
	 * bootstrap.addBundle(new ExampleBundle(...);
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(Bootstrap<RestApiConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets/css", rootPath + "/css", null, "css"));
		bootstrap.addBundle(new AssetsBundle("/assets/js", rootPath + "/js", null, "js"));
		bootstrap.addBundle(new AssetsBundle("/assets/vendors", rootPath + "/vendors", null, "vendors"));
	}
	
	/**
	 * This method calls the Dropwizard run method and sets the rootPath of the service.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run(String... arguments) throws Exception {
		try {
			YamlReader reader = new YamlReader(new FileReader(arguments[1]));
			String rootPath = ((Map<String, String>) reader.read()).get("rootPath");
			this.rootPath = rootPath != null ? rootPath : "";
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		super.run(arguments);
    }

	/**
	 * Registers resources, healthchecks and tasks.
	 * Initializes important resources like MetaProjectResource and ProjectResource.
	 */
	@Override
	public void run(RestApiConfiguration configuration, Environment environment) throws Exception {
		if (Files.notExists(Paths.get(configuration.getDataPath())))
			throw new WebApplicationException("The specified WebProtégé data folder does not exist.");
		
		metaProjectResource = new MetaProjectResource(configuration.getDataPath());
		projectResource     = new ProjectResource(configuration.getWebprotegeRelativeToWebroot());
		staticResource      = new StaticResource();
		
		metaProjectResource.setProjectResource(projectResource).setRootPath(configuration.getRootPath());
		projectResource.setMetaProjectManager(metaProjectResource.getMetaProjectManager()).setRootPath(configuration.getRootPath());
		staticResource.setRootPath(configuration.getRootPath());
		
		/*** Register resources here: ***/
		environment.jersey().register(metaProjectResource);
		environment.jersey().register(projectResource);
		environment.jersey().register(staticResource);
		environment.jersey().register(new AnnotationResource(metaProjectResource.getMetaProjectManager()).setRootPath(configuration.getRootPath()));
		// environment.jersey().register(MultiPartFeature.class);
		
		/*** Register health checks here: ***/
		environment.healthChecks().register("template", new WebProtegeHealthCheck(configuration));
		
		/*** Register tasks here: ***/
		environment.admin().addTask(new ClearCacheTask(metaProjectResource));
	}

}
