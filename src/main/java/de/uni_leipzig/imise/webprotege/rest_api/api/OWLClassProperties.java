package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;

public class OWLClassProperties extends OWLEntityProperties {
	public Set<String> superclasses = new HashSet<String>();
	public Set<String> subclasses   = new HashSet<String>();
	
	
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
	
}
