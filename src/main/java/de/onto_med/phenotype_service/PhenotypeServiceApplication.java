package de.onto_med.phenotype_service;

import de.onto_med.ontology_service.resources.PhenotypeResource;
import de.onto_med.phenotype_service.health.PhenotypeHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class PhenotypeServiceApplication extends Application<PhenotypeServiceConfiguration> {
	/**
	 * Main method, which starts the service.
	 * @param args Dropwizard arguments
	 * @throws Exception If App initialization failed
	 */
	public static void main(String[] args) throws Exception {
		new PhenotypeServiceApplication().run(args);
	}

	/**
	 * Returns the name of this service.
	 */
	@Override
	public String getName() {
		return "Phenotype Service";
	}

	/**
	 * Initializes the service.
	 * Add additional bundles here.
	 *
	 * bootstrap.addBundle(new ExampleBundle(...);
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(Bootstrap<PhenotypeServiceConfiguration> bootstrap) {
		bootstrap.addBundle(new ViewBundle());
		bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
		bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
		bootstrap.addBundle(new AssetsBundle("/assets/vendors", "/vendors", null, "vendors"));
		bootstrap.addBundle(new AssetsBundle("/favicon.ico", "/favicon.ico", null, "favicon"));
	}

	/**
	 * Registers resources, healthchecks and tasks.
	 * Initializes important resources like MetaProjectResource and ProjectResource.
	 */
	@Override
	public void run(PhenotypeServiceConfiguration configuration, Environment environment) {
		PhenotypeResource phenotypeResource = new PhenotypeResource("", configuration.getPhenotypePath());

		/* Register resources here: */
		environment.jersey().register(phenotypeResource);

		/* Register health checks here: */
		environment.healthChecks().register("template", new PhenotypeHealthCheck(configuration));
	}
}
