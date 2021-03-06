package de.onto_med.ontology_service.resources;

import de.onto_med.ontology_service.data_model.CondencedProject;
import de.onto_med.ontology_service.data_model.Entity;
import de.onto_med.ontology_service.data_model.EntityQuery;
import de.onto_med.ontology_service.data_model.ReasonQuery;
import de.onto_med.ontology_service.manager.MetaProjectManager;
import de.onto_med.ontology_service.views.EntityFormView;
import de.onto_med.ontology_service.views.EntityResultsetView;
import de.onto_med.ontology_service.views.ProjectListView;
import de.onto_med.ontology_service.views.ReasonFormView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

/**
 * Project resource, which is accessible by the REST API.
 * Query relative URL "/" to get a JSON document with all available paths.
 * 
 * @author Christoph Beger
 */
@Path("/")
@Singleton
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
public class MetaProjectResource extends Resource {
	private final static Logger LOGGER = LoggerFactory.getLogger(MetaProjectResource.class);
	private ProjectResource projectResource;
	
	
	/**
	 * Constructor.
	 * @param dataPath path to WebProtégé's data folder.
	 */
	public MetaProjectResource(String dataPath, String mongoHost, int mongoPort) {
		metaProjectManager = new MetaProjectManager(dataPath, mongoHost, mongoPort);
	}

	public MetaProjectResource setProjectResource(ProjectResource pr) {
		projectResource = pr;
		return this;
	}
	
	/**
	 * Removes all entries of the ProjectManager cache.
	 * @return Response message
	 */
	@GET
	@Path("/clear_cache")
	public Response clearCache() {
		metaProjectManager.clearCache();
		return Response.seeOther(UriBuilder.fromUri(rootPath).build()).build();
	}
	
	/**
	 * Returns a list of public projects with condensed metadata.
	 * @return List of projects with metadata
	 */
	@GET
	@Path("/projects")
	public Response getProjectList(@Context HttpHeaders headers) {
		try {
			ArrayList<CondencedProject> projectList = metaProjectManager.getProjectList();
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(projectList).build();
			} else {
				return Response.ok(new ProjectListView(projectList, rootPath)).build();
			}
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}

	
	/**
	 * Searches one or many ontologies for matching entities.
	 * @param name localName part of an entity
	 * @param property property the searched entity is annotated with
	 * @param value property value
	 * @param type entity, class or individual
	 * @param ontologies list of project IDs separated by comma
	 * @param match matching method for name ('exact' or 'loose'), defaults to 'loose'
	 * @param operator logical operator to combine name and property, defaults to 'and')
	 * @return ArrayList of OWLEntityProperties or error message
	 */
	@GET
	@Path("/entity")
	public Response searchOntologyEntities(
		@Context HttpHeaders headers,
		@QueryParam("name")	String name,
		@QueryParam("iri") String iri,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@DefaultValue("entity") @QueryParam("type") String type,
		@QueryParam("ontologies") String ontologies,
		@DefaultValue("loose") @QueryParam("match") String match,
		@DefaultValue("and") @QueryParam("operator") String operator
	) {
		ArrayList<Entity> result = new ArrayList<>();
		
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property) && StringUtils.isEmpty(iri))
				throw new Exception("Neither query param 'name' nor 'iri', nor 'property' given.");
			
			for (String projectId : metaProjectManager.parseOntologies(ontologies)) {
				result.addAll(projectResource.searchOntologyEntities(
					projectId, name, iri, property, value, type, match, operator
				));
			}
		} catch (Exception e) {
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				EntityFormView view = new EntityFormView(
					rootPath, type, name, iri, property, value, match, operator, ontologies
				);
				view.addMessage("danger", e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(rootPath, result)).build();
		}
	}
	
	
	@POST
	@Path("/entity")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reason(@Context HttpHeaders headers, EntityQuery entityQuery) {
		return searchOntologyEntities(
			headers, entityQuery.getName(), entityQuery.getIri(), entityQuery.getProperty(),
			entityQuery.getValue(), entityQuery.getType(), entityQuery.getOntologies(), 
			entityQuery.getMatch(), entityQuery.getOperator()
		);
	}
	
	
	/**
	 * Reasons over the specified ontologies with supplied class expression
	 * @param ce class expression
	 * @param ontologies list of ontologies (optional)
	 * @return search result
	 */
	@GET
	@Path("/reason")
	public Response reason(
			@Context HttpHeaders headers,
			@QueryParam("ce") String ce,
			@QueryParam("ontologies") String ontologies
		) {
		ArrayList<Entity> result = new ArrayList<>();
		
		try {
			if (StringUtils.isEmpty(ce))
				throw new Exception("No class expression given.");
		
			for (String projectId : metaProjectManager.parseOntologies(ontologies)) {
				result.addAll(projectResource.reason(projectId, ce));
			}
		} catch (Exception e) {
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				ReasonFormView view = new ReasonFormView(rootPath, ce, ontologies);
				view.addMessage("danger", e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(rootPath, result)).build();
		}
	}
	
	@POST
	@Path("/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reason(@Context HttpHeaders headers, ReasonQuery reasonQuery) {
		return reason(headers, reasonQuery.getCe(), reasonQuery.getOntologies());
	}

	
	public MetaProjectManager getMetaProjectManager() {
		return metaProjectManager;
	}
	
}