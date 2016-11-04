package de.uni_leipzig.imise.webprotege.rest_api.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.core.NoContentException;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

public class ProjectManager {
	private KnowledgeBase kb;
	private Cls projectClass;
	private String dataPath;
	
	public ProjectManager(String dataPath) {
		kb = new Project(dataPath + "metaproject/metaproject.pprj", new ArrayList<String>()).getKnowledgeBase();
		projectClass = kb.getCls("Project");
		this.dataPath = dataPath;
	}
	
	public ArrayList<ProjectListEntry> getProjectList() {
		ArrayList<ProjectListEntry> list = new ArrayList<ProjectListEntry>();
		
		Iterator<Instance> iterator = getProjectInstances().iterator();
		while (iterator.hasNext()) {
			Instance project = iterator.next();
			
			ProjectListEntry entry = new ProjectListEntry();
			entry.id          = project.getName();
			entry.name        = (String) project.getOwnSlotValue(kb.getSlot("displayName"));
			entry.description = (String) project.getOwnSlotValue(kb.getSlot("description"));
			
			list.add(entry);
		}
		return list;
	}
	
	public OntologyManager getOntologyManager(String id) throws NoContentException {
		Instance project = getProjectInstance(id);
		
		if (project != null) 
			return new OntologyManager(project, dataPath);
		else 
			throw new NoContentException("Could not find project by id: '" + id + "'");
	}
	
	private ArrayList<Instance> getProjectInstances() {
		ArrayList<Instance> instances = new ArrayList<Instance>();
		
		Iterator<Instance> iterator = projectClass.getInstances().iterator();
		while (iterator.hasNext()) {
			Instance project = iterator.next();
			
			if (!isPublic(project)) continue;
			
			instances.add(project);
		}
		
		return instances;
	}
	
	private Instance getProjectInstance(String id) {
		Iterator<Instance> iterator = getProjectInstances().iterator();
		
		while (iterator.hasNext()) {
			Instance project = iterator.next();
			
			if (project.getName().equals(id)) return project;
		}
		
		return null;
	}
	
	private boolean isPublic(Instance project) {
		if ((Boolean) project.getOwnSlotValue(kb.getSlot("inTrash")))
			return false;
		
		@SuppressWarnings("unchecked")
		Collection<Instance> groupOperations = project.getOwnSlotValues(
			kb.getSlot("allowedGroupOperation")
		);
		
		Iterator<Instance> groupOperationIterator = groupOperations.iterator();
		while (groupOperationIterator.hasNext()) {
			Instance currentGroupOperation = groupOperationIterator.next();
			Instance currentGroup = (Instance) currentGroupOperation.getOwnSlotValue(
				kb.getSlot("allowedGroup")
			);
		
			@SuppressWarnings("unchecked")
			Collection<Instance> operations =  currentGroupOperation.getOwnSlotValues(
				kb.getSlot("allowedOperation")
			);
			
			Iterator<Instance> operationIterator = operations.iterator();	
			while (operationIterator.hasNext()) {
				Instance currentOperation = operationIterator.next();
				if (currentGroup.getBrowserText().equals("World")
						&& currentOperation.getBrowserText().equals("Read"))
					return true;
			}
		}
		return false;
	}
	
	public class ProjectListEntry {
		public String id;
		public String name;
		public String description;
	}
}
