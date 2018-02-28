package de.onto_med.ontology_service.views;

import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;

import java.util.List;
import java.util.Map;

public class PhenotypeView extends RestApiView {
	private String                        id;
	private List<AbstractSinglePhenotype> phenotypes;
	private Map<String, Long>             ontologies;

	public PhenotypeView(String template, String rootPath, String id) {
		super(template, rootPath);
		this.id = id;
	}

	public PhenotypeView(String template, String rootPath, Map<String, Long> ontologies) {
		super(template, rootPath);
		this.ontologies = ontologies;
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

	public Map<String, Long> getOntologies() {
		return ontologies;
	}
}
