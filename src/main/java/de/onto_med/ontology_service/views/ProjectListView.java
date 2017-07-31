package de.onto_med.ontology_service.views;

import java.util.ArrayList;

import de.onto_med.ontology_service.data_models.CondencedProject;

public class ProjectListView extends RestApiView {
	private final ArrayList<CondencedProject> projects;
	
	public ProjectListView(ArrayList<CondencedProject> projectList, String rootPath) {
		super("ProjectList.ftl", rootPath);
		this.projects = projectList;
	}
	
	public ArrayList<CondencedProject> getProjects() {
		return projects;
	}
}
