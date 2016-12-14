package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class ProjectView extends View {
	private final ProjectManager ontologyManager;
	
	public ProjectView(ProjectManager ontologyManager) {
		super("Project.ftl");
		this.ontologyManager = ontologyManager;
	}

	public ProjectManager getProject() {
		return ontologyManager;
	}
}
