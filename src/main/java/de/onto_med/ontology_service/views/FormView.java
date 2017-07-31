package de.onto_med.ontology_service.views;

public class FormView extends RestApiView {
	private String error;
	
	
	public FormView(String templateName, String rootPath) {
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
