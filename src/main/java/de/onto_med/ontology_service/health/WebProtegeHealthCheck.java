package de.onto_med.ontology_service.health;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codahale.metrics.health.HealthCheck;

import de.onto_med.ontology_service.OntologyServiceConfiguration;

public class WebProtegeHealthCheck extends HealthCheck {
	private OntologyServiceConfiguration configuration;
	
	public WebProtegeHealthCheck(OntologyServiceConfiguration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * checks if path to data folder exists and is readable.
	 */
	@Override
	protected Result check() throws Exception {
		if (!dataFolderIsAccessible())
			return Result.unhealthy("Could not access WebProteges data folder!");

		return Result.healthy();
	}
	
	private boolean dataFolderIsAccessible() {
		Path path = FileSystems.getDefault().getPath(configuration.getDataPath());
		
		return Files.exists(path) && Files.isReadable(path);
	}
}
