package de.onto_med.ontology_service.views;

import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;

import java.util.List;

public class PhenotypeView extends RestApiView {
	private String id;
	private List<AbstractSinglePhenotype> phenotypes;

	public PhenotypeView(String template, String rootPath, String id) {
		super(template, rootPath);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setPhenotypes(List<AbstractSinglePhenotype> phenotypes) {
		this.phenotypes = phenotypes;
	}

	public List<AbstractSinglePhenotype> getPhenotypes() {
		return phenotypes;
	}
}
