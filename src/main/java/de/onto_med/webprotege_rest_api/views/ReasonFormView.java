package de.onto_med.webprotege_rest_api.views;

public class ReasonFormView extends FormView {
	
	private String ce;
	private String ontologies;
	
	public ReasonFormView(String ce, String ontologies) {
		super("ReasonForm.ftl");
		this.ce = ce;
		this.ontologies = ontologies;
	}
	
	
	public String getCe() {
		return ce;
	}
	
	public String getOntologies() {
		return ontologies;
	}
	
}
