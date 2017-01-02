package de.onto_med.webprotege_rest_api.views;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;

public class EntityFormView extends FormView {
	
 	public EntityFormView() {
		super("EntityForm.ftl");
	}
 	
 	public EntityFormView(ProjectManager project) {
 		super("EntityForm.ftl", project);
 	}
}
