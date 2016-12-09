package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.ontology.OntologyManager;
import de.uni_leipzig.imise.webprotege.rest_api.project.MetaProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.views.WebProtegeProjectView;

/**
 * This class provids all ontology specific tasks.
 * @author Christoph Beger
 */
@Path("/project")
public class OntologyResource extends Resource {
	/**
	 * Constructor.
	 * @param dataPath path to WebProtegés data folder.
	 */
	public OntologyResource(String dataPath) {
		super(dataPath);
	}
	
	
	/**
	 * Returns a list of imported ontologies for a specified project.
	 * @param projectid ID of the WebProtegé project
	 * @return List of imported ontologies or error message
	 */
	@GET
	@Path("/{id}/imports")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Object getOntologyImports(@PathParam("id") String projectId) {
		try {
			return getOntologyManager(projectId).getOntologyImports();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
	}
	

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public WebProtegeProjectView getProject(@PathParam("id") String projectId) throws Exception {
		return new WebProtegeProjectView(new MetaProjectManager(dataPath).getOntologyManager(projectId));
	}
	
	
	/**
	 * Returns full OWL document as RDF/XML.
	 * @param projectid ID of the WebProtegé project
	 * @return
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
	public Object getFullRDFDocument(@PathParam("id") String projectId) {
		try {
			return getOntologyManager(projectId).getFullRDFDocument();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
	}


	/**
	 * Reasons over the specified ontologies with supplied classexpression
	 * @param id projectId of the project
	 * @param ce class expression
	 * @return search result
	 */
	@GET
	@Path("/{id}/reason")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ArrayList<OWLEntityProperties> reason(@PathParam("id") String projectId, @QueryParam("ce") String ce) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			OntologyManager manager = getOntologyManager(projectId);
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
	@GET
	@Path("/{id}/entity")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public ArrayList<OWLEntityProperties> searchOntologyEntities(
		@PathParam("id") String projectId,
		@QueryParam("name")	String name,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@QueryParam("type") String type,
		@QueryParam("match") String match,
		@QueryParam("operator") String operator
	) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			ArrayList<OWLEntityProperties> tempResult = new ArrayList<OWLEntityProperties>();
			OntologyManager manager = getOntologyManager(projectId);
				
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
