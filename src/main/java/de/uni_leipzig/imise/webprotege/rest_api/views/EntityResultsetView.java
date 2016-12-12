package de.uni_leipzig.imise.webprotege.rest_api.views;

import java.util.ArrayList;

import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import io.dropwizard.views.View;

public class EntityResultsetView extends View {
	
	private final ArrayList<OWLEntityProperties> resultset;
	
	public EntityResultsetView(ArrayList<OWLEntityProperties> resultset) {
		super("EntityResultset.ftl");
		this.resultset = resultset;
	}
	
	public ArrayList<OWLEntityProperties> getResultset() {
		return resultset;
	}
}
