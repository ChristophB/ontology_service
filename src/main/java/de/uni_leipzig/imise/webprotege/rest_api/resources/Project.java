package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.api.PathDocumentation;
import de.uni_leipzig.imise.webprotege.rest_api.ontology.OntologyManager;
import de.uni_leipzig.imise.webprotege.rest_api.ontology.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.ontology.ProjectManager.ProjectListEntry;

/**
 * Project resource, which is accessible by the REST API.
 * Query relative URL "/" to get a JSON document with all available paths.
 * 
 * @author Christoph Beger
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class Project {
	final static Logger logger = LoggerFactory.getLogger(Project.class);
	
	/**
	 * Path to WebProtegés data folder.
	 */
	private String dataPath;
	
	
	
	/**
	 * Constructor.
	 * @param dataPath path to WebProtegés data folder.
	 */
	public Project(String dataPath) {
		this.dataPath = dataPath;
	}
	
	
	/**
	 * Returns documentation as list.
	 * @return List of possible relative paths and parameters
	 */
	@GET
	@Path("/")
	public ArrayList<Object> getDocumentation() {
		ArrayList<Object> documentation = new ArrayList<Object>();
		
		documentation.add(
			new PathDocumentation("/entity", "Search for a single or multiple entities.")
				.addParameter("type",       "Entity, class or individual")
				.addParameter("name",       "Entity name")
				.addParameter("match",      "Match method for 'name' parameter: 'exact' or 'loose' (default: loose)")
				.addParameter("property",   "Name of a Property, the entity is annotated with") // @toto: property -> properties
				.addParameter("value",      "Value of the specified Property")
				.addParameter("operator",   "Logical operator to combine 'name' and 'property' (default: and)")
				.addParameter("ontologies", "List of comma separated ontology ids (default: all ontologies)")	
		);
			
		documentation.add(
			new PathDocumentation("/projects", "List all available projects/ontologies with a short description and id.")
		);
				
		documentation.add(
			new PathDocumentation("/project/{id}/imports", "List all imports of the specified ontology.")
		);
		
		return documentation;
	}
	
	
	/**
	 * Returns a list of public projects with condensed metadata.
	 * @return List of projects with metadata
	 */
	@GET
	@Path("/projects")
	public ArrayList<ProjectListEntry> getOntologyList() {
		ProjectManager pm = new ProjectManager(dataPath);
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
			
			for (String id : parseOntologies(ontologies)) {
				ArrayList<OWLEntityProperties> tempResult = new ArrayList<OWLEntityProperties>();
				
				if (StringUtils.isNotEmpty(name)) {
					tempResult = searchOntologyEntityByName(id, type, name, match);
				}
				
				if (StringUtils.isNotEmpty(property)) {
					if ("or".equals(operator) || StringUtils.isEmpty(name))
						tempResult.addAll(searchOntologyEntityByProperty(id, type, property, value, match));
					else
						tempResult.retainAll(searchOntologyEntityByProperty(id, type, property, value, match));	
				}
				
				result.addAll(tempResult);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
		
		return result;
	}
	
	
	/**
	 * Returns a list of imported ontologies for a specified project.
	 * @param projectid IF of the WebProtegé project
	 * @return List of imported ontologies or error message
	 */
	@GET
	@Path("/project/{id}/imports")
	public Object getOntologyImports(@PathParam("id") String projectid) {
		try {
			return getOntologyManager(projectid).getOntologyImports();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
	}
	
	
	/**
	 * Searches for OWLEntities with given type, and name.
	 * @param projectid ID of the WebProtegé project
	 * @param type 'entity', 'individual' or 'class', defaults to 'entity'
	 * @param name localename of the entity to search for
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of OWLEntityProperties for found entities
	 * @throws Exception If the specified type is not one of 'entity', 'individual' and 'class', or the project was not found
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntityByName(
		String projectid, String type, String name, String match
	) throws Exception {
		OntologyManager manager = getOntologyManager(projectid);
		if (StringUtils.isEmpty(type)) type = "entity";
		
		switch (type) {
			case "entity":
				return manager.getEntityPropertiesByName(null, name, match);
			case "individual":
				return manager.getNamedIndividualPropertiesByName(null, name, match);
			case "class":
				return manager.getClassPropertiesByName(null, name, match);
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
	}
	
	
	/**
	 * Searches for OWLEntities which are annotated with a property with given name.
	 * @param projectid ID of the WebProtegé project
	 * @param type 'entity', 'individual' or 'class', defaults to 'entity'
	 * @param property localename of the property
	 * @param value with property annotated value or null for no value check
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of OWLEntityProperties for found entities
	 * @throws Exception If the specified type is not one of 'entity', 'individual' and 'class', or the project was not found
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntityByProperty(
		String projectid, String type, String property, String value, String match
	) throws Exception {
		OntologyManager manager = getOntologyManager(projectid);
		if (StringUtils.isEmpty(type)) type = "entity";
		
		switch (type) {
			case "individual":
				return manager.getNamedIndividualPropertiesByProperty(null, property, value);
			case "class":
				return manager.getClassPropertiesByProperty(null, property, value);
			case "entity":
				return manager.getEntityPropertiesByProperty(null, property, value);
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
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
			for (ProjectListEntry entry : getOntologyList()) {
				ontologyList.add(entry.id);
			}
			return ontologyList;
		} else {
			return Arrays.asList(ontologies.split(","));
		}
	}
	
	
	/**
	 * Returns the OntologyManager for a given projectid
	 * @param projectid ID of a WebProtegé project
	 * @return OntologyManager
	 * @throws NoContentException If no public project exists with specified id.
	 */
	private OntologyManager getOntologyManager(String projectid) throws NoContentException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(projectid);
		
		return om;
	}
	
}