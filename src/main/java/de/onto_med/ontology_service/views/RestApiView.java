package de.onto_med.ontology_service.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dropwizard.views.View;

public class RestApiView extends View {
	protected String rootPath;
	protected Map<String, List<String>> messages;
	
	public RestApiView(String template, String rootPath) {
		super(template);
		this.rootPath = rootPath;
		messages = new HashMap<String, List<String>>();
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	public void addMessage(String type, String message) {
		messages.putIfAbsent(type, new ArrayList<String>());
		messages.get(type).add(message);
	}
	
	public Map<String, List<String>> getMessages() {
		return messages;
	}
}
