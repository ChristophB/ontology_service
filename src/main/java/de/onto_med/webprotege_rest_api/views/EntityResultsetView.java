package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;

import de.onto_med.webprotege_rest_api.api.Entity;
import io.dropwizard.views.View;

public class EntityResultsetView extends View {
	
	private final ArrayList<Entity> resultset;
	
	public EntityResultsetView(ArrayList<Entity> resultset) {
		super("EntityResultset.ftl");
		this.resultset = resultset;
	}
	
	public ArrayList<Entity> getResultset() {
		return resultset;
	}

}
