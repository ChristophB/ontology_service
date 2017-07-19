package de.onto_med.webprotege_rest_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.onto_med.webprotege_rest_api.views.DocumentationView;
import de.onto_med.webprotege_rest_api.views.EntityFormView;
import de.onto_med.webprotege_rest_api.views.ReasonFormView;

/**
 * Resource class which handles all requests for documentations.
 * @author Christoph Beger
 */
@Path("/")
public class StaticResource {
	private String rootPath;
	
	public StaticResource() {
		super();
	}
	
	public StaticResource setRootPath(String rootPath) {
		this.rootPath = rootPath;
		
		return this;
	}

	
	/**
	 * Returns documentation as list.
	 * @return List of possible relative paths and parameters
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public DocumentationView getDocumentation() {
		return new DocumentationView(rootPath);
	}


	@GET
	@Path("/entity-form")
	@Produces(MediaType.TEXT_HTML)
	public EntityFormView getEntityForm(
		@QueryParam("type") String type,
		@QueryParam("name") String name,
		@QueryParam("iri") String iri,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@QueryParam("match") String match,
		@QueryParam("operator") String operator,
		@QueryParam("ontologies") String ontologies
	) {
		return new EntityFormView(rootPath, type, name, iri, property, value, match, operator, ontologies);
	}
	
	
	@GET
	@Path("/reason-form")
	@Produces(MediaType.TEXT_HTML)
	public ReasonFormView getReasonForm(@QueryParam("ce") String ce, @QueryParam("ontologies") String ontologies) {
		return new ReasonFormView(rootPath, ce, ontologies);
	}
	
}
