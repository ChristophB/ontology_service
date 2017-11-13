package de.onto_med.ontology_service.data_model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Instances of this class represent ontological individuals.
 * @author Christoph Beger
 */
public class Individual {
	@JsonProperty
	private List<Property> properties;
	@JsonProperty
	private List<String> types;
	@JsonProperty
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
