package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;

public class EntityFormView extends FormView {
	
 	public EntityFormView() {
		super("EntityForm.ftl");
	}
 	
 	public EntityFormView(ProjectManager project) {
 		super("EntityForm.ftl", project);
 	}
}
