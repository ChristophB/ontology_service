package de.onto_med.phenotype_service.health;

import com.codahale.metrics.health.HealthCheck;
import de.onto_med.phenotype_service.PhenotypeServiceConfiguration;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class PhenotypeHealthCheck extends HealthCheck {
	private PhenotypeServiceConfiguration configuration;

	public PhenotypeHealthCheck(PhenotypeServiceConfiguration configuration) {
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
		Path path = FileSystems.getDefault().getPath(configuration.getPhenotypePath());
		
		return Files.exists(path) && Files.isReadable(path);
	}
}
