package de.onto_med.webprotege_rest_api.views;

import java.util.Collection;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class ProjectListView extends View {
	private final Collection<ProjectManager> projects;
	
	public ProjectListView(Collection<ProjectManager> collection) {
		super("ProjectList.ftl");
		this.projects = collection;
	}
	
	public Collection<ProjectManager> getProjects() {
		return projects;
	}
}
