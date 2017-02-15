package de.onto_med.webprotege_rest_api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;

import de.onto_med.webprotege_rest_api.api.OWLEntityProperties;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import de.onto_med.webprotege_rest_api.views.ProjectView;
import de.onto_med.webprotege_rest_api.views.SimpleListView;

/**
 * This class provids all ontology specific tasks.
 * @author Christoph Beger
 */
@Path("/project")
@Singleton
public class ProjectResource extends Resource {
	
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
				return Response.ok(getProjectManager(projectId).getImportedOntologyIds()).build();
			} else {
				return Response.ok(new SimpleListView(getProjectManager(projectId).getImportedOntologyIds(), "Imported Ontologies")).build();
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
		return new ProjectView(metaProjectManager.getProjectManager(projectId));
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
			}
			result.addAll(manager.getIndividualPropertiesByClassExpression(ce));
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
		
		return result;
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
	
}
