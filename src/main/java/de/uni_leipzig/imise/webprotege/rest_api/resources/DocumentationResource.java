package de.uni_leipzig.imise.webprotege.rest_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.uni_leipzig.imise.webprotege.rest_api.views.DocumentationView;

/**
 * Resource class which handles all requests for documentations.
 * @author Christoph Beger
 */
@Path("/")
@Produces(MediaType.TEXT_HTML)
public class DocumentationResource {
	
	public DocumentationResource() {
		super();
	}
	
	/**
	 * Returns documentation as list.
	 * @return List of possible relative paths and parameters
	 */
	@GET
	@Path("/")
	public DocumentationView getDocumentation() {
		return new DocumentationView();
	}
}
