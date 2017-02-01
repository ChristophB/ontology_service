package de.onto_med.webprotege_rest_api.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

public class OWLEntityProperties {
	private String iri;
	private String javaClass;
	private HashSet<String> superclasses;
	private HashSet<String> subclasses;
	private HashSet<String> types;
	private HashMap<String, Set<String>> annotationProperties;
	private HashMap<String, Set<String>> dataTypeProperties;
	private HashMap<String, Set<String>> objectProperties;
	
	
	public void addAnnotationProperty(OWLAnnotationProperty property, OWLAnnotationValue value) {
		if (property == null || value == null) return;
		if (annotationProperties == null) annotationProperties = new HashMap<String, Set<String>>();
		
		String propertyIRI = property.getIRI().toString();
		if (!annotationProperties.containsKey(propertyIRI)) {
			annotationProperties.put(propertyIRI, new HashSet<String>());
		}
		
		if (value.getClass().isAssignableFrom(OWLLiteralImplString.class)) {
			annotationProperties.get(propertyIRI).add(((OWLLiteralImplString) value).getLiteral());
		} else {
			annotationProperties.get(propertyIRI).add(value.toString().replaceAll("^.*?\"|\"\\^.*$", ""));
		}
	}
	
	public void addSuperClassExpression(OWLClassExpression expression) {
		if (superclasses == null) superclasses = new HashSet<String>();
		this.superclasses.add(expression.toString());
	}
	
	public void addSuperClassExpressions(Collection<OWLClassExpression> collection) {
		if (collection.isEmpty()) return;
		if (superclasses == null) superclasses = new HashSet<String>();
		
		Iterator<OWLClassExpression> iterator = collection.iterator();
    	while (iterator.hasNext()) {
    		addSuperClassExpression(iterator.next());
    	}
	}
	
	public void addSubClassExpression(OWLClassExpression expression) {
		if (subclasses == null) subclasses = new HashSet<String>();
		this.subclasses.add(expression.toString());
	}
	
	public void addSubClassExpressions(Collection<OWLClassExpression> collection) {
		if (collection.isEmpty()) return;
		if (subclasses == null) subclasses = new HashSet<String>();
		
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
		if (types == null) types = new HashSet<String>();
		
		this.types.add(expression.toString());
	}
	
	public void addDataTypeProperty(OWLDataPropertyExpression property, Collection<OWLLiteral> values) {
		if (values.isEmpty()) return;
		if (dataTypeProperties == null) dataTypeProperties = new HashMap<String, Set<String>>();
		
		String propertyIRI = property.asOWLDataProperty().getIRI().toString();
		if (!dataTypeProperties.containsKey(propertyIRI)) {
			dataTypeProperties.put(propertyIRI, new HashSet<String>());
		}
		for (OWLLiteral literal : values) {
			dataTypeProperties.get(propertyIRI).add(literal.getLiteral());
		}
	}
	
	public void addObjectProperty(OWLObjectPropertyExpression property, Collection<OWLIndividual> values) {
		if (values.isEmpty()) return;
		if (objectProperties == null) objectProperties = new HashMap<String, Set<String>>();
		
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

	public void setIri(String iri) {
		this.iri = iri;
	}
	
	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}
	
	public void setSuperclasses(HashSet<String> superclasses) {
		this.superclasses = superclasses;
	}
	
	public void setSubclasses(HashSet<String> subclasses) {
		this.subclasses = subclasses;
	}
	
	public void setTypes(HashSet<String> types) {
		this.types = types;
	}
	
	public void setAnnotationProperties(HashMap<String, Set<String>> annotationProperties) {
		this.annotationProperties = annotationProperties;
	}
	
	public void setDataTypeProperties(HashMap<String, Set<String>> dataTypeProperties) {
		this.dataTypeProperties = dataTypeProperties;
	}
	
	public void setObjectProperties(HashMap<String, Set<String>> objectProperties) {
		this.objectProperties = objectProperties;
	}
	
	
	public String getIri() {
		return iri;
	}
	
	public String getJavaClass() {
		return javaClass;
	}
	
	public HashSet<String> getSuperclasses() {
		return superclasses;
	}
	
	public HashSet<String> getSubclasses() {
		return subclasses;
	}
	
	public HashSet<String> getTypes() {
		return types;
	}

	public HashMap<String, Set<String>> getAnnotationProperties() {
		return annotationProperties;
	}
	
	public HashMap<String, Set<String>> getDataTypeProperties() {
		return dataTypeProperties;
	}
	
	public HashMap<String, Set<String>> getObjectProperties() {
		return objectProperties;
	}
}
