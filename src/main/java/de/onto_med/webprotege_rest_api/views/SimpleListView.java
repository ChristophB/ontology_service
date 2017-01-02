package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;
import io.dropwizard.views.View;

public class SimpleListView extends View {
	private final ArrayList<String> resultset;
	private final String column;
	
	public SimpleListView(ArrayList<String> resultset, String column) {
		super("SimpleList.ftl");
		this.resultset = resultset;
		this.column    = column;
	}
	
	
	public ArrayList<String> getResultset() {
		return resultset;
	}


	public String getColumn() {
		return column;
	}
}
