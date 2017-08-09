package de.onto_med.ontology_service.data_models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * Instnces of this class represent ontological entities.
 * @author Christoph Beger
 */
public class Entity {
	private String projectId;
	private String iri;
	private String javaClass;
	private Set<String> individuals       = new HashSet<String>();
	private Set<String> superclasses      = new HashSet<String>();
	private Set<String> subclasses        = new HashSet<String>();
	private Set<String> types             = new HashSet<String>();
	private Set<String> disjointClasses   = new HashSet<String>();
	private Set<String> equivalentClasses = new HashSet<String>();
	private Set<String> sameIndividuals   = new HashSet<String>();
	private Map<String, Set<String>> annotationProperties = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> dataTypeProperties   = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> objectProperties     = new HashMap<String, Set<String>>();
	
	public void addAnnotationProperty(OWLAnnotationProperty property, OWLAnnotationValue value) {
		if (property == null || value == null) return;
		
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
	
	public void addIndividuals(Stream<OWLNamedIndividual> individuals) {
		individuals.parallel().forEach(
			individual -> this.individuals.add(individual.getIRI().toString())
		);
	}
	
	public void addSuperClassExpressions(Stream<OWLClass> superClasses) {
    	superClasses.parallel().forEach(
    		cls -> this.superclasses.add(cls.getIRI().toString())
    	);
	}
	
	public void addSubClassExpressions(Stream<OWLClass> subclasses) {
    	subclasses.parallel().forEach(
    		cls -> this.subclasses.add(cls.getIRI().toString())
    	);
	}
	
	public boolean equals(Object object) {
		return object instanceof Entity && iri.equals(((Entity)object).iri);
	}
	
	public void addTypes(Stream<OWLClass> types) {
		types.parallel().forEach(
			type -> this.types.add(type.getIRI().toString())
		);
	}
	
	public void addSameIndividuals(Stream<OWLNamedIndividual> sameIndividuals) {
		sameIndividuals.parallel().forEach(
			individual -> this.sameIndividuals.add(individual.getIRI().toString())
		);
	}
	
	public void addDataProperty(OWLDataPropertyExpression property, OWLLiteral value) {
		if (value == null) return;
		
		String propertyIRI = property.asOWLDataProperty().getIRI().toString();
		if (!dataTypeProperties.containsKey(propertyIRI)) {
			dataTypeProperties.put(propertyIRI, new HashSet<String>());
		}
		dataTypeProperties.get(propertyIRI).add(value.getLiteral());
	}
	
	public void addObjectProperty(OWLObjectPropertyExpression property, OWLIndividual individual) {
		if (individual == null) return;
		
		String propertyIRI = property.asOWLObjectProperty().getIRI().toString();
		if (!objectProperties.containsKey(propertyIRI)) {
			objectProperties.put(propertyIRI, new HashSet<String>());
		}
		objectProperties.get(propertyIRI).add(individual.toString().replaceAll("^.*?\"|\"\\^.*$", ""));
	}
	
	public void addDisjointClasses(Stream<OWLClass> disjointClasses) {
		disjointClasses.parallel().forEach(
			cls -> this.disjointClasses.add(cls.getIRI().toString())
		);
	}
	
	public void addEquivalentClasses(Stream<OWLClass> equivalentClasses) {
		equivalentClasses.parallel().forEach(
			cls -> this.equivalentClasses.add(cls.getIRI().toString())
		);
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
	
	public Set<String> getIndividuals() {
		return individuals;
	}
	
	public Set<String> getSuperclasses() {
		return superclasses;
	}
	
	public Set<String> getSubclasses() {
		return subclasses;
	}
	
	public Set<String> getTypes() {
		return types;
	}
	
	public Set<String> getSameIndivduals() {
		return sameIndividuals;
	}
	
	public Set<String> getDisjointClasses() {
		return disjointClasses;
	}
	
	public Set<String> getEquivalentClasses() {
		return equivalentClasses;
	}

	public Map<String, Set<String>> getAnnotationProperties() {
		return annotationProperties;
	}
	
	public Map<String, Set<String>> getDataTypeProperties() {
		return dataTypeProperties;
	}
	
	public Map<String, Set<String>> getObjectProperties() {
		return objectProperties;
	}

	
}
