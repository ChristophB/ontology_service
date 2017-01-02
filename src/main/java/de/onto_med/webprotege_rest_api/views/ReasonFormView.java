package de.onto_med.webprotege_rest_api.views;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;

public class ReasonFormView extends FormView {
	
	public ReasonFormView() {
		super("ReasonForm.ftl");
	}
	
	public ReasonFormView(ProjectManager project) {
		super("ReasonForm.ftl", project);
	}
	
}
