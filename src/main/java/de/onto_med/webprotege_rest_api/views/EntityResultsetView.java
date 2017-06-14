package de.onto_med.webprotege_rest_api.views;

import java.util.List;

import de.onto_med.webprotege_rest_api.api.json.Entity;

public class EntityResultsetView extends RestApiView {
	
	private final List<Entity> resultset;
	
	public EntityResultsetView(String rootPath, List<Entity> resultset) {
		super("EntityResultset.ftl", rootPath);
		this.resultset = resultset;
	}
	
	public List<Entity> getResultset() {
		return resultset;
	}

}
