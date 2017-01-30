package de.onto_med.webprotege_rest_api.manager;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.NoContentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;

import de.onto_med.webprotege_rest_api.RestApiApplication;
import de.onto_med.webprotege_rest_api.ontology.PprjParser;
import edu.stanford.smi.protege.model.Instance;

/**
 * This class provides information about existing projects in WebProtegé.
 * @author Christoph Beger
 */
@Singleton
public class MetaProjectManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestApiApplication.class);
	private PprjParser pprjParser;
	
	private LoadingCache<String, ProjectManager> projectManagers = CacheBuilder.newBuilder()
		.expireAfterWrite(10, TimeUnit.MINUTES)
		.build(
			new CacheLoader<String, ProjectManager>() {
				@Override
				public ProjectManager load(String key) throws Exception {
					LOGGER.info("populating cache with '" + key + "'.");
					
					ProjectManager projectManager = pprjParser.getProjectManager(key);
					if (projectManager != null) {
						return projectManager;
					} else 
						throw new NoContentException("Could not find project by id: '" + key + "'");
				}
			}
		);
	
	/**
	 * Constructor
	 * @param dataPath Path to WebProtegés data folder.
	 */
	public MetaProjectManager(String dataPath) {
		pprjParser = new PprjParser(dataPath);
	}
	
	
	/**
	 * Returns a list of all available public readable projects, stored in WebProtegé.
	 * @return List of projects
	 * @throws NoContentException 
	 * @throws ExecutionException 
	 */
	public Collection<ProjectManager> getProjectList() throws NoContentException, ExecutionException {
		for (Instance project : pprjParser.getProjectInstances()) {
			projectManagers.get(project.getName());
		}
		
		return projectManagers.asMap().values();
	}

	
	/**
	 * Returns an OntologyManager for a given id, if project with specified id exists and is public.
	 * @param projectId id of a project
	 * @return OntologyManager for project with specified id
	 * @throws NoContentException If no public project with matching id was found or ontology was not parsable
	 * @throws ExecutionException 
	 */
	public ProjectManager getProjectManager(String projectId) throws NoContentException, ExecutionException {
		return projectManagers.get(projectId);
	}
}
