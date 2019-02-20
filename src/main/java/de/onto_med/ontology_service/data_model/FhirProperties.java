package de.onto_med.ontology_service.data_model;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FhirProperties {
	public String serverUrl;
	public String gender;
	public String minDate;
	public String maxDate;
	public String minAge;
	public String maxAge;
	public List<Property> properties;

	public FhirProperties() { }

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getGender() {
		return StringUtils.trimToNull(gender);
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMinDate() {
		return StringUtils.trimToNull(minDate);
	}

	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	public String getMaxDate() {
		return StringUtils.trimToNull(maxDate);
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public String getMinAge() {
		return StringUtils.trimToNull(minAge);
	}

	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}

	public String getMaxAge() {
		return StringUtils.trimToNull(maxAge);
	}

	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
}
