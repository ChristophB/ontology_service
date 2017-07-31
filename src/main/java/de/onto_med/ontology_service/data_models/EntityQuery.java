package de.onto_med.ontology_service.data_models;

import javax.ws.rs.DefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class can be used to transform entity json requests into Java objects.
 * @author Christoph Beger
 */
public class EntityQuery {
	@JsonProperty
	private String name;
	@JsonProperty
	private String iri;
	@JsonProperty
	private String property;
	@JsonProperty
	private String value;
	@JsonProperty @DefaultValue("entity")
	private String type;
	@JsonProperty
	private String ontologies;
	@JsonProperty @DefaultValue("loose")
	private String match;
	@JsonProperty @DefaultValue("and")
	private String operator;
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getOntologies() {
		return ontologies;
	}

	public void setOntologies(String ontologies) {
		this.ontologies = ontologies;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
