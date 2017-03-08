package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;

import de.onto_med.webprotege_rest_api.api.CondencedProject;
import io.dropwizard.views.View;

public class ProjectListView extends View {
	private final ArrayList<CondencedProject> projects;
	
	public ProjectListView(ArrayList<CondencedProject> projectList) {
		super("ProjectList.ftl");
		this.projects = projectList;
	}
	
	public ArrayList<CondencedProject> getProjects() {
		return projects;
	}
}
