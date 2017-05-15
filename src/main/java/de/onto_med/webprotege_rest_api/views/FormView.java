package de.onto_med.webprotege_rest_api.views;

public class FormView extends RestApiView {
	private String error;
	
	
	protected FormView(String templateName, String rootPath) {
		super(templateName, rootPath);
	}
	
	
	public void addErrorMessage(String error) {
 		if (this.error == null) {
 			this.error = error;
 		} else {
 			this.error += "<br>" + error;
 		}
 	}
 	

 	public String getErrorMessage() {
 		return error;
 	}
 	
}
