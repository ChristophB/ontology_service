package de.onto_med.webprotege_rest_api.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReasonQuery {
	@JsonProperty
	private String ontologies;
	@JsonProperty
	private String ce;
	
	public String getCe() {
		return ce;
	}
	
	public void setCe(String ce) {
		this.ce = ce;
	}
	
	public String getOntologies() {
		return ontologies;
	}
	
	public void setOntologies(String ontologies) {
		this.ontologies = ontologies;
	}
	
}
