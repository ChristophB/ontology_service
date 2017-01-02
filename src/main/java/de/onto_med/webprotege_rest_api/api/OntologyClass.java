package de.onto_med.webprotege_rest_api.api;

import java.util.ArrayList;
import java.util.Iterator;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OntologyClass {
	private long id;
	private OWLOntology ontology;
	private OWLClass ontClass;
	
	public OntologyClass() { }

	public OntologyClass(long id, OWLOntology ontology, OWLClass ontClass) {
		this.id = id;
		this.ontology = ontology;
		this.ontClass = ontClass;
	}
	
	@JsonProperty
	public long getId() {
		return id;
	}
	
	@JsonProperty
	public String getIRI() {
		return ontClass.getIRI().toString();
	}
	
	@JsonProperty
	public ArrayList<String> getSuperclasses() {
		ArrayList<String> result = new ArrayList<String>();
		Iterator<OWLClassExpression> iterator = EntitySearcher.getSuperClasses(ontClass, ontology).iterator();
		
		while (iterator.hasNext()) {
			result.add(iterator.next().toString());
		}
		return result;
	}
}
