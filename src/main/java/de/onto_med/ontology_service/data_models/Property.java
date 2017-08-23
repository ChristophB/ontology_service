package de.onto_med.ontology_service.data_models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents Properties provided by JSON.
 * Assumption: all values for a property are from the same Java class.
 * @author Christoph Beger
 */
public class Property {
	@JsonProperty
	private String name;
	@JsonProperty
	private String className;
	@JsonProperty
	private String value;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getValue() {
		return value;
	}
	
}
