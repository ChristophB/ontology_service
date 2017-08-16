package de.onto_med.ontology_service.views;

import de.onto_med.ontology_service.data_models.Phenotype;

public class PhenotypeFormView extends RestApiView {
	private Phenotype phenotype;
	
	public PhenotypeFormView(String rootPath) {
		super("PhenotypeForm.ftl", rootPath);
	}
	
	public PhenotypeFormView(String rootPath, Phenotype phenotype) {
		this(rootPath);
		this.phenotype = phenotype;
	}
	
	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}
	
	public Phenotype getPhenotype() {
		return phenotype;
	}

}
