package de.uni_leipzig.imise.webprotege.rest_api.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.manager.MetaProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;
import de.uni_leipzig.imise.webprotege.rest_api.views.EntityFormView;
import de.uni_leipzig.imise.webprotege.rest_api.views.EntityResultsetView;
import de.uni_leipzig.imise.webprotege.rest_api.views.ProjectListView;
import de.uni_leipzig.imise.webprotege.rest_api.views.ReasonFormView;

/**
 * Project resource, which is accessible by the REST API.
 * Query relative URL "/" to get a JSON document with all available paths.
 * 
 * @author Christoph Beger
 */
@Path("/")
public class MetaProjectResource extends Resource {
	
	private MetaProjectManager mpm;

	
	
	/**
	 * Constructor.
	 * @param dataPath path to WebProtegés data folder.
	 */
	public MetaProjectResource(String dataPath) {
		super(dataPath);
		mpm = new MetaProjectManager(dataPath);
	}
	
	
	/**
	 * Returns a list of public projects with condensed metadata.
	 * @return List of projects with metadata
	 * @throws NoContentException 
	 */
	@GET
	@Path("/projects")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getProjectList(@Context HttpHeaders headers) throws NoContentException {
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		
		if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(mpm.getProjectList()).build();
		} else {
			return Response.ok(new ProjectListView(mpm.getProjectList())).build();
		}
	}

	
	/**
	 * Searches one or many ontologies for mathing entities.
	 * @param name localName part of an entity
	 * @param property property the searched entity is annotated with
	 * @param value property value
	 * @param type entity, class or individual
	 * @param ontologies list of projectids separated by comma
	 * @param match matching method for name ('exact' or 'loose'), defaults to 'loose'
	 * @param operator logical operator to combine name and property, defaults to 'and')
	 * @return ArrayList of OWLEntityProperties or error message
	 */
	@GET
	@Path("/entity")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response searchOntologyEntities(
		@Context HttpHeaders headers,
		@QueryParam("name")	String name,
		@QueryParam("property") String property,
		@QueryParam("value") String value,
		@QueryParam("type") String type,
		@QueryParam("ontologies") String ontologies,
		@QueryParam("match") String match,
		@QueryParam("operator") String operator
	) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		
		try {
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(property))
				throw new Exception("Neither query param 'name' nor 'property' given.");
			
			for (String projectId : parseOntologies(ontologies)) {
				result.addAll(new ProjectResource(dataPath).searchOntologyEntities(
					projectId, name, property, value, type, match, operator
				));
			}
		} catch (Exception e) {
			if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				EntityFormView view = new EntityFormView();
				view.addErrorMessage(e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(result)).build();
		}
	}
	
	
	/**
	 * Reasons over the specified ontologies with supplied classexpression
	 * @param ce class expression
	 * @param ontologies list of ontologies (optional)
	 * @return search result
	 */
	@GET
	@Path("/reason")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response reason(
			@Context HttpHeaders headers,
			@QueryParam("ce") String ce,
			@QueryParam("ontologies") String ontologies
		) {
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		List<MediaType> accepts = headers.getAcceptableMediaTypes();
		
		try {
			if (StringUtils.isEmpty(ce))
				throw new Exception("No class expression given.");
		
			for (String projectId : parseOntologies(ontologies)) {
				ProjectResource pr = new ProjectResource(dataPath);
				result.addAll(pr.reason(projectId, ce));
			}
		} catch (Exception e) {
			if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(e.getMessage()).build();
			} else {
				ReasonFormView view = new ReasonFormView();
				view.addErrorMessage(e.getMessage().replaceAll("\\n", "<br>"));
				return Response.ok(view).build();
			}
		}
		
		if (accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(result).build();
		} else {
			return Response.ok(new EntityResultsetView(result)).build();
		}
	}

	
	/**
	 * Parses a string of projectids separated by comma and returns a list of projectids.
	 * If the string is empty, this function returns a list of all public projects.
	 * @param ontologies String of projectids separated by comma
	 * @return List of projectids
	 * @throws NoContentException 
	 */
	private List<String> parseOntologies(String projects) throws NoContentException {
		if (StringUtils.isEmpty(projects)) {
			List<String> projectList = new ArrayList<String>();
			for (ProjectManager pm : mpm.getProjectList()) {
				projectList.add(pm.getProjectId());
			}
			return projectList;
		} else {
			return Arrays.asList(projects.split(","));
		}
	}
	
}