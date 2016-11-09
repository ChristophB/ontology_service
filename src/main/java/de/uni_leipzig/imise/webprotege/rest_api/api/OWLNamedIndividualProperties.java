package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class OWLNamedIndividualProperties extends OWLEntityProperties {
	public Set<String> types                                 = new HashSet<String>();
	public HashMap<String, Set<String>> dataProperties       = new HashMap<String, Set<String>>();
	public HashMap<String, Set<String>> objectProperties     = new HashMap<String, Set<String>>();
	
	public void addTypeExpression(OWLClassExpression expression) {
		this.types.add(expression.toString());
	}
	
	public void addDataProperty(OWLDataPropertyExpression property, Set<OWLLiteral> values) {
		String propertyIRI = property.asOWLDataProperty().getIRI().toString();
		if (!dataProperties.containsKey(propertyIRI)) {
			dataProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLLiteral literal : values) {
			dataProperties.get(propertyIRI).add(literal.getLiteral());
		}
	}
	
	public void addObjectProperty(OWLObjectPropertyExpression property, Set<OWLIndividual> values) {
		String propertyIRI = property.asOWLObjectProperty().getIRI().toString();
		if (!objectProperties.containsKey(propertyIRI)) {
			objectProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLIndividual individual : values) {
			objectProperties.get(propertyIRI).add(individual.toString().replaceAll("^.*?\"|\"\\^.*$", ""));
		}
	}

}
