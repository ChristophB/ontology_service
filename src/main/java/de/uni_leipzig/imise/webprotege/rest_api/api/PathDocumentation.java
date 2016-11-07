package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;

/**
 * This class represents documentation for a GET path of this application.
 * 
 * @author cbeger
 */
public class PathDocumentation {
	public String path, description;
	public HashMap<String, String> parameters;
	
	
	
	/**
	 * Constructor.
	 */
	public PathDocumentation(String path, String description) {
		this.path = path;
		this.description = description;
		parameters = new HashMap<String, String>();
	}
	
	
	/**
	 * Adds parameter documentation to this path.
	 * @return This instance to allow cascade calls
	 */
	public PathDocumentation addParameter(String parameterName, String description) {
		parameters.put(parameterName, description);
		
		return this;
	}
}
