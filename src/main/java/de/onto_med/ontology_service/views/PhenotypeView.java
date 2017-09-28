package de.onto_med.ontology_service.views;

public class PhenotypeView extends RestApiView {
	private String id;

	public PhenotypeView(String template, String rootPath, String id) {
		super(template, rootPath);
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
