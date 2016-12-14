package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;

public class ReasonFormView extends FormView {
	
	public ReasonFormView() {
		super("ReasonForm.ftl");
	}
	
	public ReasonFormView(ProjectManager project) {
		super("ReasonForm.ftl", project);
	}
	
}
