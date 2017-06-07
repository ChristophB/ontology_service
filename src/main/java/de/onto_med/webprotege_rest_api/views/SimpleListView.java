package de.onto_med.webprotege_rest_api.views;

import java.util.ArrayList;

public class SimpleListView extends RestApiView {
	private final ArrayList<String> resultset;
	private final String column;
	
	public SimpleListView(String rootPath, ArrayList<String> resultset, String column) {
		super("SimpleList.ftl", rootPath);
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
