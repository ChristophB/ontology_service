package de.uni_leipzig.imise.webprotege.rest_api.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentParserFactory;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentStorer;
import org.semanticweb.owlapi.apibinding.OWLManager;
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

/**
 * Instances of this class can be used to query a specific project ontology of WebProtegé.
 * 
 * @author Christoph Beger
 */
public class OntologyManager {
	
	/**
	 * Threshhold for Jaro Winkler Distance comparisson.
	 */
	private static final Double THRESHOLD = 0.8;
	
	/**
	 * Relative path to the project folder.
	 */
	private String path;
	
	/**
	 * Absolute path to WebProtegés data folder.
	 */
	private String rootPath;
	
	/**
	 * Relative path to imported ontology documents.
	 */
	private String importsPath;
	
	/**
	 * OWLOntology object for one project.
	 */
	private OWLOntology ontology;
	
	
	/**
	 * Adds the BinaryOWLOntologyDocumentParserFactory to OWLParserFactoryRegistry for parsing binaryowl files.
	 */
	static {
	    OWLManager.getOWLDataFactory();
	    OWLParserFactoryRegistry parserFactoryRegistry = OWLParserFactoryRegistry.getInstance();
	    parserFactoryRegistry.clearParserFactories();
	    parserFactoryRegistry.registerParserFactory(new BinaryOWLOntologyDocumentParserFactory());
	}
	
	
	
	/**
	 * Constructor.
	 * @param project Instance of class Project in WebProtegés metaproject
	 * @param dataPath Absolute path to WebProtegés data folder.
	 * @throws OWLOntologyCreationException 
	 */
	public OntologyManager(Instance project, String dataPath) throws OWLOntologyCreationException {
		path        = dataPath + "/data-store/project-data/" + project.getName();
		rootPath    = path + "/ontology-data/root-ontology.binary";
		importsPath = path + "/imports-cache";
		
		ontology = getRootOntology();
	}
	
	
	/**
	 * Returns a list of imported ontology ids.
	 * @return List of imported ontology ids
	 */
	public ArrayList<String> getOntologyImports() {
		ArrayList<String> imports = new ArrayList<String>();
		
		for (OWLOntology ontology : ontology.getImports()) {
			imports.add(ontology.getOntologyID().toString());
		}
		
		return imports;
	}

	
	/**
	 * Returns a list of OWLEntityProperties for all classes with matching localname.
	 * @param name Localename to search for
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 */
	public ArrayList<OWLEntityProperties> getClassPropertiesByName(String name, String match) {
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
	    
	    return getPropertiesForOWLEntities(extractEntitiesWithFilter(name, filter));
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all individuals with matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties 
	 */
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByName(String name, String match) {
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
		
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(name, filter));
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all entities witch matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 */
	public ArrayList<OWLEntityProperties> getEntityPropertiesByName(String name, String match) {
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
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(name, filter));
	}
	
	
	public ArrayList<OWLEntityProperties> getClassPropertiesByProperty(String property, String value) {
	    return getPropertiesForOWLEntities(extractOWLClassesByProperty(property, value));
	}
	
	
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByProperty(String property, String value) {
		return getPropertiesForOWLEntities(extractOWLNamedIndividualByProperty(property, value));
	}
	
	
	public ArrayList<OWLEntityProperties> getEntityPropertiesByProperty(String property, String value) {
		return getPropertiesForOWLEntities(extractOWLEntitiesByProperty(property, value));
	}
	
	
	private ArrayList<OWLEntity> extractOWLClassesByProperty(String name, String value) {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		
		for (OWLClass cls : ontology.getClassesInSignature(true)) {
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

	
	private ArrayList<OWLEntity> extractOWLEntitiesByProperty(String name, String value) {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
	    	
	    for (OWLDataProperty property : getOWLDataPropertiesFromString(name)) {
	    	for (OWLEntity entity : ontology.getSignature(true)) {
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
    		if (StringUtils.isEmpty(value)
    			|| curValue.toString().replaceAll("^.*?\"|\"\\^.*$", "").equals(value)
    		) {
    			return true;
    		}
    	}
		return false;
	}
	
	
	private ArrayList<OWLEntity> extractOWLNamedIndividualByProperty(String name, String value) {
		ArrayList<OWLEntity> entities = extractOWLEntitiesByProperty(name, value);
		
		for (OWLEntity entity : entities) {
			if (!entity.isOWLNamedIndividual())
				entities.remove(entity);
		}
		
		return entities;
	}
	
	
	/**
	 * Returns a list of all data properties witch matching name
	 * @param name localname
	 * @return List of found OWLDataPropertys
	 */
	private ArrayList<OWLDataProperty> getOWLDataPropertiesFromString(String name) {
		ArrayList<OWLDataProperty> properties = new ArrayList<OWLDataProperty>();
		
		for (OWLDataProperty property : ontology.getDataPropertiesInSignature(true)) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}

	
	/**
	 * Returns a list of all object properties witch matching name.
	 * @param name localname
	 * @return List of found OWLObjectPropertys
	 */
	private ArrayList<OWLObjectProperty> getOWLObjectPropertiesFromString(String name) {
		ArrayList<OWLObjectProperty> properties = new ArrayList<OWLObjectProperty>();
		
		for (OWLObjectProperty property : ontology.getObjectPropertiesInSignature(true)) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}
	
	
	/**
	 * Returns a list of all annotationproperties witch matching name.
	 * @param name localname
	 * @return List of found OWLAnnotationpropertys
	 */
	private ArrayList<OWLAnnotationProperty> getOWLAnnotationPropertiesFromString(String name) {
		ArrayList<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();
		
		for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			if (property.getIRI().getFragment().toString().equals(name) && !properties.contains(property)) {
				properties.add(property);
			}
		}
		
		return properties;
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for a set of entities.
	 * @param entities set of OWLEntitys
	 * @return List of OWLEntityProperties
	 */
 	private ArrayList<OWLEntityProperties> getPropertiesForOWLEntities(ArrayList<OWLEntity> entities) {
		ArrayList<OWLEntityProperties> properties = new ArrayList<OWLEntityProperties>();

		for (OWLEntity entity : entities) {
	    	properties.add(getPropertiesForOWLEntity(entity));
	    }
		return properties;
	}
 	
 	
 	/**
 	 * Returns OWLEntityProperties for a single entity
 	 * @param entity single OWLEntity
 	 * @return OWLEntityProperties for specified entity
 	 */
	private OWLEntityProperties getPropertiesForOWLEntity(OWLEntity entity) {
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
	
	
	/**
	 * Returns properties for a single class.
	 * @param cls class object
	 * @return properties as OWLClassProperties
	 */
	private OWLClassProperties getPropertiesForOWLClass(OWLClass cls) {
		OWLClassProperties properties = new OWLClassProperties();
    	
    	properties.iri = cls.getIRI().toString();
    	properties.addSuperClassExpressions(cls.getSuperClasses(getOntologies()));
    	properties.addSubClassExpressions(cls.getSubClasses(getOntologies()));
    	
    	for (OWLAnnotationProperty property : ontology.getAnnotationPropertiesInSignature()) {
			Set<OWLAnnotation> values = cls.getAnnotations(ontology, property);
			properties.addAnnotationProperty(property, values);
		}
    	
    	return properties;
	}
	
	
	/**
	 * Returns properties for a single individual.
	 * @param individual individual object
	 * @return properties as OWLNamedIndividualProperties
	 */
	private OWLNamedIndividualProperties getPropertiesForOWLNamedIndividual(OWLNamedIndividual individual) {
		OWLNamedIndividualProperties properties = new OWLNamedIndividualProperties();
		
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
	
	
	/**
	 * Returns a list of OWLEntities which match the given filter criteria.
	 * @param name entity name
	 * @param filter Filter object which uses parameter name
	 * @return Resulting list of OWLEntities
	 */
	private ArrayList<OWLEntity> extractEntitiesWithFilter(String name, Filter filter) {		
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
	    
	    for (OWLEntity entity : ontology.getSignature(true)) {
	    	if (!filter.run(entity, name)) continue;
	    	
	    	if (!resultset.contains(entity))
	    		resultset.add(entity);
	    }
		
	    return resultset;
	}
	
	
	/**
	 * Returns the root-ontology witch all loaded imports.
	 * This function tries to load all imports before loading the ontology.
	 * Depending on their filename, the order of loading may vary and errors can occure.
	 * When ever an error occures, the concerned document remains as 'not loaded',
	 * so the function can try to load it in the next iteration.
	 * @return root-ontology
	 * @throws OWLOntologyCreationException If there was an error while parsing the ontology
	 */
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

	
	/**
	 * Returns all ontologies, imports are included.
	 * @return Set of OWLOntologys
	 */
	private Set<OWLOntology> getOntologies() {
		return ontology.getImportsClosure();
	}
	
	
	
	/**
	 * Abstract filter class.
	 * @author Christoph Beger
	 */
	abstract class Filter {
		public abstract boolean run(OWLEntity a, String b);
	}

}
