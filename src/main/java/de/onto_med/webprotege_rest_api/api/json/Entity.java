package de.onto_med.webprotege_rest_api.api.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Instnces of this class represent ontological entities.
 * @author Christoph Beger
 */
public class Entity {
	private String projectId;
	private String iri;
	private String javaClass;
	private HashSet<String> individuals;
	private HashSet<String> superclasses;
	private HashSet<String> subclasses;
	private HashSet<String> types;
	private HashSet<String> disjointClasses;
	private HashSet<String> equivalentClasses;
	private HashSet<String> sameIndividuals;
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
		
		if (value instanceof OWLLiteral) {
			annotationProperties.get(propertyIRI).add(((OWLLiteral) value).getLiteral());
		} else {
			annotationProperties.get(propertyIRI).add(value.toString().replaceAll("^.*?\"|\"\\^.*$", ""));
		}
	}
	
	public void addIndividuals(NodeSet<OWLNamedIndividual> individuals) {
		if (individuals.isEmpty()) return;
		if (this.individuals == null) this.individuals = new HashSet<String>();
		
		for (Node<OWLNamedIndividual> individual : individuals) {
			this.individuals.add(individual.iterator().next().getIRI().toString());
		}
	}
	
	public void addSuperClassExpressions(NodeSet<OWLClass> superClasses) {
		if (superClasses.isEmpty()) return;
		if (this.superclasses == null) this.superclasses = new HashSet<String>();
		
    	for (Node<OWLClass> node : superClasses) {
    		this.superclasses.add(node.iterator().next().getIRI().toString());
    	}
	}
	
	public void addSubClassExpressions(NodeSet<OWLClass> subclasses) {
		if (subclasses.isEmpty()) return;
		if (this.subclasses == null) this.subclasses = new HashSet<String>();
		
    	for (Node<OWLClass> node : subclasses) {
    		this.subclasses.add(node.iterator().next().getIRI().toString());
    	}
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Entity)) 
			return false;
		return iri.equals(((Entity)object).iri);
	}
	
	public void addTypes(NodeSet<OWLClass> types) {
		if (types.isEmpty()) return;
		if (this.types == null) this.types = new HashSet<String>();
		
		for (Node<OWLClass> node : types) {
			this.types.add(node.iterator().next().getIRI().toString());
		}
	}
	
	public void addSameIndividuals(Node<OWLNamedIndividual> sameIndividuals) {
		if (sameIndividuals.getSize() == 0) return;
		if (this.sameIndividuals == null) this.sameIndividuals = new HashSet<String>();
		
		Iterator<OWLNamedIndividual> iterator = sameIndividuals.iterator();
		while (iterator.hasNext()) {
			this.sameIndividuals.add(iterator.next().getIRI().toString());
		}
	}
	
	public void addDataProperty(OWLDataPropertyExpression property, Collection<OWLLiteral> values) {
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
	
	public void addDisjointClasses(NodeSet<OWLClass> disjointClasses) {
		if (disjointClasses.isEmpty()) return;
		if (this.disjointClasses == null) this.disjointClasses = new HashSet<String>();
		
		for (Node<OWLClass> node : disjointClasses) {
			this.disjointClasses.add(node.iterator().next().getIRI().toString());
		}
	}
	
	public void addEquivalentClasses(Node<OWLClass> equivalentClasses) {
		if (equivalentClasses.getSize() == 0) return;
		if (this.equivalentClasses == null) this.equivalentClasses = new HashSet<String>();
		
		Iterator<OWLClass> iterator = equivalentClasses.iterator();
		while (iterator.hasNext()) {
			this.equivalentClasses.add(iterator.next().getIRI().toString());
		}
	}
	
	public int hashCode() {
		return iri.hashCode();
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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
	
	public void setDisjointClasses(HashSet<String> disjointClasses) {
		this.disjointClasses = disjointClasses;
	}
	
	public void setEquivalentClasses(HashSet<String> equivalentClasses) {
		this.equivalentClasses = equivalentClasses;
	}
	
	public void setIndividuals(HashSet<String> individuals) {
		this.individuals = individuals;
	}
	
	public void setSameIndividuals(HashSet<String> sameIndividuals) {
		this.sameIndividuals = sameIndividuals;
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
	
	
	public String getProjectId() {
		return projectId;
	}
	
	public String getIri() {
		return iri;
	}
	
	public String getJavaClass() {
		return javaClass;
	}
	
	public HashSet<String> getIndividuals() {
		return individuals;
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
	
	public HashSet<String> getSameIndivduals() {
		return sameIndividuals;
	}
	
	public HashSet<String> getDisjointClasses() {
		return disjointClasses;
	}
	
	public HashSet<String> getEquivalentClasses() {
		return equivalentClasses;
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
