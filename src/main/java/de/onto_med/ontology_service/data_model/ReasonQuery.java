package de.onto_med.ontology_service.data_model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class can be used to transform reson json request into Java objects.
 * @author Christoph Beger
 */
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
