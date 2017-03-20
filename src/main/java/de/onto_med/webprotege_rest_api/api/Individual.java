package de.onto_med.webprotege_rest_api.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Individual {
	@JsonProperty
	private List<Property> properties;
	@JsonProperty
	private List<String> types;
	private List<String> classification;
	
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	public void setClassification(List<String> classification) {
		this.classification = classification;
	}
	
	
	public List<Property> getProperties() {
		return properties;
	}
	
	public List<String> getTypes() {
		return types;
	}
	
	public List<String> getClassification() {
		return classification;
	};
	
}
