package de.onto_med.webprotege_rest_api.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class can be used to transform json request to the AnnotationResource into Java objects.
 * @author Christoph Beger
 */
public class AnnotationQuery {
	@JsonProperty
	private String text;
	@JsonProperty
	private String ontologies;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getOntologies() {
		return ontologies;
	}
	
	public void setOntologies(String ontologies) {
		this.ontologies = ontologies;
	}
}
