package de.uni_leipzig.imise.webprotege.rest_api.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

public class OWLEntityProperties {
	public String iri;
	public HashMap<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	
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
	
	public boolean equals(Object object) {
		if (!(object instanceof OWLEntityProperties)) 
			return false;
		return iri.equals(((OWLEntityProperties)object).iri);
	}
	
	public int hashCode() {
		return iri.hashCode();
	}
}
