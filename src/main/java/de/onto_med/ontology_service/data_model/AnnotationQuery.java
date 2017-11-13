package de.onto_med.ontology_service.data_model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class can be used to transform json request to the AnnotationResource into Java objects.
 * @author Christoph Beger
 */
public class AnnotationQuery {
	/**
	 * The text which will be annotated.
	 */
	@JsonProperty
	private String text;
	/**
	 * A string containing project ids or ontology iris, which will be used for the annotation.
	 * If the string is empty, all ontologies will be used.
	 */
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
