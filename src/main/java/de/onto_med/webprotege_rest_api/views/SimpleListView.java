package de.onto_med.webprotege_rest_api.views;

import java.util.List;

public class SimpleListView extends RestApiView {
	private final List<String> resultset;
	private final String column;
	
	public SimpleListView(String rootPath, List<String> resultset, String column) {
		super("SimpleList.ftl", rootPath);
		this.resultset = resultset;
		this.column    = column;
	}
	
	
	public List<String> getResultset() {
		return resultset;
	}


	public String getColumn() {
		return column;
	}
}
