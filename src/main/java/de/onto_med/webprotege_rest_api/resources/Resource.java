package de.onto_med.webprotege_rest_api.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.onto_med.webprotege_rest_api.manager.MetaProjectManager;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;

public abstract class Resource {
	final static Logger logger = LoggerFactory.getLogger(MetaProjectResource.class);
	/**
	 * Path to WebProtegés data folder.
	 */
	protected String dataPath;
	
	
	public Resource(String dataPath) {
		this.dataPath = dataPath;
	}
	
	
	protected ProjectManager getProjectManager(String projectId) throws Exception {
		return new MetaProjectManager(dataPath).getProjectManager(projectId);
	}
}
