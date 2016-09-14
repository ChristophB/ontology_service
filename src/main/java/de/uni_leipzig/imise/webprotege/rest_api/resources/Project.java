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
				.addParameter("name",       "Entity name")
				.addParameter("property",   "Name of a Property, the entity is annotated with")
				.addParameter("value",      "Value of the specified Property")
				.addParameter("type",       "Entity, class or individual")
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
		@QueryParam("ontologies") String ontologies
	) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		if (type == null || type.isEmpty()) type = "entity";
		
		try {
			if ((name == null || name.isEmpty()) && (property == null || property.isEmpty()))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			for (String id : parseOntologies(ontologies)) {
				if (name != null && !name.isEmpty()) {
					result = searchOntologyEntityByName(id, type, name);
					if (property != null && !property.isEmpty())
						result.retainAll(searchOntologyEntityByProperty(id, type, property, value));
				} else if (property != null && !property.isEmpty()) {
					result = searchOntologyEntityByProperty(id, type, property, value);
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
		@PathParam("id") String id,
		@PathParam("type") String type,
		@PathParam("name") String name
	) throws NoContentException, Exception {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		switch (type) {
			case "entity":
				result = getOntologyManager(id).getEntityPropertiesByName(name);
				break;
			case "individual":
				result = getOntologyManager(id).getNamedIndividualPropertiesByName(name);
				break;
			case "class":
				result = getOntologyManager(id).getClassPropertiesByName(name);
				break;
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
		
		return result;
	}
	
	public ArrayList<OWLEntityProperties> searchOntologyEntityByProperty(
		@PathParam("id") String id,
		@PathParam("type") String type, 
		@PathParam("property") String property,
		@QueryParam("value") String value
	) throws NoContentException, Exception {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		switch (type) {
			case "individual":
				result = getOntologyManager(id).getNamedIndividualPropertiesByProperty(property, value);
				break;
			case "class":
				result = getOntologyManager(id).getClassPropertiesByProperty(property, value);
				break;
			case "entity":
				result = getOntologyManager(id).getEntityPropertiesByProperty(property, value);
				break;
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
		
		return result;
	}
	
	
	
	
	private List<String> parseOntologies(String ontologies) {
		if (ontologies == null || ontologies.equals("")) {
			logger.info("No ontologies given, using all.");
			
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