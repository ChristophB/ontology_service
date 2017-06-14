package de.onto_med.webprotege_rest_api.api.json;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;

/** 
 * Instances of this class represent projects, but only with the most relevant informations.
 * @author Christoph Beger
 */
public class CondencedProject {
	private String projectId;
	private String name;
	private String description;
	
	/**
	 * Constructs a CondencedProject from ProjectManager.
	 * @param project a ProjectManager
	 */
	public CondencedProject(ProjectManager project) {
		setProjectId(project.getProjectId());
		setName(project.getName());
		setDescription(project.getDescription());
	}
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
