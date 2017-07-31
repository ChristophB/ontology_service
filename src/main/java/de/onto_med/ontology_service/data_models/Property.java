package de.onto_med.ontology_service.data_models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents Properties provided by JSON.
 * Assumption: all values for a propert are from the same Java class.
 * 
 * @author Christoph Beger
 *
 */
public class Property {
	@JsonProperty
	private String iri;
	@JsonProperty
	private String className;
	@JsonProperty
	private List<String> values;
	
	public void setIri(String iri) {
		this.iri = iri;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	
	public String getIri() {
		return iri;
	}
	
	public String getClassName() {
		return className;
	}
	
	public List<String> getValues() {
		return values;
	}
	
}
