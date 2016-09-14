package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

public class OWLEntityProperties {
	public String iri;
	public HashMap<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	
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
	
	public boolean equals(Object object) {
		if (!(object instanceof OWLEntityProperties)) 
			return false;
		return iri.equals(((OWLEntityProperties)object).iri);
	}
	
	public int hashCode() {
		return iri.hashCode();
	}
}
