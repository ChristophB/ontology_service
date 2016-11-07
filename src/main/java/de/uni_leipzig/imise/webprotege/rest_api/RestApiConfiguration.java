package de.uni_leipzig.imise.webprotege.rest_api;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * This is the configuration class of the application,
 * which reads config.yml and sets all required parameter.
 * 
 * @author Christoph Beger
 *
 */
public class RestApiConfiguration extends Configuration {
	@NotEmpty private String dataPath;
	
	
	
	@JsonProperty
	public String getDataPath() {
		return dataPath;
	}
	
	
	@JsonProperty
	public void setDataPath(String path) {
		dataPath = path;
	}
}