package de.onto_med.webprotege_rest_api.views;

import io.dropwizard.views.View;

public class FormView extends View {
	private String error;
	
	
	protected FormView(String templateName) {
		super(templateName);
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
