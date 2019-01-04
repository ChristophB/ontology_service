package de.onto_med.ontology_service.data_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * This class represents Properties provided by JSON.
 * Assumption: all values for a property are from the same Java class.
 *
 * @author Christoph Beger
 */
public class Property {
	@JsonProperty
	private String name;
	@JsonProperty
	private String className;
	@JsonProperty
	private String value;
	@JsonProperty
	private String observationDate;

	public Property() {	}

	public Property(String name, String value) {
		this();
		this.name = name;
		this.value = value;
	}

	public Property(String name, String className, String value) {
		this(name, value);
		this.className = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getObservationDate() {
		return StringUtils.trimToNull(observationDate);
	}

	public void setObservationDate(String observationDate) {
		this.observationDate = observationDate;
	}

	public String toString() {
		return String.format("%s [%s]:%s", name, className, value);
	}
}
