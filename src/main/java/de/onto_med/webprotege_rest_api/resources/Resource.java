package de.onto_med.webprotege_rest_api.resources;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import de.onto_med.webprotege_rest_api.manager.MetaProjectManager;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;

/**
 * Abstract class for Dropwizard resources
 * @author Christoph Beger
 */
public abstract class Resource {
	/**
	 * Path to WebProtegés data folder.
	 */
	protected MetaProjectManager metaProjectManager;
	/**
	 * root path of this application (relative to hostname)
	 */
	protected String rootPath;
	
	public Resource() {}
	
	public Resource(MetaProjectManager mpm) {
		metaProjectManager = mpm;
	}
	
	public Resource(String rootPath) {
		this.rootPath = rootPath;
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
