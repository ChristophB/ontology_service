package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;

public class PathDocumentation {
	public String path, description;
	public HashMap<String, String> parameters;
	
	public PathDocumentation(String path, String description) {
		this.path = path;
		this.description = description;
		parameters = new HashMap<String, String>();
	}
	
	public PathDocumentation addParameter(String name, String description) {
		parameters.put(name, description);
		
		return this;
	}
}
