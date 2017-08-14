package de.onto_med.ontology_service.views;

import org.lha.phenoman.model.category_tree.PhenotypeAttributes;

public class PhenotypeFormView extends RestApiView {
	private PhenotypeAttributes phenotype;
	
	public PhenotypeFormView(String templateName, String rootPath) {
		super(templateName, rootPath);
	}
	
	public PhenotypeFormView(String templateName, String rootPath, PhenotypeAttributes phenotype) {
		this(templateName, rootPath);
		this.phenotype = phenotype;
	}
	
	public void setPhenotype(PhenotypeAttributes phenotype) {
		this.phenotype = phenotype;
	}
	
	public PhenotypeAttributes getPhenotype() {
		return phenotype;
	}

}
