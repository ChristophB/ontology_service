package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
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
		
		documentation.add(
			new PathDocumentation("/project/{id}", "Get full OWL document as RDF/XML.")
		);
		
		documentation.add(
			new PathDocumentation("/reason", "Search for individuals by reasoning")
				.addParameter("ce", "Class expression (currently not working with short forms, use full IRIs instead)")
				.addParameter("ontologies", "List of comma separated ontology ids (default: all ontologies)")
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
				OntologyManager manager = getOntologyManager(id);
				
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
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
		
		return result;
	}
	
	
	/**
	 * Returns a list of imported ontologies for a specified project.
	 * @param projectid ID of the WebProtegé project
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
	 * Returns full OWL document as RDF/XML.
	 * @param projectid ID of the WebProtegé project
	 * @return
	 */
	@GET
	@Path("/project/{id}")
	public Object getFullRDFDocument(@PathParam("id") String projectid) {
		try {
			return getOntologyManager(projectid).getFullRDFDocument();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
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
	
	
	@GET
	@Path("/reason")
	public Object reasonClassExpression(@QueryParam("ce") String ce, @QueryParam("ontologies") String ontologies) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		try {
			if (StringUtils.isEmpty(ce))
				throw new Exception("No class expression given.");
		
			for (String id : parseOntologies(ontologies)) {
				OntologyManager manager = getOntologyManager(id);
				result.addAll(manager.getIndividualPropertiesByClassExpression(ce));
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
		
		return result;
	}
	
	/**
	 * Returns the OntologyManager for a given projectid
	 * @param projectid ID of a WebProtegé project
	 * @return OntologyManager
	 * @throws Exception If ontology for given projectid could not be parsed
	 */
	private OntologyManager getOntologyManager(String projectid) throws Exception {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(projectid);
		
		return om;
	}
	
}