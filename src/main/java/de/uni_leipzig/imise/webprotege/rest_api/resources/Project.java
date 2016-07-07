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
import de.uni_leipzig.imise.webprotege.rest_api.api.OntologyManager.OWLClassProperties;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.api.ProjectManager.ProjectListEntry;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
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
	public ArrayList<OWLClassProperties> getOntologyClass(@PathParam("id") String id, @PathParam("class") String cls) throws OWLOntologyCreationException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getClassProperties(cls);
	}
	
	@GET
	@Path("/project/{id}/imports")
	public ArrayList<String> getOntologyImports(@PathParam("id") String id) throws UnknownOWLOntologyException, OWLOntologyCreationException {
		ProjectManager pm = new ProjectManager(dataPath);
		OntologyManager om = pm.getOntologyManager(id);
		
		return om.getOntologyImports();
	}
}