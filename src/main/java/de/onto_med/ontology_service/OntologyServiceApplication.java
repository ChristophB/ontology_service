package de.onto_med.ontology_service;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import com.esotericsoftware.yamlbeans.YamlReader;

import de.onto_med.ontology_service.health.WebProtegeHealthCheck;
import de.onto_med.ontology_service.resources.AnnotationResource;
import de.onto_med.ontology_service.resources.MetaProjectResource;
import de.onto_med.ontology_service.resources.PhenotypeResource;
import de.onto_med.ontology_service.resources.ProjectResource;
import de.onto_med.ontology_service.resources.StaticResource;
import de.onto_med.ontology_service.tasks.ClearCacheTask;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

/**
 * This is the main application of the WebProtégé Rest-API.
 * @author Christoph Beger
 */
public class OntologyServiceApplication extends Application<OntologyServiceConfiguration> {
	private String rootPath = "";

	/**
	 * Main method, which starts the service.
	 * @param args DropWizard arguments
	 * @throws Exception If application start failed.
	 */
	public static void main(String[] args) throws Exception {
		new OntologyServiceApplication().run(args);
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
	public void initialize(Bootstrap<OntologyServiceConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets/css", rootPath + "/css", null, "css"));
		bootstrap.addBundle(new AssetsBundle("/assets/js", rootPath + "/js", null, "js"));
		bootstrap.addBundle(new AssetsBundle("/assets/vendors", rootPath + "/vendors", null, "vendors"));
		bootstrap.addBundle(new AssetsBundle("/favicon.ico", rootPath + "/favicon.ico", null, "favicon"));
	}
	
	/**
	 * This method calls the DropWizard run method and sets the rootPath of the service.
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
	 * Registers resources, health checks and tasks.
	 * Initializes important resources like MetaProjectResource and ProjectResource.
	 */
	@Override
	public void run(OntologyServiceConfiguration configuration, Environment environment) {
		if (Files.notExists(Paths.get(configuration.getDataPath())))
			throw new WebApplicationException("The specified WebProtégé data folder does not exist.");

		MetaProjectResource metaProjectResource = new MetaProjectResource(configuration.getDataPath(), configuration.getMongoHost(), configuration.getMongoPort());
		ProjectResource     projectResource     = new ProjectResource(configuration.getWebprotegeRelativeToWebroot());
		
		metaProjectResource.setProjectResource(projectResource).setRootPath(configuration.getRootPath());
		projectResource.setMetaProjectManager(metaProjectResource.getMetaProjectManager()).setRootPath(configuration.getRootPath());

		/* Register resources here: */
		environment.jersey().register(metaProjectResource);
		environment.jersey().register(projectResource);
		environment.jersey().register(new StaticResource(configuration.getRootPath()));
		environment.jersey().register(new AnnotationResource(metaProjectResource.getMetaProjectManager()).setRootPath(configuration.getRootPath()));
		environment.jersey().register(new PhenotypeResource(configuration.getRootPath(), configuration.getPhenotypePath()));
		// environment.jersey().register(MultiPartFeature.class);
		
		/* Register health checks here: */
		environment.healthChecks().register("template", new WebProtegeHealthCheck(configuration));
		
		/* Register tasks here: */
		environment.admin().addTask(new ClearCacheTask(metaProjectResource));
	}

}
