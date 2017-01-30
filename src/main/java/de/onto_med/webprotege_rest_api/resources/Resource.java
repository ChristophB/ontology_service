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
	protected MetaProjectManager metaProjectManager;
	
	
	protected ProjectManager getProjectManager(String projectId) throws Exception {
		return metaProjectManager.getProjectManager(projectId);
	}
	
	public MetaProjectManager setMetaProjectManager(MetaProjectManager mpm) {
		metaProjectManager = mpm;
		
		return metaProjectManager;
	}
}
