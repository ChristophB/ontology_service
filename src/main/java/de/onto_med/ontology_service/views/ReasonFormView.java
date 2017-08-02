package de.onto_med.ontology_service.views;

public class ReasonFormView extends RestApiView {
	
	private String ce;
	private String ontologies;
	
	public ReasonFormView(String rootPath, String ce, String ontologies) {
		super("ReasonForm.ftl", rootPath);
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
