package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class FormView extends View {
	private final ProjectManager project;
	private String error;
	
	protected FormView(String templateName) {
		super(templateName);
		project = null;
	}
	
	protected FormView(String templateName, ProjectManager project) {
		super(templateName);
		this.project = project;
	}
	
	public void addErrorMessage(String error) {
 		if (this.error == null) {
 			this.error = error;
 		} else {
 			this.error += "<br>" + error;
 		}
 	}
 	
 	public ProjectManager getProject() {
 		return project;
 	}

 	public String getErrorMessage() {
 		return error;
 	}
}
