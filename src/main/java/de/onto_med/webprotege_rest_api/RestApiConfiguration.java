package de.onto_med.webprotege_rest_api;

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
	@NotEmpty private String webprotegeRelativeToWebroot;
	@NotEmpty private String rootPath;
	
	@JsonProperty
	public String getDataPath() {
		return dataPath;
	}
	
	@JsonProperty
	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}
	
	@JsonProperty
	public String getWebprotegeRelativeToWebroot() {
		return webprotegeRelativeToWebroot;
	}
	
	@JsonProperty
	public void setWebprotegeRelativeToWebroot(String webprotegeRelativeToWebroot) {
		this.webprotegeRelativeToWebroot = webprotegeRelativeToWebroot;
	}
	
	@JsonProperty
	public String getRootPath() {
		return rootPath;
	}
	
	@JsonProperty
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
}