package de.onto_med.ontology_service.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import de.onto_med.ontology_service.manager.ProjectManager;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;

/**
 * This class can be used to parse an existing Protégé metaproject file (*.pprj).
 * @author Christoph Beger
 */
public class PprjParser extends OntologyParser {
	private Supplier<KnowledgeBase> knowledgeBaseSupplier;
	
	
	/**
	 * Constructor
	 * @param dataPath path to WebProtégé data folder
	 */
	public PprjParser(String dataPath) {
		super(dataPath);
		
		knowledgeBaseSupplier = Suppliers.memoizeWithExpiration(
			getKnowledgeBaseSupplier(dataPath + "metaproject/metaproject.pprj"), 1, TimeUnit.MINUTES
		);
	}
	
	
	/**
	 * Returns the ontological instances of all public projects in WebProtegé.
	 * @return list of instances of class Project of the knowledgebase
	 */
	public Collection<Instance> getProjectInstances() {
		Collection<Instance> instances = knowledgeBaseSupplier.get().getCls("Project").getInstances();
		instances.removeIf(i -> !isPublic(i));
		return instances;
	}

	
	/**
	 * Creates a project manager and returns it. (sets name and description of the manager)
	 * @param projectId id of the project
	 * @return project manager
	 */
	public ProjectManager getProjectManager(String projectId) {
		Instance instance = knowledgeBaseSupplier.get().getInstance(projectId);
		
		if (instance == null) return null;
		ProjectManager projectManager = new ProjectManager(projectId, dataPath);
		projectManager.setName((String) instance.getOwnSlotValue(knowledgeBaseSupplier.get().getSlot("displayName")));
		projectManager.setDescription((String) instance.getOwnSlotValue(knowledgeBaseSupplier.get().getSlot("description")));
		
		return projectManager;
	}
	
	
	private Supplier<KnowledgeBase> getKnowledgeBaseSupplier(String pprjPath) {
		return new Supplier<KnowledgeBase>() {
			public KnowledgeBase get() {
				return new Project(pprjPath, new ArrayList<String>()).getKnowledgeBase();
			}
		};
	}
	
	
	/**
	 * Checks if a project in WebProtégé is public.
	 * A project is public if it is not in trash
	 * and if it has allowedOperation "Read" for allowedGroup "World"
	 * @param project instance of class Project of the knowledgebase
	 * @return true if the project is public, else false
	 */
	@SuppressWarnings("unchecked")
	private boolean isPublic(Instance project) {
		if ((Boolean) project.getOwnSlotValue(knowledgeBaseSupplier.get().getSlot("inTrash")))
			return false;
		
		Collection<Instance> groupOperations = project.getOwnSlotValues(knowledgeBaseSupplier.get().getSlot("allowedGroupOperation"));
		for (Instance groupOperation : groupOperations) {
			Instance group = (Instance) groupOperation.getOwnSlotValue(
				knowledgeBaseSupplier.get().getSlot("allowedGroup")
			);
			if (!group.getBrowserText().equals("World")) continue;
			
			Collection<Instance> operations = groupOperation.getOwnSlotValues(knowledgeBaseSupplier.get().getSlot("allowedOperation"));
			for (Instance operation : operations)
				if (operation.getBrowserText().equals("Read"))
					return true;
		}
		return false;
	}
}
