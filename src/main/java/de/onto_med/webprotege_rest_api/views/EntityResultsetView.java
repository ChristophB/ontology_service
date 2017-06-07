package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;

import de.onto_med.webprotege_rest_api.api.Entity;

public class EntityResultsetView extends RestApiView {
	
	private final ArrayList<Entity> resultset;
	
	public EntityResultsetView(String rootPath, ArrayList<Entity> resultset) {
		super("EntityResultset.ftl", rootPath);
		this.resultset = resultset;
	}
	
	public ArrayList<Entity> getResultset() {
		return resultset;
	}

}
