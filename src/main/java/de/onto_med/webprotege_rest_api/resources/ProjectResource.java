package de.onto_med.webprotege_rest_api.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;

import de.onto_med.webprotege_rest_api.api.Entity;
import de.onto_med.webprotege_rest_api.api.Individual;
import de.onto_med.webprotege_rest_api.api.Project;
import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import de.onto_med.webprotege_rest_api.views.ProjectTaxonomyView;
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
			ArrayList<String> importedOntologyIds = getProjectManager(projectId).getImportedOntologyIds();
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(importedOntologyIds).build();
			} else {
				return Response.ok(new SimpleListView(importedOntologyIds, "Imported Ontologies")).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	

	/**
	 * Returns project's ontology as simple taxonomy.
	 * @return HTML page or JSON object
	 */
	@GET
	@Path("/{id}/taxonomy")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getTaxonomy(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") String projectId) {
		try {
			ProjectManager project = metaProjectManager.getProjectManager(projectId);
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(project.getTaxonomy()).build();
			} else {
				return Response.ok(new ProjectTaxonomyView(project, uriInfo.getBaseUri().getHost(), project.getTaxonomy())).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	
	/**
	 * Returns some generic information about project's ontology
	 * @param headers
	 * @param uriInfo
	 * @param projectId
	 * @return HTML page or JSON list of generic information
	 */
	@GET
	@Path("/{id}/overview")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getProject(@Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") String projectId) {
		try {
			ProjectManager project = metaProjectManager.getProjectManager(projectId);
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(new Project(project)).build();
			} else {
				return Response.ok(new ProjectView(project, uriInfo.getBaseUri().getHost() + "/webprotege")).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	
	/**
	 * Returns full OWL document as RDF/XML.
	 * @param projectid ID of the WebProtegé project
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFullRDFDocument(@PathParam("id") String projectId) {
		try {
			ProjectManager pm = getProjectManager(projectId);
			return Response.ok(pm.getFullRDFDocument())
				.header(HttpHeaders.CONTENT_DISPOSITION,
						String.format("attachment; filename='%s.owl'", pm.getProjectShortForm()))
				.build();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	

	@POST
	@Path("/{id}/classify")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response classifyIndividuals(@PathParam("id") String projectId, List<Individual> individuals) {
		try {
			ProjectManager pm = getProjectManager(projectId);
			
			for (Individual individual : individuals) {
				individual.setClassification(pm.classifyIndividual(individual));
			}
			return Response.ok(individuals).build();
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
	public ArrayList<Entity> reason(String projectId, String ce) {
		try {
			ProjectManager manager = getProjectManager(projectId);
			
			/* TODO: remove this workaround */
			HashMap<String, String> shortFormMap = manager.getOntologyIris();
			for (String shortForm : shortFormMap.keySet()) {
				ce = ce.replaceAll(shortForm + ":([\\w_\\-]+)", "<" + shortFormMap.get(shortForm) + "#$1>");
			}
			return manager.getEntityProperties(ce);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
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
	public ArrayList<Entity> searchOntologyEntities(
		String projectId, String name, String iri, String property, String value,
		String type, String match, String operator
	) {
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property) && StringUtils.isEmpty(iri))
				throw new Exception("Neither query param 'name' nor 'iri', nor 'property' given.");
			
			ProjectManager manager = getProjectManager(projectId);
			
			return manager.getEntityProperties(iri, name, property, value, match, operator, type);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
}
