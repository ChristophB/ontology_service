package de.uni_leipzig.imise.webprotege.rest_api;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class RestApiConfiguration extends Configuration {
//	@NotEmpty private String dataFolder;
//	@NotEmpty private String pprjPath;
//	@NotEmpty private String pizzaOntology;
//	@NotEmpty private String ontologyFolder;
	@NotEmpty private String dataPath;
	
//	@JsonProperty
//	public String getPprj() {
//		return dataFolder + pprjPath;
//	}
//	
//	@JsonProperty
//	public String getPizzaOntology() {
//		return ontologyFolder + "/" + pizzaOntology ;
//	}
//	
//	@JsonProperty
//	public String getOntologyFolder() {
//		return ontologyFolder;
//	}
	
	@JsonProperty
	public String getDataPath() {
		return dataPath;
	}
	
//	@JsonProperty
//	public void setOntologyFolder(String folder) {
//		ontologyFolder = folder;
//	}
//	
//	@JsonProperty
//	public void setPizzaOntology(String path) {
//		pizzaOntology = path;
//	}
//	
//	@JsonProperty
//	public void setPprjPath(String path) {
//		pprjPath = path;
//	}
//	
//	@JsonProperty
//	public void setDataFolder(String folder) {
//		dataFolder = folder;
//	}
	
	@JsonProperty
	public void setDataPath(String path) {
		dataPath = path;
	}
}