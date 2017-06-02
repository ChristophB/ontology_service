package de.onto_med.webprotege_rest_api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.onto_med.webprotege_rest_api.manager.MetaProjectManager;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;

@Path("/example")
@Produces({ MediaType.APPLICATION_JSON })
public class ExampleResource extends Resource {
	
	public ExampleResource(MetaProjectManager mpm) {
		super(mpm);
	}
	
	@GET
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response handleGetRequest(@QueryParam("projectId") String projectId) {
		try {
			ProjectManager pm = getProjectManager(projectId);
			return Response.ok("Responding to a GET request for project " + pm.getProjectIri() + ".").build();
		} catch (Exception e) {
			throw new WebApplicationException(e.getLocalizedMessage());
		}
	}
	
	@POST
	@Path("/")
	public Response handlePostRequest(@QueryParam("projectId") String projectId) {
		try {
			ProjectManager pm = getProjectManager(projectId);
			return Response.ok("Responding to a POST request for project " + pm.getProjectIri() + ".").build();
		} catch (Exception e) {
			throw new WebApplicationException(e.getLocalizedMessage());
		}
	}
	
}
