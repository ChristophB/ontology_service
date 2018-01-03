package de.onto_med.phenotype_service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhenotypeServiceConfiguration extends Configuration {
	/**
	 * Path to the cop.owl file, which is used to store phenotypes.
	 */
	@NotEmpty
	private String phenotypePath;

	@JsonProperty
	public String getPhenotypePath() {
		return phenotypePath;
	}

	@JsonProperty
	public void setPhenotypePath(String phenotypePath) {
		this.phenotypePath = phenotypePath;
	}
}
