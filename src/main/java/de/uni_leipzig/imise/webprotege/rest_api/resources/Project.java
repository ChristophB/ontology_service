package de.uni_leipzig.imise.webprotege.rest_api.resources;

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
	@Path("/project/{id}/class/{class}")
	public ArrayList<Object> getOntologyClass(@PathParam("id") String id, @PathParam("class") String cls) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		try {
			result = getOntologyManager(id).getClassPropertiesByName(cls);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.add(e.getMessage());
		}
		
		return result;
	}
	
	@GET
	@Path("/project/{id}/individual/{individual}")
	public ArrayList<Object> getOntologyIndividual(@PathParam("id") String id, @PathParam("individual") String individual) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		try {
			result = getOntologyManager(id).getNamedIndividualPropertiesByName(individual);
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
	@Path("/project/{id}/class/hasProperty/{property}")
	public ArrayList<Object> getOntologyClassWithProperty(@PathParam("id") String id, @PathParam("property") String property) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		try {
			result = getOntologyManager(id).getClassPropertiesByProperty(property);
		} catch (Exception e) {
			logger.warn(e.getMessage());
			result.add(e.getMessage());
		}
		
		return result;
	}
	
	@GET
	@Path("/project/{id}/individual/hasProperty/{property}")
	public ArrayList<Object> getOntologyNamedIndividualWithProperty(@PathParam("id") String id, @PathParam("property") String property) {
		ArrayList<Object> result = new ArrayList<Object>();
		
		try {
			result = getOntologyManager(id).getNamedIndividualPropertiesByProperty(property);
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