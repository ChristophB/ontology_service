package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.project.MetaProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.project.WebProtegeProject;
import de.uni_leipzig.imise.webprotege.rest_api.views.WebProtegeProjectListView;

/**
 * Project resource, which is accessible by the REST API.
 * Query relative URL "/" to get a JSON document with all available paths.
 * 
 * @author Christoph Beger
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class MetaProjectResource extends Resource {
	/**
	 * Constructor.
	 * @param dataPath path to WebProtegés data folder.
	 */
	public MetaProjectResource(String dataPath) {
		super(dataPath);
	}
	
	
	/**
	 * Returns a list of public projects with condensed metadata.
	 * @return List of projects with metadata
	 */
	@GET
	@Path("/projects")
	@Produces(MediaType.TEXT_HTML)
	public WebProtegeProjectListView getProjectList() {
		return new WebProtegeProjectListView(getOntologyList());
	}
	
	private ArrayList<WebProtegeProject> getOntologyList() {
		MetaProjectManager pm = new MetaProjectManager(dataPath);
		return pm.getProjectList();
	}

	
	/**
	 * Searches one or many ontologies for mathing entities.
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
	@Path("/entity")
	public Object searchOntologyEntities(
		@QueryParam("name")	String name,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@QueryParam("type") String type,
		@QueryParam("ontologies") String ontologies,
		@QueryParam("match") String match,
		@QueryParam("operator") String operator
	) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			for (String projectId : parseOntologies(ontologies)) {
				result.addAll(new OntologyResource(dataPath).searchOntologyEntities(
					projectId, name, property, value, type, match, operator
				));
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
		
		return result;
	}
	
	
	
	/**
	 * Reasons over the specified ontologies with supplied classexpression
	 * @param ce class expression
	 * @param ontologies list of ontologies (optional)
	 * @return search result
	 */
	@GET
	@Path("/reason")
	public ArrayList<OWLEntityProperties> reason(@QueryParam("ce") String ce, @QueryParam("ontologies") String ontologies) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			if (StringUtils.isEmpty(ce))
				throw new Exception("No class expression given.");
		
			for (String projectId : parseOntologies(ontologies)) {
				OntologyResource or = new OntologyResource(dataPath);
				result.addAll(or.reason(projectId, ce));
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
		
		return result;
	}

	
	
	/**
	 * Parses a string of projectids separated by comma and returns a list of projectids.
	 * If the string is empty, this function returns a list of all public projects.
	 * @param ontologies String of projectids separated by comma
	 * @return List of projectids
	 */
	private List<String> parseOntologies(String ontologies) {
		if (StringUtils.isEmpty(ontologies)) {
			List<String> ontologyList = new ArrayList<String>();
			for (WebProtegeProject entry : getOntologyList()) {
				ontologyList.add(entry.getProjectId());
			}
			return ontologyList;
		} else {
			return Arrays.asList(ontologies.split(","));
		}
	}
	
}