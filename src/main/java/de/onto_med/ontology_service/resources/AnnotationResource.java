package de.onto_med.ontology_service.resources;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.onto_med.ontology_service.api.json.AnnotationQuery;
import de.onto_med.ontology_service.api.json.Entity;
import de.onto_med.ontology_service.manager.MetaProjectManager;
import de.onto_med.ontology_service.manager.ProjectManager;
import de.onto_med.ontology_service.ontology.BinaryOwlParser;
import de.onto_med.ontology_service.views.EntityResultsetView;

/**
 * This resource adds an annotation service to the application.
 * One can provide a text and (optionally) ontologies.
 * The service will search for appropriate classes in ontologies.
 * @author Christoph Beger
 */
@Path("/annotate")
@Produces({ MediaType.APPLICATION_JSON })
public class AnnotationResource extends Resource {
	private static final int LENGTH_THRESHOLD = 4;
	
	/**
	 * Constructor of AnnotationResource.
	 * @param metaProjectManager a MetaProjectManager
	 */
	public AnnotationResource(MetaProjectManager metaProjectManager) {
		super(metaProjectManager);
	}
	
	/**
	 * GET method to access the annotation functionality of this resource.
	 * @param headers 		provided headers
	 * @param text 			text to annotate
	 * @param ontologies 	ontologies to search in (optional)
	 * @return
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response get(@Context HttpHeaders headers, @QueryParam("text") String text, @QueryParam("ontologies") String ontologies) {
		List<Entity> resultset = annotate(text, ontologies);
		
		if (acceptsMediaType(headers, MediaType.TEXT_HTML_TYPE)) {
			return Response.ok(new EntityResultsetView(rootPath, resultset)).build();
		} else {
			return Response.ok(resultset).build();
		}
	}
	
	/**
	 * POST method to access the annotation functionality of this resource.
	 * @param headers 		provided headers
	 * @param text 			text to annotate
	 * @param ontologies 	ontologies to search in (optional)
	 * @return
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response post(@Context HttpHeaders headers, AnnotationQuery query) {
		List<Entity> resultset = annotate(query.getText(), query.getOntologies());
		
		if (acceptsMediaType(headers, MediaType.TEXT_HTML_TYPE)) {
			return Response.ok(new EntityResultsetView(rootPath, resultset)).build();
		} else {
			return Response.ok(resultset).build();
		}
	}
	
	/**
	 * This method annotates an input text (word based) and returns all found classes.
	 * @param text			text to annotate
	 * @param ontologies	ontologies to search in (optional)
	 * @return list of found classes
	 */
	private List<Entity> annotate(String text, String ontologies) {
		checkArgument(!StringUtils.isBlank(text), "Parameter 'text' is empty or blank");
		
		try {
			List<Entity> classes = new ArrayList<Entity>();
			
			for (String ontology : metaProjectManager.parseOntologies(ontologies)) {
				ProjectManager projectManager = metaProjectManager.getProjectManager(ontology);
				BinaryOwlParser parser = projectManager.getBinaryOwlParser();
				
				for (String word : text.split("\\s+")) {
					if (word.length() < AnnotationResource.LENGTH_THRESHOLD) continue;
					classes.addAll(parser.annotate(word, false));
				}
			}
			
			return classes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebApplicationException(e.getLocalizedMessage());
		}
	}
	
}
