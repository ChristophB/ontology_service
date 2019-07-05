package de.onto_med.ontology_service.data_model;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("WeakerAccess")
public class ArtDecorImportRequest {
	@JsonProperty
	private String categoryId;
	@JsonProperty
	private String dataSetId;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String artDecorId) {
		dataSetId = artDecorId;
	}
}
