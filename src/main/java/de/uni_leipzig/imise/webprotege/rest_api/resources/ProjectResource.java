package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.manager.MetaProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.views.EntityFormView;
import de.uni_leipzig.imise.webprotege.rest_api.views.EntityResultsetView;
import de.uni_leipzig.imise.webprotege.rest_api.views.ProjectView;
import de.uni_leipzig.imise.webprotege.rest_api.views.ReasonFormView;
import de.uni_leipzig.imise.webprotege.rest_api.views.SimpleListView;

/**
 * This class provids all ontology specific tasks.
 * @author Christoph Beger
 */
@Path("/project")
public class ProjectResource extends Resource {
	
	/**
	 * Constructor.
	 * @param dataPath path to WebProtegés data folder.
	 */
	public ProjectResource(String dataPath) {
		super(dataPath);
	}
	
	
	/**
	 * Returns a list of imported ontologies for a specified project.
	 * @param projectid ID of the WebProtegé project
	 * @return List of imported ontologies or error message
	 */
	@GET
	@Path("/{id}/imports")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getOntologyImportsJson(@Context HttpHeaders headers, @PathParam("id") String projectId) {
		try {
			List<MediaType> accepts = headers.getAcceptableMediaTypes();
			if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(getProjectManager(projectId).getOntologyImports()).build();
			} else {
				return Response.ok(new SimpleListView(getProjectManager(projectId).getOntologyImports(), "Imported Ontologies")).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	

	@GET
	@Path("/{id}/overview")
	@Produces(MediaType.TEXT_HTML)
	public ProjectView getProject(@PathParam("id") String projectId) throws Exception {
		return new ProjectView(new MetaProjectManager(dataPath).getProjectManager(projectId));
	}
	
	
	/**
	 * Returns full OWL document as RDF/XML.
	 * @param projectid ID of the WebProtegé project
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public Object getFullRDFDocument(@PathParam("id") String projectId) {
		try {
			return getProjectManager(projectId).getFullRDFDocument();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}

	
	@GET
	@Path("/{id}/reason")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response reason(
		@Context HttpHeaders headers,
		@PathParam("id") String projectId,
		@QueryParam("ce") String ce
	) {
		ArrayList<OWLEntityProperties> result;
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		ProjectManager project  = null;
		
		try {
			project = getProjectManager(projectId);
			result  = reason(projectId, ce);
		} catch (Exception e) {
			if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				ReasonFormView view = new ReasonFormView(project);
				view.addErrorMessage(ce + "<br><br>" + e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(result)).build();
		}
	}
	

	/**
	 * Reasons over the specified ontologies with supplied classexpression
	 * @param id projectId of the project
	 * @param ce class expression
	 * @return search result
	 */
	public ArrayList<OWLEntityProperties> reason(String projectId, String ce) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			ProjectManager manager = getProjectManager(projectId);
			
			/* TODO: remove this workarround */
			HashMap<String, String> shortFormMap = manager.getOntologyIris();
			for (String shortForm : shortFormMap.keySet()) {
				ce = ce.replaceAll(shortForm + ":([\\w_\\-]+)", "<" + shortFormMap.get(shortForm) + "#$1>");
				System.err.println(ce);
			}
			result.addAll(manager.getIndividualPropertiesByClassExpression(ce));
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@GET
	@Path("/{id}/entity")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response searchOntologyEntities(
		@Context HttpHeaders headers,
		@PathParam("id") String projectId,
		@QueryParam("name")	String name,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@DefaultValue("entity") @QueryParam("type") String type,
		@DefaultValue("loose") @QueryParam("match") String match,
		@DefaultValue("and") @QueryParam("operator") String operator
	) {
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		ArrayList<OWLEntityProperties> result;
		ProjectManager project = null;
		
		try {
			project = getProjectManager(projectId);
			result  = searchOntologyEntities(projectId, name, property, value, type, match, operator);
		} catch (Exception e) {
			if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				EntityFormView view = new EntityFormView(project);
				view.addErrorMessage(e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		
		if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(result)).build();
		}
	}
	
	
	/**
	 * Searches for mathing entities in this ontology.
	 * @param name localName part of an entity
	 * @param property property the searched entity is annotated with
	 * @param value property value
	 * @param type entity, class or individual
	 * @param ontologies list of projectids separated by comma
	 * @param match matching method for name ('exact' or 'loose'), defaults to 'loose'
	 * @param operator logical operator to combine name and property, defaults to 'and')
	 * @return ArrayList of OWLEntityProperties or error message
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntities(
		String projectId, String name, String property, String value,
		String type, String match, String operator
	) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			ArrayList<OWLEntityProperties> tempResult = new ArrayList<OWLEntityProperties>();
			ProjectManager manager = getProjectManager(projectId);
				
			if (StringUtils.isNotEmpty(name)) {
				tempResult = manager.searchOntologyEntityByName(type, name, match);
			}
				
			if (StringUtils.isNotEmpty(property)) {
				if ("or".equals(operator) || StringUtils.isEmpty(name))
					tempResult.addAll(manager.searchOntologyEntityByProperty(type, property, value, match));
				else
					tempResult.retainAll(manager.searchOntologyEntityByProperty(type, property, value, match));	
			}
			
			result.addAll(tempResult);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
		
		return result;
	}
	
	
	@GET
	@Path("/{id}/entity-form")
	@Produces(MediaType.TEXT_HTML)
	public EntityFormView getEntityForm(@PathParam("id") String projectId) {
		try {
			return new EntityFormView(getProjectManager(projectId));
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	@GET
	@Path("/{id}/reason-form")
	@Produces(MediaType.TEXT_HTML)
	public ReasonFormView getReasonForm(@PathParam("id") String projectId) {
		try {
			return new ReasonFormView(getProjectManager(projectId));
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}

}
