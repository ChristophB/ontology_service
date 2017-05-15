package de.onto_med.webprotege_rest_api.views;

public class EntityFormView extends FormView {
	
 	private String type;
	private String name;
	private String iri;
	private String property;
	private String value;
	private String match;
	private String operator;
	private String ontologies;

	
	public EntityFormView(
 		String rootPath, String type, String name, String iri, String property,
 		String value, String match, String operator, String ontologies
 	) {
		super("EntityForm.ftl", rootPath);
		this.type       = type;
		this.name       = name;
		this.iri        = iri;
		this.property   = property;
		this.value      = value;
		this.match      = match;
		this.operator   = operator;
		this.ontologies = ontologies;
	}


 	public String getType() {
 		return type;
 	}
 	
 	
 	public String getName() {
 		return name;
 	}
 	
 	
 	public String getIri() {
 		return iri;
 	}
 	
 	
 	public String getProperty() {
 		return property;
 	}
 	
 	
 	public String getValue() {
 		return value;
 	}
 	
 	
 	public String getMatch() {
 		return match;
 	}
 	
 	
 	public String getOperator() {
 		return operator;
 	}
 	
 	
 	public String getOntologies() {
 		return ontologies;
 	}
 	
}
