package de.uni_leipzig.imise.webprotege.rest_api.project;

import org.apache.commons.lang3.StringUtils;

/**
 * This class represents a project by providing a condensed set of metadata.
 * @author Christoph Beger
 */
public class WebProtegeProject {
	private String projectId;
	private String name;
	private String description;
	
	public WebProtegeProject(String projectId, String name, String description) {
		this.projectId   = projectId;
		this.name        = name;
		this.description = description;
	}
	
	public String getProjectId() {
		return projectId;
	}
	
	public String getName() {
		return StringUtils.defaultString(name, "");
	}
	
	public String getDescription() {
		return StringUtils.defaultString(description, "");
	}
}
