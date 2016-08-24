package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class OWLClassProperties {
	public Set<String> superclasses = new HashSet<String>();
	public Set<String> subclasses   = new HashSet<String>();
	public HashMap<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	public String iri;
	
	
	public void addSuperClassExpression(OWLClassExpression expression) {
		this.superclasses.add(expression.toString());
	}
	
	public void addSuperClassExpressions(Set<OWLClassExpression> expressions) {
		Iterator<OWLClassExpression> iterator = expressions.iterator();
    	while (iterator.hasNext()) {
    		addSuperClassExpression(iterator.next());
    	}
	}
	
	public void addSubClassExpression(OWLClassExpression expression) {
		this.subclasses.add(expression.toString());
	}
	
	public void addSubClassExpressions(Set<OWLClassExpression> expressions) {
		Iterator<OWLClassExpression> iterator = expressions.iterator();
    	while (iterator.hasNext()) {
    		addSubClassExpression(iterator.next());
    	}
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
