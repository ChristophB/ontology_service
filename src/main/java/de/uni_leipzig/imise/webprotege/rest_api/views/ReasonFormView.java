package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.project.ProjectManager;
import io.dropwizard.views.View;

public class ReasonFormView extends View {

	private final ProjectManager project;
	
	
	public ReasonFormView() {
		super("ReasonForm.ftl");
		project = null;
	}
	
	
	public ReasonFormView(ProjectManager project) {
		super("ReasonForm.ftl");
		this.project = project;
	}
	
	
	public ProjectManager getProject() {
		return project;
	}
	
}
