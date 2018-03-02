package de.onto_med.ontology_service.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import de.onto_med.ontology_service.api.Timer;
import de.onto_med.ontology_service.data_model.CondencedProject;
import de.onto_med.owlapi_utils.binaryowl.BinaryOwlUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.NoContentException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

/**
 * This class provides information about existing projects in WebProtégé.
 *
 * @author Christoph Beger
 */
@Singleton
public class MetaProjectManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaProjectManager.class);
	private MongoDatabase mongoDatabase;
	private String        dataPath;

	/**
	 * This is the Cache, which contains all previously loaded projectManagers.
	 * Expiration time is set to 10 minutes after the last access.
	 * If a a non existent key is used, the cache tries to instantiate a respective ProjectManager.
	 */
	private LoadingCache<String, ProjectManager> projectManagers = CacheBuilder.newBuilder()
		.expireAfterAccess(10, TimeUnit.MINUTES)
		.build(
			new CacheLoader<String, ProjectManager>() {
				@Override
				public ProjectManager load(@Nonnull String key) throws Exception {
					Timer timer = new Timer();
					ProjectManager projectManager = null;

					Document document = mongoDatabase.getCollection("ProjectDetails").find(
						and(eq("_id", key), eq("inTrash", false))
					).first();

					if (document != null && !document.isEmpty() && isPublic(document.getString("_id"))) {
						projectManager = new ProjectManager(key, dataPath);
						projectManager.setName(document.getString("displayName"));
						projectManager.setDescription(document.getString("description"));
					}

					if (projectManager == null) {
						for (Document project : mongoDatabase.getCollection("ProjectDetails").find()) {
							String projectId = project.getString("_id");
							String path      = dataPath + "/data-store/project-data/" + projectId + "/ontology-data/root-ontology.binary";

							String extractedIri = BinaryOwlUtils.getOntologyIriFromBinaryOwl(path);
							if (extractedIri != null && extractedIri.equals(key)) {
								projectManager = new ProjectManager(projectId, dataPath);
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
	 *
	 * @param dataPath Path to WebProtégé's data folder.
	 */
	public MetaProjectManager(String dataPath, String mongoHost, int mongoPort) {
		this.dataPath = dataPath;
		try {
			mongoDatabase = new MongoClient(new MongoClientURI(String.format("mongodb://%s:%d", mongoHost, mongoPort)))
				.getDatabase("webprotege");
		} catch (Exception e) {
			throw new WebApplicationException("Could not connect to MongoDB instance.");
		}
	}

	/**
	 * Parses a string of project ids separated by comma and returns a list of project ids.
	 * If the string is empty, this function returns a list of all public projects.
	 *
	 * @param projects String of project ids separated by comma
	 * @return List of project ids
	 */
	public List<String> parseOntologies(String projects) throws ExecutionException {
		if (StringUtils.isBlank(projects))
			return getProjectList().parallelStream().map(CondencedProject::getProjectId).collect(Collectors.toList());
		else
			return Arrays.asList(projects.split(","));
	}

	/**
	 * Empties the ProjectManager cache immediately.
	 */
	public void clearCache() {
		projectManagers.invalidateAll();
	}


	/**
	 * Returns a list of all available public readable projects, stored in WebProtégé.
	 *
	 * @return List of projects
	 */
	public ArrayList<CondencedProject> getProjectList() throws ExecutionException {
		FindIterable<Document> projects = mongoDatabase.getCollection("ProjectDetails").find(eq("inTrash", false));

		ArrayList<CondencedProject> projectList = new ArrayList<>();
		for (Document project : projects) {
			if (!isPublic(project.getString("_id"))) continue;
			projectList.add(new CondencedProject(projectManagers.get(project.getString("_id"))));
		}

		return projectList;
	}


	/**
	 * Returns an OntologyManager for a given id, if project with specified id exists and is public.
	 *
	 * @param projectId id of a project
	 * @return OntologyManager for project with specified id
	 * @throws NoContentException If no public project with matching id was found or ontology could not be parsed
	 * @throws ExecutionException If get() on cached project managers failed
	 */
	public ProjectManager getProjectManager(String projectId) throws NoContentException, ExecutionException {
		if (projectId == null) throw new NoContentException("projectId can not be null.");
		return projectManagers.get(projectId);
	}

	private boolean isPublic(String projectId) {
		Document roleAssignments = mongoDatabase.getCollection("RoleAssignments").find(
			and(eq("projectId", projectId),
				exists("userName", false),
				or(all("assignedRoles", "CanView"),
					all("assignedRoles", "CanComment"),
					all("assignedRoles", "CanEdit"),
					all("assignedRoles", "CanManage")
				))
		).first();
		return (roleAssignments != null && !roleAssignments.isEmpty());
	}
}
