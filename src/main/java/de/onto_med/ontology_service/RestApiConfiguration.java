package de.onto_med.ontology_service;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * This is the configuration class of the application,
 * which reads config.yml and sets all required parameter.
 * @author Christoph Beger
 */
public class RestApiConfiguration extends Configuration {
	/**
	 * Path to the WebProtégé data folder
	 */
	@NotEmpty private String dataPath;
	/**
	 * Relative uri to the WebProtégé instance.
	 */
	@NotEmpty private String webprotegeRelativeToWebroot;
	/**
	 * Root path of the service.
	 */
	@NotEmpty private String rootPath;
	/**
	 * Path to the cop.owl file, which is used to store phenotypes.
	 */
	@NotEmpty private String phenotypePath;
	
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
	
	@JsonProperty
	public String getPhenotypePath() {
		return phenotypePath;
	}
	
	@JsonProperty
	public void setPhenotypePath(String phenotypePath) {
		this.phenotypePath = phenotypePath;
	}
	
}