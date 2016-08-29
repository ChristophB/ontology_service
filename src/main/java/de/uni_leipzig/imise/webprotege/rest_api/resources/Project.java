package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_leipzig.imise.webprotege.rest_api.api.OntologyManager;
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
	@Path("/projects")
	public ArrayList<ProjectListEntry> getOntologyList() {
		ProjectManager pm = new ProjectManager(dataPath);
		return pm.getProjectList();
	}

	@GET
	@Path("/project/{id}/{type}/{name}")
	public ArrayList<Object> getOntologyEntityByName(
		@PathParam("id") String id,
		@PathParam("type") String type,
		@PathParam("name") String name
	) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		try {
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
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.add(e.getMessage());
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
	
	@GET
	@Path("/project/{id}/{type}/hasProperty/{property}{value : (/.*)?}")
	public ArrayList<Object> getOntologyEntityWithProperty(
		@PathParam("id") String id,
		@PathParam("type") String type, 
		@PathParam("property") String property,
		@PathParam("value") String value
	) {
		ArrayList<Object> result = new ArrayList<Object>();
		value = value.replace("/", "");
		
		try {
			switch (type) {
				case "individual":
					result = getOntologyManager(id).getNamedIndividualPropertiesByProperty(property);
					break;
				case "class":
					result = getOntologyManager(id).getClassPropertiesByProperty(property);
					break;
				case "entity":
					result = getOntologyManager(id).getEntityPropertiesByProperty(property, value);
					break;
				default:
					throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.add(e.getMessage());
		}
		
		return result;
	}
	
	private OntologyManager getOntologyManager(String id) throws NoContentException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om;
	}
	
}