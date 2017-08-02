package de.onto_med.ontology_service.views;

import de.onto_med.ontology_service.api.TaxonomyNode;
import de.onto_med.ontology_service.manager.ProjectManager;

public class ProjectTaxonomyView extends ProjectView {
	private final TaxonomyNode taxonomy;
	private static final String template = "ProjectTaxonomy.ftl";
	
	
	public ProjectTaxonomyView(ProjectManager projectManager, String rootPath, String baseUri, TaxonomyNode taxonomy) {
		super(template, projectManager, rootPath, baseUri);
		this.taxonomy = taxonomy;
	}
	
	public TaxonomyNode getTaxonomy() {
		return taxonomy;
	}
}
