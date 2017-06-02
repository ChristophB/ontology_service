package de.onto_med.webprotege_rest_api.resources;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

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
	protected String rootPath;
	
	public Resource() {}
	
	public Resource(MetaProjectManager mpm) {
		metaProjectManager = mpm;
	}
	
	protected ProjectManager getProjectManager(String projectId) throws NoContentException, ExecutionException {
		return metaProjectManager.getProjectManager(projectId);
	}
	
	protected boolean acceptsMediaType(HttpHeaders headers, MediaType mediaType) {
		return headers.getAcceptableMediaTypes().contains(mediaType);
	}
	
	public Resource setMetaProjectManager(MetaProjectManager mpm) {
		metaProjectManager = mpm;
		
		return this;
	}
	
	public Resource setRootPath(String rootPath) {
		this.rootPath = rootPath;
		
		return this;
	}
	
}
