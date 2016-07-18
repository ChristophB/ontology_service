package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.resources.Project;
import io.dropwizard.views.View;

public class ProjectView extends View {
	private final Project project;

    public ProjectView(Project project) {
        super("project.ftl");
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
