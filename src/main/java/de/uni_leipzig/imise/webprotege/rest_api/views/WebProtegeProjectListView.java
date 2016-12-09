package de.uni_leipzig.imise.webprotege.rest_api.views;

import java.util.ArrayList;

import de.uni_leipzig.imise.webprotege.rest_api.project.WebProtegeProject;
import io.dropwizard.views.View;

public class WebProtegeProjectListView extends View {
	private final ArrayList<WebProtegeProject> projects;
	
	public WebProtegeProjectListView(ArrayList<WebProtegeProject> arrayList) {
		super("WebProtegeProjectList.ftl");
		this.projects = arrayList;
	}
	
	public ArrayList<WebProtegeProject> getProjects() {
		return projects;
	}
}
