package de.onto_med.ontology_service.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.core.NoContentException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;

import edu.stanford.smi.protege.model.Instance;
import de.onto_med.ontology_service.api.Timer;
import de.onto_med.ontology_service.data_models.CondencedProject;
import de.onto_med.ontology_service.ontology.PprjParser;
import de.onto_med.owlapi_utils.binaryowl.BinaryOwlUtils;

/**
 * This class provides information about existing projects in WebProtegé.
 * @author Christoph Beger
 */
@Singleton
public class MetaProjectManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaProjectManager.class);
	private PprjParser pprjParser;
	private String dataPath;
	
	/**
	 * This is the Cache, wich contains all previously loaded projectManagers.
	 * Expiration time is set to 10 minutes after the last access.
	 * If a a non existend key is used, the cache tries to instantiate a respective ProjectManager.
	 */
	private LoadingCache<String, ProjectManager> projectManagers = CacheBuilder.newBuilder()
		.expireAfterAccess(10, TimeUnit.MINUTES)
		.build(
			new CacheLoader<String, ProjectManager>() {
				@Override
				public ProjectManager load(String key) throws Exception {
					Timer timer = new Timer();
					ProjectManager projectManager = pprjParser.getProjectManager(key);
					if (projectManager == null) {
						for (Instance instance : pprjParser.getProjectInstances()) {
							String projectId = instance.getName();
							String path = dataPath + "/data-store/project-data/" + projectId + "/ontology-data/root-ontology.binary";
							
							String extractedIri = BinaryOwlUtils.getOntologyIriFromBinaryOwl(path);
							if (extractedIri != null && extractedIri.equals(key)) {
								projectManager = pprjParser.getProjectManager(projectId);
								break;
							}
						}
						
						if (projectManager == null)
							throw new NoContentException("Could not find project by id: '" + key + "'.");
					}
					
					LOGGER.info("Populated cache with '" + key + "'. " + timer.getDiff());
					return projectManager;
				}
			}
		);
	
	/**
	 * Constructor
	 * @param dataPath Path to WebProtegés data folder.
	 */
	public MetaProjectManager(String dataPath) {
		this.dataPath = dataPath;
		pprjParser = new PprjParser(dataPath);
	}
	
	/**
	 * Parses a string of projectids separated by comma and returns a list of projectids.
	 * If the string is empty, this function returns a list of all public projects.
	 * @param ontologies String of projectids separated by comma
	 * @return List of projectids
	 * @throws NoContentException 
	 * @throws ExecutionException 
	 */
	public List<String> parseOntologies(String projects) throws NoContentException, ExecutionException {
		if (StringUtils.isBlank(projects))
			return getProjectList().parallelStream().map(p -> p.getProjectId()).collect(Collectors.toList());
		else
			return Arrays.asList(projects.split(","));
	}
	
	/**
	 * Empties the ProjectManager cache immediatly.
	 */
	public void clearCache() {
		projectManagers.invalidateAll();
	}
	
	
	/**
	 * Returns a list of all available public readable projects, stored in WebProtegé.
	 * @return List of projects
	 * @throws NoContentException 
	 * @throws ExecutionException 
	 */
	public ArrayList<CondencedProject> getProjectList() throws NoContentException, ExecutionException {
		ArrayList<CondencedProject> projectList = new ArrayList<CondencedProject>();
		for (Instance instance : pprjParser.getProjectInstances()) {
			projectList.add(new CondencedProject(projectManagers.get(instance.getName())));
		}
		
		return projectList;
	}

	
	/**
	 * Returns an OntologyManager for a given id, if project with specified id exists and is public.
	 * @param projectId id of a project
	 * @return OntologyManager for project with specified id
	 * @throws NoContentException If no public project with matching id was found or ontology was not parsable
	 * @throws ExecutionException 
	 */
	public ProjectManager getProjectManager(String projectId) throws NoContentException, ExecutionException {
		if (projectId == null) throw new NoContentException("projectId can not be null.");
		return projectManagers.get(projectId);
	}
}
