package de.uni_leipzig.imise.webprotege.rest_api.views;

import java.util.ArrayList;

import de.uni_leipzig.imise.webprotege.rest_api.project.ProjectManager;
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
