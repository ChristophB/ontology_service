package de.onto_med.webprotege_rest_api.views;

import io.dropwizard.views.View;

public class RestApiView extends View {
	protected String rootPath;
	
	public RestApiView(String template, String rootPath) {
		super(template);
		this.rootPath = rootPath;
	}
	
	public String getRootPath() {
		return rootPath;
	}
}
