package de.onto_med.webprotege_rest_api.views;

import de.onto_med.webprotege_rest_api.api.TaxonomyNode;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;

public class ProjectTaxonomyView extends ProjectView {
	private final TaxonomyNode taxonomy;
	private static final String template = "ProjectTaxonomy.ftl";
	
	
	public ProjectTaxonomyView(ProjectManager projectManager, String baseUri, TaxonomyNode taxonomy) {
		super(template, projectManager, baseUri);
		this.taxonomy = taxonomy;
	}
	
	public TaxonomyNode getTaxonomy() {
		return taxonomy;
	}
	
	public String getWebProtegeUri() {
		return String.format("http://%s/#Edit:projectId=%s", baseUri, getProject().getProjectId());
	}
	
	public ProjectManager getProject() {
		return projectManager;
	}
}
