package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class OWLNamedIndividualProperties {
	public Set<String> types                                             = new HashSet<String>();
	public HashMap<String, Set<OWLLiteral>> dataProperties               = new HashMap<String, Set<OWLLiteral>>();
	public HashMap<String, Set<OWLIndividual>> objectProperties          = new HashMap<String, Set<OWLIndividual>>();
	public HashMap<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	public String iri;
	
	public void addTypeExpression(OWLClassExpression expression) {
		this.types.add(expression.toString());
	}
	
	public void addDataProperty(OWLDataProperty property, Set<OWLLiteral> values) {
		String propertyIRI = property.getIRI().toString();
		if (!dataProperties.containsKey(propertyIRI)) {
			dataProperties.put(propertyIRI, new HashSet<OWLLiteral>());
		}
		dataProperties.get(property.getIRI().toString()).addAll(values);
	}
	
	public void addObjectProperty(OWLObjectProperty property, Set<OWLIndividual> values) {
		String propertyIRI = property.getIRI().toString();
		if (!objectProperties.containsKey(propertyIRI)) {
			objectProperties.put(propertyIRI, new HashSet<OWLIndividual>());
		}
		objectProperties.get(propertyIRI).addAll(values);
	}
	
	public void addAnnotationProperty(OWLAnnotationProperty property, Set<OWLAnnotation> values) {
		if (values.isEmpty()) return;
		
		String propertyIRI = property.getIRI().toString();
		if (!annotationProperties.containsKey(propertyIRI)) {
			annotationProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLAnnotation annotation : values) {
			annotationProperties.get(property.getIRI().toString()).add(annotation.getValue().toString());
		}
	}
}
