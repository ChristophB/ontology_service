package de.uni_leipzig.imise.webprotege.rest_api.ontology;

import java.util.ArrayList;
import java.util.Collection;
import javax.ws.rs.core.NoContentException;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

/**
 * This class provides information about existing projects in WebProtegé.
 * 
 * @author Christoph Beger
 */
public class ProjectManager {
	/**
	 * Knowledgebase of WebProtegé which contains all project meta informations.
	 * Typically location: [webprotege-data]/metaproject/metaproject.pprj
	 */
	private KnowledgeBase kb;
	/**
	 * Ontological class in knowledgebase for projects.
	 */
	private Cls projectClass;
	/**
	 * Path to WebProtegés data folder.
	 */
	private String dataPath;
	
	
	
	/**
	 * Constructor
	 * @param dataPath Path to WebProtegés data folder.
	 */
	public ProjectManager(String dataPath) {
		kb = new Project(dataPath + "metaproject/metaproject.pprj",	new ArrayList<String>()).getKnowledgeBase();
		projectClass = kb.getCls("Project");
		this.dataPath = dataPath;
	}
	
	
	/**
	 * Returns a list of all available public readable projects, stored in WebProtegé.
	 * @return List of projects
	 */
	public ArrayList<ProjectListEntry> getProjectList() {
		ArrayList<ProjectListEntry> list = new ArrayList<ProjectListEntry>();
		
		for (Instance project : getProjectInstances()) {
			list.add(new ProjectListEntry(
				project.getName(),
				(String) project.getOwnSlotValue(kb.getSlot("displayName")),
				(String) project.getOwnSlotValue(kb.getSlot("description"))
			));
		}
		
		return list;
	}

	
	/**
	 * Returns an OntologyManager for a given id, if project with specified id exists and is public.
	 * @param projectId id of a project
	 * @return OntologyManager for project with specified id
	 * @throws Exception If no public project with matching id was found or ontology was not parsable
	 */
	public OntologyManager getOntologyManager(String projectId) throws Exception {
		Instance project = getProjectInstance(projectId);
		
		if (project != null) 
			return new OntologyManager(project, dataPath);
		else 
			throw new NoContentException("Could not find project by id: '" + projectId + "'");
	}
	
	
	/**
	 * Returns the ontological instances of all public projects in WebProtegé.
	 * @return list of instances of class Project of the knowledgebase
	 */
	private ArrayList<Instance> getProjectInstances() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		for (Instance project : projectClass.getInstances())
			if (isPublic(project))
				instances.add(project);
		
		return instances;
	}

	
	/**
	 * Returns an ontological instance of a public project for a given projectid.
	 * @param projectId id of an instance of class Project of the knowledgebase
	 * @return Instance of class Project or null if no matching instance was found
	 */
	private Instance getProjectInstance(String projectId) {
		for (Instance project : getProjectInstances())
			if (project.getName().equals(projectId))
				return project;
		
		return null;
	}

	
	/**
	 * Checks if an project in WebProtegé is public.
	 * A project is public if it is not in trash
	 * and if it has allowedOperation "Read" for allowedGroup "World"
	 * @param project instance of class Project of the knowledgebase
	 * @return true if the project is public, else false
	 */
	@SuppressWarnings("unchecked")
	private boolean isPublic(Instance project) {
		if ((Boolean) project.getOwnSlotValue(kb.getSlot("inTrash")))
			return false;
		
		Collection<Instance> groupOperations = project.getOwnSlotValues(kb.getSlot("allowedGroupOperation"));
		for (Instance groupOperation : groupOperations) {
			Instance group = (Instance)groupOperation.getOwnSlotValue(
				kb.getSlot("allowedGroup")
			);
			if (!group.getBrowserText().equals("World")) continue;
			
			Collection<Instance> operations = groupOperation.getOwnSlotValues(kb.getSlot("allowedOperation"));
			for (Instance operation : operations)
				if (operation.getBrowserText().equals("Read"))
					return true;
		}
		return false;
	}

	
	
	/**
	 * This class represents a project by providing a condensed set of metadata.
	 * @author Christoph Beger
	 */
	public class ProjectListEntry {
		public String id;
		public String name;
		public String description;
		
		public ProjectListEntry() {};
		
		public ProjectListEntry(String id, String name, String description) {
			this.id          = id;
			this.name        = name;
			this.description = description;
		}
	}
}
