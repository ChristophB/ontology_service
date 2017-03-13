package de.onto_med.webprotege_rest_api.views;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class ProjectView extends View {
	private final ProjectManager ontologyManager;
	private final String baseUri;
	
	public ProjectView(ProjectManager ontologyManager, String baseUri) {
		super("Project.ftl");
		this.ontologyManager = ontologyManager;
		this.baseUri = baseUri;
	}

	public ProjectManager getProject() {
		return ontologyManager;
	}
	
	public String getWebProtegeUri() {
		return String.format("http://%s/#Edit:projectId=%s", baseUri, getProject().getProjectId());
	}
}
