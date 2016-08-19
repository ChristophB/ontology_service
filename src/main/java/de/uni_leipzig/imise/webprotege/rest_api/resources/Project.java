package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;

import de.uni_leipzig.imise.webprotege.rest_api.api.OntologyManager;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager.ProjectListEntry;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class Project {
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
	public ArrayList<Object> getOntologyClass(@PathParam("id") String id, @PathParam("class") String cls) throws Exception {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getClassPropertiesByName(cls);
	}
	
	@GET
	@Path("/project/{id}/individual/{individual}")
	public ArrayList<Object> getOntologyIndividual(@PathParam("id") String id, @PathParam("individual") String individual) throws Exception {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getNamedIndividualPropertiesByName(individual);
	}
	
	@GET
	@Path("/project/{id}/imports")
	public ArrayList<String> getOntologyImports(@PathParam("id") String id) throws UnknownOWLOntologyException, OWLOntologyCreationException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getOntologyImports();
	}
	
	@GET
	@Path("/project/{id}/class/hasProperty/{property}")
	public ArrayList<Object> getOntologyClassWithProperty(@PathParam("id") String id, @PathParam("property") String property) throws Exception {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getClassPropertiesByProperty(property);
	}
	
	@GET
	@Path("/project/{id}/individual/hasProperty/{property}")
	public ArrayList<Object> getOntologyNamedIndividualWithProperty(@PathParam("id") String id, @PathParam("property") String property) throws Exception {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getNamedIndividualPropertiesByProperty(property);
	}
	
}