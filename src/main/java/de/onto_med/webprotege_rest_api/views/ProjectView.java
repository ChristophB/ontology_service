package de.onto_med.webprotege_rest_api.views;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class ProjectView extends View {
	protected final ProjectManager projectManager;
	protected final String baseUri;
	private static final String template = "Project.ftl";
	
	protected ProjectView(String template, ProjectManager projectManager, String baseUri) {
		super(template);
		this.projectManager = projectManager;
		this.baseUri = baseUri;
	}
	
	public ProjectView(ProjectManager projectManager, String baseUri) {
		this(template, projectManager, baseUri);
	}

	public ProjectManager getProject() {
		return projectManager;
	}
	
	public String getWebProtegeUri() {
		return String.format("http://%s/#Edit:projectId=%s", baseUri, getProject().getProjectId());
	}
}
