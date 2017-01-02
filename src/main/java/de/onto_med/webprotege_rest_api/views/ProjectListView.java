package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import io.dropwizard.views.View;

public class ProjectListView extends View {
	private final ArrayList<ProjectManager> projects;
	
	public ProjectListView(ArrayList<ProjectManager> arrayList) {
		super("ProjectList.ftl");
		this.projects = arrayList;
	}
	
	public ArrayList<ProjectManager> getProjects() {
		return projects;
	}
}
