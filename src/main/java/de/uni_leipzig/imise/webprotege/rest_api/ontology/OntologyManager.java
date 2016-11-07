package de.uni_leipzig.imise.webprotege.rest_api.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentParserFactory;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentStorer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.OWLParserFactoryRegistry;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.uni_leipzig.imise.webprotege.rest_api.api.OWLClassProperties;
import de.uni_leipzig.imise.webprotege.rest_api.api.OWLEntityProperties;
import de.uni_leipzig.imise.webprotege.rest_api.api.OWLNamedIndividualProperties;
import edu.stanford.smi.protege.model.Instance;

public class OntologyManager {
	private static final Double THRESHOLD = 0.8;
	private String path;
	private String rootPath;
	private String importsPath;
	
	static {
	    OWLManager.getOWLDataFactory();
	    OWLParserFactoryRegistry parserFactoryRegistry = OWLParserFactoryRegistry.getInstance();
	    List<OWLParserFactory> parserFactoryList = new ArrayList<OWLParserFactory>(parserFactoryRegistry.getParserFactories());
	    Collections.reverse(parserFactoryList);
	    parserFactoryRegistry.clearParserFactories();
	    for(OWLParserFactory parserFactory : parserFactoryList) {
	         parserFactoryRegistry.registerParserFactory(parserFactory);
	    }
	    parserFactoryRegistry.registerParserFactory(new BinaryOWLOntologyDocumentParserFactory());
	}
	
	
	public OntologyManager(Instance project, String dataPath) {
		path        = dataPath + "/data-store/project-data/" + project.getName();
		rootPath    = path + "/ontology-data/root-ontology.binary";
		importsPath = path + "/imports-cache";
	}
	
	
	public ArrayList<String> getOntologyImports() throws OWLOntologyCreationException {
		ArrayList<String> imports = new ArrayList<String>();
		
		for (OWLOntology ontology : getRootOntology().getImports()) {
			imports.add(ontology.getOntologyID().toString());
		}
		
		return imports;
	}

	
	public ArrayList<OWLEntityProperties> getClassPropertiesByName(ArrayList<String> set, String name, String match) throws Exception {
		Filter filter;
		
		if ("exact".equals(match)) {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return a.isOWLClass() && a.getIRI().getFragment().equals(b);
				}
			};
		} else {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return a.isOWLClass() && StringUtils.getJaroWinklerDistance(a.getIRI().getFragment(), b) >= THRESHOLD;
				}
			};
		}
	    
	    return getPropertiesForOWLEntities(extractEntitiesWithFilter(set, name, filter));
	}
	
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByName(ArrayList<String> set, String name, String match) throws Exception {
		Filter filter;
		
		if ("exact".equals(match)) {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return a.isOWLNamedIndividual() && a.getIRI().getFragment().equals(b);
				}
			};
		} else {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return a.isOWLNamedIndividual() && StringUtils.getJaroWinklerDistance(a.getIRI().getFragment(), b) >= THRESHOLD;
				}
			};
		}
		
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(set, name, filter));
	}
	
	public ArrayList<OWLEntityProperties> getEntityPropertiesByName(ArrayList<String> set, String name, String match) throws Exception {
		Filter filter;
		
		if ("exact".equals(match)) {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return a.getIRI().getFragment().equals(b);
				}
			};
		} else {
			filter = new Filter() {
				@Override public boolean run(OWLEntity a, String b) {
					return StringUtils.getJaroWinklerDistance(a.getIRI().getFragment(), b) >= THRESHOLD;
				}
			};
		}
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(set, name, filter));
	}
	
	
	public ArrayList<OWLEntityProperties> getClassPropertiesByProperty(ArrayList<String> set, String property, String value) throws Exception {
	    return getPropertiesForOWLEntities(extractOWLClassesByProperty(set, property, value));
	}
	
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByProperty(ArrayList<String> set, String property, String value) throws Exception {
		return getPropertiesForOWLEntities(extractOWLNamedIndividualByProperty(set, property, value));
	}
	
	public ArrayList<OWLEntityProperties> getEntityPropertiesByProperty(ArrayList<String> set, String property, String value) throws Exception {
		return getPropertiesForOWLEntities(extractOWLEntitiesByProperty(set, property, value));
	}
	
	
	private ArrayList<OWLEntity> extractOWLClassesByProperty(ArrayList<String> set, String name, String value) throws OWLOntologyCreationException {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		OWLOntology ontology = getRootOntology();
		
		for (OWLClass cls : ontology.getClassesInSignature(true)) {
			if (set != null && !set.contains(cls.getIRI().toString())) continue;
			
			for (OWLAnnotationProperty property : getOWLAnnotationPropertiesFromString(name)) {
	    		@SuppressWarnings({ "unchecked", "rawtypes" })
				Set<OWLObject> values = (Set)cls.getAnnotations(ontology, property);
	    		if (values.isEmpty() || resultset.contains(cls))
	    			continue;
	    		
	    		if (valueSetContains(values, value))
		    		resultset.add(cls);
	    	}
			
			for (OWLObjectProperty property : getOWLObjectPropertiesFromString(name)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Set<OWLObject> values = (Set)cls.getObjectPropertiesInSignature();
				if (!values.contains(property) || resultset.contains(cls)) // @todo: filter values by property
	    			continue;
	    		
	    		if (valueSetContains(values, value))
		    		resultset.add(cls);
	    	}
		}
		
		return resultset;
	}
	
	private ArrayList<OWLEntity> extractOWLEntitiesByProperty(ArrayList<String> set, String name, String value) throws OWLOntologyCreationException {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		OWLOntology ontology = getRootOntology();
	    	
	    for (OWLDataProperty property : getOWLDataPropertiesFromString(name)) {
	    	for (OWLEntity entity : ontology.getSignature(true)) {
		    	if (set != null && !set.contains(entity.getIRI().toString())) continue;
	    		
		    	@SuppressWarnings({ "unchecked", "rawtypes" })
				Set<OWLObject> values = (Set)entity.getDataPropertiesInSignature();
	    		if (values.isEmpty() || !values.contains(property) || resultset.contains(entity)) // @todo: filter values by property
		    		continue;
	    		
	    		if (valueSetContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
	    	
	    for (OWLObject property : getOWLObjectPropertiesFromString(name)) {
	    	for (OWLEntity entity : ontology.getSignature(true)) {
		    	if (set != null && !set.contains(entity.getIRI().toString())) continue;
	    	
		    	@SuppressWarnings({ "unchecked", "rawtypes" })
				Set<OWLObject> values = (Set)entity.getObjectPropertiesInSignature();
	    		if (values.isEmpty() ||!values.contains(property) || resultset.contains(entity)) // @todo: filter values by property
	    			continue;
		    	
	    		if (valueSetContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
	    
	    for (OWLAnnotationProperty property : getOWLAnnotationPropertiesFromString(name)) {
	    	for (OWLEntity entity : ontology.getSignature(true)) {
		    	if (set != null && !set.contains(entity.getIRI().toString())) continue;
		    	
		    	@SuppressWarnings({ "rawtypes", "unchecked" })
				Set<OWLObject> values = (Set)entity.getAnnotations(ontology, property);
	    		if (values.isEmpty() || resultset.contains(entity))
	    			continue;
	    		
		    	if (valueSetContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
		
	    return resultset;
	}
	
	/**
	 * Checks if set of values contains a value or value is null.
	 * @param valueSet
	 * @param value
	 * @return true if valueSet contains value or value is null, else false
	 */
	private boolean valueSetContains(Set<OWLObject> valueSet, String value) {
		for (OWLObject curValue : valueSet) {
    		if (value == null || value.equals("")
    			|| curValue.toString().replaceAll("^.*?\"|\"\\^.*$", "").equals(value)
    		) {
    			return true;
    		}
    	}
		return false;
	}
	
	private ArrayList<OWLEntity> extractOWLNamedIndividualByProperty(ArrayList<String> set, String name, String value) throws OWLOntologyCreationException {
		ArrayList<OWLEntity> entities = extractOWLEntitiesByProperty(set, name, value);
		
		for (OWLEntity entity : entities) {
			if (!entity.isOWLNamedIndividual())
				entities.remove(entity);
		}
		
		return entities;
	}
	
	
	private ArrayList<OWLDataProperty> getOWLDataPropertiesFromString(String name) throws OWLOntologyCreationException {
		OWLOntology ontology = getRootOntology();
		ArrayList<OWLDataProperty> properties = new ArrayList<OWLDataProperty>();
		
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature(true)) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}
	
	private ArrayList<OWLObjectProperty> getOWLObjectPropertiesFromString(String name) throws OWLOntologyCreationException {
		OWLOntology ontology = getRootOntology();
		ArrayList<OWLObjectProperty> properties = new ArrayList<OWLObjectProperty>();
		
		for (OWLObjectProperty property : ontology.getObjectPropertiesInSignature(true)) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}
	
	private ArrayList<OWLAnnotationProperty> getOWLAnnotationPropertiesFromString(String name) throws OWLOntologyCreationException {
		OWLOntology ontology = getRootOntology();
		ArrayList<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();
		
		for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property)) {
				properties.add(property);
			}
		}
		
		return properties;
	}
	
	
 	private ArrayList<OWLEntityProperties> getPropertiesForOWLEntities(ArrayList<OWLEntity> entities) throws Exception {
		ArrayList<OWLEntityProperties> properties = new ArrayList<OWLEntityProperties>();

		for (OWLEntity entity : entities) {
	    	properties.add(getPropertiesForOWLEntity(entity));
	    }
		return properties;
	}
 	
 		
	private OWLEntityProperties getPropertiesForOWLEntity(OWLEntity entity) throws Exception {
		if (entity.isOWLClass()) {
			return getPropertiesForOWLClass((OWLClass) entity);
		} else if (entity.isOWLNamedIndividual()) {
			return getPropertiesForOWLNamedIndividual((OWLNamedIndividual) entity);
		} else {
			OWLEntityProperties result = new OWLEntityProperties();
			result.iri = entity.getIRI().toString();
			return result;
		}
	}
	
	private OWLClassProperties getPropertiesForOWLClass(OWLClass cls) throws OWLOntologyCreationException {
		OWLClassProperties properties = new OWLClassProperties();
    	OWLOntology ontology = getRootOntology();
    	
    	properties.iri = cls.getIRI().toString();
    	properties.addSuperClassExpressions(cls.getSuperClasses(getOntologies()));
    	properties.addSubClassExpressions(cls.getSubClasses(getOntologies()));
    	
    	for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			Set<OWLAnnotation> values = cls.getAnnotations(ontology, property);
			properties.addAnnotationProperty(property, values);
		}
    	
    	return properties;
	}
	
	private OWLNamedIndividualProperties getPropertiesForOWLNamedIndividual(OWLNamedIndividual individual) throws OWLOntologyCreationException, InterruptedException {
		OWLNamedIndividualProperties properties = new OWLNamedIndividualProperties();
		OWLOntology ontology = getRootOntology();
		
		properties.iri = individual.getIRI().toString();
		
		Map<OWLDataPropertyExpression, Set<OWLLiteral>> dataProperties = individual.getDataPropertyValues(ontology);
		for (OWLDataPropertyExpression property : dataProperties.keySet()) {
			properties.addDataProperty(property, dataProperties.get(property));
		}
		
		Map<OWLObjectPropertyExpression, Set<OWLIndividual>> objectProperties = individual.getObjectPropertyValues(ontology);
		for (OWLObjectPropertyExpression property : objectProperties.keySet()) {
			properties.addObjectProperty(property, objectProperties.get(property));
		}
		
		for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			Set<OWLAnnotation> values = individual.getAnnotations(ontology, property);
			properties.addAnnotationProperty(property, values);
		}
		
		for (OWLClassExpression type : individual.getTypes(ontology)) {
			properties.addTypeExpression(type);
		}
		
		return properties;
	}
	
	private ArrayList<OWLEntity> extractEntitiesWithFilter(ArrayList<String> set, String name, Filter filter) throws OWLOntologyCreationException {		
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		
		OWLOntology ontology = getRootOntology();
	    
	    for (OWLEntity entity : ontology.getSignature(true)) {
	    	if (!filter.run(entity, name)) continue;
	    	if (set != null && !set.contains(entity.getIRI().toString())) continue;
	    	
	    	if (!resultset.contains(entity))
	    		resultset.add(entity);
	    }
		
	    return resultset;
	}
	
		
	@SuppressWarnings("unchecked")
	private OWLOntology getRootOntology() throws OWLOntologyCreationException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.addOntologyStorer(new BinaryOWLOntologyDocumentStorer());
	    
        ArrayList<File> documents = new ArrayList<File>(
        	Arrays.asList((new File(importsPath)).listFiles())
        );
        for (File ontologyDocument : documents) {
        	if (ontologyDocument.isHidden() || ontologyDocument.isDirectory())
        		documents.remove(ontologyDocument);
        }
        
        int counter = 0, maxTries = documents.size();
        while (!documents.isEmpty() && counter <= maxTries) {
        	for (File ontologyDocument : (ArrayList<File>)documents.clone()) {
        		try {
        			manager.loadOntologyFromOntologyDocument(ontologyDocument);
        			documents.remove(ontologyDocument);
        		} catch (UnparsableOntologyException e) {
        			System.err.println(
        				"Could not parse ontology " + ontologyDocument.getName() + ". "
        				+ "Trying other imports first."
        			);
        		}
        	}
        }
        		
		return manager.loadOntologyFromOntologyDocument(new File(rootPath));
	}
	
	private Set<OWLOntology> getOntologies() throws OWLOntologyCreationException {
		return getRootOntology().getImportsClosure();
	}
	
	
	
	abstract class Filter {
		public abstract boolean run(OWLEntity a, String b);
	}

}
