package de.onto_med.ontology_service.views;

import de.onto_med.ontology_service.manager.ProjectManager;

public class ProjectTaxonomyView extends ProjectView {
	private static final String template = "ProjectTaxonomy.ftl";

	public ProjectTaxonomyView(ProjectManager projectManager, String rootPath, String baseUri) {
		super(template, projectManager, rootPath, baseUri);
	}
}
