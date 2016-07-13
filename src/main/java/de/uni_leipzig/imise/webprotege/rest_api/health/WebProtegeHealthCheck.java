package de.uni_leipzig.imise.webprotege.rest_api.health;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codahale.metrics.health.HealthCheck;

import de.uni_leipzig.imise.webprotege.rest_api.RestApiConfiguration;

public class WebProtegeHealthCheck extends HealthCheck {
	private RestApiConfiguration configuration;
	
	public WebProtegeHealthCheck(RestApiConfiguration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * checks if path to data folder exists and is readable.
	 */
	@Override
	protected Result check() throws Exception {
		Path path = FileSystems.getDefault().getPath(configuration.getDataPath());
		
		if (Files.exists(path) && Files.isReadable(path)) {
			return Result.healthy();
		} else {
			return Result.unhealthy("Could not access WebProteges data folder!");
		}
	}
}
