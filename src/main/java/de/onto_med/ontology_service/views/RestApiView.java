package de.onto_med.ontology_service.views;

import java.util.ArrayList;
import java.util.List;

import io.dropwizard.views.View;

public class RestApiView extends View {
	protected String rootPath;
	protected List<String> errorMessages   = new ArrayList<String>();
	protected List<String> successMessages = new ArrayList<String>();
	protected List<String> infoMessages    = new ArrayList<String>();
	
	public RestApiView(String template, String rootPath) {
		super(template);
		this.rootPath = rootPath;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public void addErrorMessage(String message) {
		errorMessages.add(message);
 	}
 	
 	public List<String> getErrorMessages() {
 		return errorMessages;
 	}
 	
 	public void addSuccessMessage(String message) {
 		successMessages.add(message);
 	}
 	
 	public List<String> getSuccessMessage(String message) {
 		return successMessages;
 	}

	public List<String> getInfoMessages() {
		return infoMessages;
	}

	public void addInfoMessage(String message) {
		infoMessages.add(message);
	}
}
