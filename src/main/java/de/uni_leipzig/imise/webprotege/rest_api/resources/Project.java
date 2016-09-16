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

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.api.OntologyManager;
import de.uni_leipzig.imise.webprotege.rest_api.api.PathDocumentation;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager.ProjectListEntry;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class Project {
	final static Logger logger = LoggerFactory.getLogger(Project.class);
	
	private String dataPath;

	public Project(String dataPath) throws OWLOntologyCreationException {
		this.dataPath = dataPath;
	}
	
	@GET
	@Path("/")
	public ArrayList<Object> getDocumentation() {
		ArrayList<Object> documentation = new ArrayList<Object>();
		
		documentation.add(
			new PathDocumentation("/entity", "Search for a single or multiple entities.")
				.addParameter("type",       "Entity, class or individual")
				.addParameter("name",       "Entity name")
				.addParameter("match",      "Match method for 'name' parameter: 'exact' or 'loose' (default)")
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
	
	@GET
	@Path("/projects")
	public ArrayList<ProjectListEntry> getOntologyList() {
		ProjectManager pm = new ProjectManager(dataPath);
		return pm.getProjectList();
	}

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
		
		if (type == null || type.isEmpty()) type = "entity";
		if (match == null || match.isEmpty()) match = "loose";
		if (operator == null || operator.isEmpty()) operator = "and";
		
		try {
			if ((name == null || name.isEmpty()) && (property == null || property.isEmpty()))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			for (String id : parseOntologies(ontologies)) {
				if (name != null && !name.isEmpty()) {
					result = searchOntologyEntityByName(null, id, type, name, match);
					if (property != null && !property.isEmpty())
						if (operator.equals("or"))
							result.addAll(searchOntologyEntityByProperty(null, id, type, property, value, match));
						else
							result.retainAll(searchOntologyEntityByProperty(result, id, type, property, value, match)); 
				} else if (property != null && !property.isEmpty()) {
					result = searchOntologyEntityByProperty(null, id, type, property, value, match);
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			return e.getMessage();
		}
		
		return result;
	}
	
	@GET
	@Path("/project/{id}/imports")
	public ArrayList<String> getOntologyImports(@PathParam("id") String id) {
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			result = getOntologyManager(id).getOntologyImports();
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.add(e.getMessage());
		}
		
		return result;
	}
	
	
	
	
	public ArrayList<OWLEntityProperties> searchOntologyEntityByName(
		ArrayList<OWLEntityProperties> set, String id, String type, String name, String match
	) throws Exception {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		OntologyManager manager = getOntologyManager(id);
		
		ArrayList<String> iriSet = new ArrayList<String>();
		if (set != null)
			for (OWLEntityProperties entity : set) {
				iriSet.add(entity.iri);
			}
		if (iriSet.isEmpty()) iriSet = null;
		
		switch (type) {
			case "entity":
				result = manager.getEntityPropertiesByName(iriSet, name, match);
				break;
			case "individual":
				result = manager.getNamedIndividualPropertiesByName(iriSet, name, match);
				break;
			case "class":
				result = manager.getClassPropertiesByName(iriSet, name, match);
				break;
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
		
		return result;
	}
	
	public ArrayList<OWLEntityProperties> searchOntologyEntityByProperty(
		ArrayList<OWLEntityProperties> set, String id, String type, String property, String value, String match
	) throws NoContentException, Exception {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		OntologyManager manager = getOntologyManager(id);
		
		ArrayList<String> iriSet = new ArrayList<String>();
		if (set != null)
			for (OWLEntityProperties entity : set) {
				iriSet.add(entity.iri);
			}
		if (iriSet.isEmpty()) iriSet = null;
		
		switch (type) {
			case "individual":
				result = manager.getNamedIndividualPropertiesByProperty(iriSet, property, value);
				break;
			case "class":
				result = manager.getClassPropertiesByProperty(iriSet, property, value);
				break;
			case "entity":
				result = manager.getEntityPropertiesByProperty(iriSet, property, value);
				break;
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
		
		return result;
	}
	
	
	
	
	private List<String> parseOntologies(String ontologies) {
		if (ontologies == null || ontologies.equals("")) {
			List<String> ontologyList = new ArrayList<String>();
			for (ProjectListEntry entry : getOntologyList()) {
				ontologyList.add(entry.id);
			}
			return ontologyList;
		} else {
			return Arrays.asList(ontologies.split(","));
		}
	}
	
	private OntologyManager getOntologyManager(String id) throws NoContentException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om;
	}
	
}