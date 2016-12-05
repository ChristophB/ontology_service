package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

public class OWLEntityProperties {
	public String iri;
	public String javaClass;
	public Set<String> superclasses = new HashSet<String>();
	public Set<String> subclasses   = new HashSet<String>();
	public Set<String> types        = new HashSet<String>();
	public HashMap<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	public HashMap<String, Set<String>> dataProperties       = new HashMap<String, Set<String>>();
	public HashMap<String, Set<String>> objectProperties     = new HashMap<String, Set<String>>();
	
	
	public void addAnnotationProperty(OWLAnnotationProperty property, Collection<OWLAnnotation> values) {
		if (values.isEmpty()) return;
		
		String propertyIRI = property.getIRI().toString();
		if (!annotationProperties.containsKey(propertyIRI)) {
			annotationProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLAnnotation annotation : values) {
			OWLAnnotationValue value = annotation.getValue();
			
			if (value.getClass().isAssignableFrom(OWLLiteralImplString.class)) {
				annotationProperties.get(property.getIRI().toString()).add(((OWLLiteralImplString)value).getLiteral());
			} else {
				annotationProperties.get(property.getIRI().toString()).add(annotation.getValue().toString().replaceAll("^.*?\"|\"\\^.*$", ""));
			}
		}
	}
	
	public void addSuperClassExpression(OWLClassExpression expression) {
		this.superclasses.add(expression.toString());
	}
	
	public void addSuperClassExpressions(Collection<OWLClassExpression> collection) {
		Iterator<OWLClassExpression> iterator = collection.iterator();
    	while (iterator.hasNext()) {
    		addSuperClassExpression(iterator.next());
    	}
	}
	
	public void addSubClassExpression(OWLClassExpression expression) {
		this.subclasses.add(expression.toString());
	}
	
	public void addSubClassExpressions(Collection<OWLClassExpression> collection) {
		Iterator<OWLClassExpression> iterator = collection.iterator();
    	while (iterator.hasNext()) {
    		addSubClassExpression(iterator.next());
    	}
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof OWLEntityProperties)) 
			return false;
		return iri.equals(((OWLEntityProperties)object).iri);
	}
	
	public void addTypeExpression(OWLClassExpression expression) {
		this.types.add(expression.toString());
	}
	
	public void addDataProperty(OWLDataPropertyExpression property, Collection<OWLLiteral> values) {
		String propertyIRI = property.asOWLDataProperty().getIRI().toString();
		if (!dataProperties.containsKey(propertyIRI)) {
			dataProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLLiteral literal : values) {
			dataProperties.get(propertyIRI).add(literal.getLiteral());
		}
	}
	
	public void addObjectProperty(OWLObjectPropertyExpression property, Collection<OWLIndividual> values) {
		String propertyIRI = property.asOWLObjectProperty().getIRI().toString();
		if (!objectProperties.containsKey(propertyIRI)) {
			objectProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLIndividual individual : values) {
			objectProperties.get(propertyIRI).add(individual.toString().replaceAll("^.*?\"|\"\\^.*$", ""));
		}
	}
	
	public int hashCode() {
		return iri.hashCode();
	}
}
