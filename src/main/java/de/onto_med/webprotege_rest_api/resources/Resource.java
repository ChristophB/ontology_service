package de.onto_med.webprotege_rest_api.resources;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

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
	
	protected boolean acceptsMediaType(HttpHeaders headers, MediaType mediaType) {
		return headers.getAcceptableMediaTypes().contains(mediaType);
	}
	
	public MetaProjectManager setMetaProjectManager(MetaProjectManager mpm) {
		metaProjectManager = mpm;
		
		return metaProjectManager;
	}
	
}
