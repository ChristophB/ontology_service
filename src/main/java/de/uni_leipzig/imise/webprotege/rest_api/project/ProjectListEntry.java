package de.uni_leipzig.imise.webprotege.rest_api.project;

/**
 * This class represents a project by providing a condensed set of metadata.
 * @author Christoph Beger
 */
public class ProjectListEntry {
	public String id;
	public String name;
	public String description;
	
	public ProjectListEntry() {};
	
	public ProjectListEntry(String id, String name, String description) {
		this.id          = id;
		this.name        = name;
		this.description = description;
	}
}
