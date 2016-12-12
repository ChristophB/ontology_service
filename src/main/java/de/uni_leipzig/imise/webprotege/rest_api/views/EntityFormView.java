package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.project.ProjectManager;
import io.dropwizard.views.View;

public class EntityFormView extends View {
	private final ProjectManager project;
	
 	public EntityFormView() {
		super("EntityForm.ftl");
		project = null;
	}
 	
 	public EntityFormView(ProjectManager project) {
 		super("EntityForm.ftl");
 		this.project = project;
 	}

 	public ProjectManager getProject() {
 		return project;
 	}
}
