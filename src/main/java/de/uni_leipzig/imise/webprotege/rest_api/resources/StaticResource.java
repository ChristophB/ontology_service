package de.uni_leipzig.imise.webprotege.rest_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.uni_leipzig.imise.webprotege.rest_api.views.DocumentationView;
import de.uni_leipzig.imise.webprotege.rest_api.views.EntityFormView;
import de.uni_leipzig.imise.webprotege.rest_api.views.ReasonFormView;

/**
 * Resource class which handles all requests for documentations.
 * @author Christoph Beger
 */
@Path("/")
public class StaticResource {
	
	public StaticResource() {
		super();
	}

	
	/**
	 * Returns documentation as list.
	 * @return List of possible relative paths and parameters
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public DocumentationView getDocumentation() {
		return new DocumentationView();
	}


	@GET
	@Path("/entity-form")
	@Produces(MediaType.TEXT_HTML)
	public EntityFormView getEntityForm() {
		return new EntityFormView();
	}
	
	
	@GET
	@Path("/reason-form")
	@Produces(MediaType.TEXT_HTML)
	public ReasonFormView getReasonForm() {
		return new ReasonFormView();
	}
	
}
