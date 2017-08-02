package de.onto_med.ontology_service.views;

import de.onto_med.ontology_service.manager.ProjectManager;

public class ProjectView extends RestApiView {
	protected final ProjectManager projectManager;
	protected final String baseUri;
	private static final String template = "Project.ftl";
	
	public ProjectView(String template, ProjectManager projectManager, String rootPath, String baseUri) {
		super(template, rootPath);
		this.projectManager = projectManager;
		this.baseUri = baseUri;
	}
	
	public ProjectView(ProjectManager projectManager, String rootPath, String baseUri) {
		this(template, projectManager, rootPath, baseUri);
	}

	public ProjectManager getProject() {
		return projectManager;
	}
	
	public String getWebProtegeUri() {
		return String.format("http://%s/#Edit:projectId=%s", baseUri, getProject().getProjectId());
	}
}
