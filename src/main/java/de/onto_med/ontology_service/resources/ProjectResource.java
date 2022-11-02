package de.onto_med.ontology_service.resources;

import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.imise.ontomed.owl2graphml.onto.MainOntology;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.onto_med.ontology_service.data_model.Entity;
import de.onto_med.ontology_service.data_model.Individual;
import de.onto_med.ontology_service.data_model.Project;
import de.onto_med.ontology_service.manager.ProjectManager;
import de.onto_med.ontology_service.views.ProjectTaxonomyView;
import de.onto_med.ontology_service.views.ProjectView;
import de.onto_med.ontology_service.views.SimpleListView;

/**
 * This class provides all ontology specific tasks.
 * @author Christoph Beger
 */
@Path("/project")
@Singleton
public class ProjectResource extends Resource {
	private final static Logger logger = LoggerFactory.getLogger(ProjectResource.class);
	private String webprotegeRelativeToWebroot;
	
	public ProjectResource(String webprotegeRelativeToWebroot) {
		this.webprotegeRelativeToWebroot = webprotegeRelativeToWebroot;
	}
	/**
	 * Returns a list of imported ontologies for a specified project.
	 * @param headers Headers of the HTTP request
	 * @param projectId ID of the WebProtégé project
	 * @return List of imported ontologies or error message
	 */
	@GET
	@Path("/{id}/imports")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getOntologyImportsJson(@Context HttpHeaders headers, @PathParam("id") String projectId) {
		try {
			List<String> importedOntologyIds = getProjectManager(projectId).getImportedOntologyIds();
			if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(importedOntologyIds).build();
			} else {
				return Response.ok(new SimpleListView(rootPath, importedOntologyIds, "Imported Ontologies")).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	/**
	 * Transforms the projects ontologies into GraphML and returns the result as string.
	 * @param id ID of the WebProtégé project
	 * @return GraphML
	 */
	@GET
	@Path("/{id}/graphml")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getGraphMl(
		@Context HttpHeaders headers, @PathParam("id") String id,
		@QueryParam("start-class") String startClassIri,
		@QueryParam("taxonomy-direction") String taxonomyDirection,
		@QueryParam("taxonomy-depth") int taxonomyDepth,
		@QueryParam("has-restriction-super-classes") String hasRestrictionSuperClasses,
		@QueryParam("has-grayscale") boolean hasGreyScale, @QueryParam("has-taxonomy") boolean hasTaxonomy,
		@QueryParam("has-annotations") boolean hasAnnotations, @QueryParam("has-property-definitions") boolean hasPropertyDefinitions,
		@QueryParam("has-anonymous-super-classes") boolean hasAnonymousSuperClasses, @QueryParam("has-equivalent-classes") boolean hasEquivalentClasses,
		@QueryParam("has-individuals") boolean hasIndividuals, @QueryParam("has-individual-types") boolean hasIndividualTypes,
		@QueryParam("has-individual-assertions") boolean hasIndividualAssertions
	) {
		ProjectManager manager;
		try {
			manager = getProjectManager(id);
		} catch (Exception e) { throw new WebApplicationException(e.getMessage()); }

		MainOntology graphMlOntology = manager.getGraphMl(startClassIri, taxonomyDirection, taxonomyDepth);

		graphMlOntology.setHasGrayscale(hasGreyScale);
		graphMlOntology.setHasRestrictionSuperClassesWithType("with type".equals(hasRestrictionSuperClasses));

		if (hasTaxonomy)
			graphMlOntology.addTaxonomy();
		if (hasAnnotations)
			graphMlOntology.addAnnotations();
		if (!"No".equals(hasRestrictionSuperClasses))
			graphMlOntology.addPropertyRestrictionSuperClasses();
		if (hasAnonymousSuperClasses)
			graphMlOntology.addAnonymousSuperClasses("No".equals(hasRestrictionSuperClasses));
		if (hasEquivalentClasses)
			graphMlOntology.addEquivalentClasses();
		if (hasIndividuals)
			graphMlOntology.addIndividuals();
		if (hasIndividualTypes)
			graphMlOntology.addIndividualTypes();
		if (hasIndividualAssertions)
			graphMlOntology.addIndividualAssertions();
		if (hasPropertyDefinitions)
			graphMlOntology.addPropertyDefinitions();

		return Response
			.ok(graphMlOntology.toXml().toString())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + manager.getProjectShortForm() + ".GraphML'")
			.build();
	}
	
	/**
	 * Show form to specify options for GraphML generation.
	 * @param id ID or IRI of the WebProtégé project
	 * @return HTML page
	 */
	@GET
	@Path("{id}/graphml-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getGraphMlForm(@Context UriInfo uriInfo, @PathParam("id") String id) {
		ProjectManager manager;
		try {
			manager = getProjectManager(id);
		} catch (Exception e) { throw new WebApplicationException(e.getMessage()); }

		return Response.ok(new ProjectView("ProjectGraphMlForm.ftl", manager, rootPath, uriInfo.getBaseUri().getHost())).build();
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
			if (acceptsMediaType(headers, MediaType.TEXT_HTML_TYPE)) {
				return Response.ok(new ProjectTaxonomyView(project, rootPath, uriInfo.getBaseUri().getHost() + webprotegeRelativeToWebroot)).build();
			} else {
				return Response.ok(project.getTaxonomy()).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	
	/**
	 * Returns some generic information about project's ontology
	 * @param headers HTTP headers of the request
	 * @param uriInfo Info about called URI
	 * @param projectId ID of the WebProtégé project
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
				return Response.ok(new ProjectView(project, rootPath, uriInfo.getBaseUri().getHost() + webprotegeRelativeToWebroot)).build();
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}
	
	
	/**
	 * Returns full OWL document as RDF/XML.
	 * @param projectId ID of the WebProtégé project
	 * @return JSON response
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFullRDFDocument(@PathParam("id") String projectId) {
		try {
			ProjectManager pm = getProjectManager(projectId);
			return Response.ok(pm.getFullRdfDocument())
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
	 * Reasons over the specified ontologies with supplied class expression.
	 * @param projectId projectId of the project
	 * @param ce class expression
	 * @return search result
	 */
	public List<Entity> reason(String projectId, String ce) {
		try {
			ProjectManager manager = getProjectManager(projectId);

			Map<String, String> shortFormMap = manager.getOntologyIris();
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
	 * Searches for matching entities in this ontology.
	 * @param name localName part of an entity
	 * @param property property the searched entity is annotated with
	 * @param value property value
	 * @param type entity, class or individual
	 * @param projectId ID of the project to search in
	 * @param iri IRI to search for
	 * @param match matching method for name ('exact' or 'loose'), defaults to 'loose'
	 * @param operator logical operator to combine name and property, defaults to 'and')
	 * @return ArrayList of OWLEntityProperties or error message
	 */
	public List<Entity> searchOntologyEntities(
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
