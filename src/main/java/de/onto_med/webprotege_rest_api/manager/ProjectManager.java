package de.onto_med.webprotege_rest_api.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.binaryowl.owlapi.BinaryOWLOntologyDocumentParserFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
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
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import com.google.common.collect.Multimap;

import de.onto_med.webprotege_rest_api.api.OWLEntityProperties;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

/**
 * Instances of this class can be used to query a specific project ontology of WebProtegé.
 * 
 * @author Christoph Beger
 */
public class ProjectManager {
	
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
	
	private String projectId;
	private String name;
	private String description;
	
	private OWLOntologyManager manager;
	/**
	 * OWLOntology object for one project.
	 */
	private OWLOntology ontology;
	
	
	
	/**
	 * Constructor.
	 * @param project Instance of class Project in WebProtegés metaproject
	 * @param dataPath Absolute path to WebProtegés data folder.
	 * @throws OWLOntologyCreationException 
	 */
	public ProjectManager(String projectId, String dataPath) {
		this.projectId = projectId;
		path           = dataPath + "/data-store/project-data/" + projectId;
		rootPath       = path + "/ontology-data/root-ontology.binary";
		importsPath    = path + "/imports-cache";
		manager        = getOWLOntologyManager();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	/**
	 * Returns a list of imported ontology ids.
	 * @return List of imported ontology ids
	 * @throws OWLOntologyCreationException 
	 */
	public ArrayList<String> getOntologyImports() throws OWLOntologyCreationException {
		ArrayList<String> imports = new ArrayList<String>();
		
		for (OWLOntology ontology : getRootOntology().getImports()) {
			imports.add(ontology.getOntologyID().toString());
		}
		
		return imports;
	}

	
	/**
	 * Returns a list of OWLEntityProperties for all classes with matching localname.
	 * @param name Localename to search for
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 * @throws OWLOntologyCreationException 
	 */
	public ArrayList<OWLEntityProperties> getClassPropertiesByName(String name, String match) throws OWLOntologyCreationException {	    
	    return getPropertiesForOWLEntities(extractEntitiesWithFilter(name, OWLClassImpl.class, "exact".equals(match)));
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all individuals with matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties 
	 * @throws OWLOntologyCreationException 
	 */
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByName(String name, String match) throws OWLOntologyCreationException {		
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(
			name, OWLNamedIndividualImpl.class, "exact".equals(match)
		));
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all entities witch matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 * @throws OWLOntologyCreationException 
	 */
	public ArrayList<OWLEntityProperties> getEntityPropertiesByName(String name, String match) throws OWLOntologyCreationException {
		return getPropertiesForOWLEntities(extractEntitiesWithFilter(
			name, OWLEntity.class, "exact".equals(match)
		));
	}
	
	
	private ArrayList<OWLEntity> extractOWLClassesByProperty(String name, String value) throws OWLOntologyCreationException {
		ArrayList<OWLEntity> entities = extractOWLEntitiesByProperty(name, value);
		
		for (OWLEntity entity : entities) {
			if (!entity.isOWLNamedIndividual())
				entities.remove(entity);
		}
		
		return entities;
	}

	
	private ArrayList<OWLEntity> extractOWLEntitiesByProperty(String name, String value) throws OWLOntologyCreationException {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
	    	
	    for (OWLDataProperty property : getOWLDataPropertiesFromString(name)) {
	    	for (OWLEntity entity : getRootOntology().getSignature(Imports.INCLUDED)) {
		    	@SuppressWarnings({ "unchecked", "rawtypes" })
				Collection<OWLObject> values = (Collection) EntitySearcher.getDataPropertyValues((OWLIndividual) entity, property, getRootOntology());
	    		if (values.isEmpty() || resultset.contains(entity))
		    		continue;
	    		
	    		if (valueCollectionContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
	    	
	    for (OWLObjectProperty property : getOWLObjectPropertiesFromString(name)) {
	    	for (OWLEntity entity : getRootOntology().getSignature(Imports.INCLUDED)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Collection<OWLObject> values = (Collection) EntitySearcher.getObjectPropertyValues((OWLIndividual) entity, property, getRootOntology());
	    		if (values.isEmpty() || resultset.contains(entity))
	    			continue;
		    	
	    		if (valueCollectionContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
	    
	    for (OWLAnnotationProperty property : getOWLAnnotationPropertiesFromString(name)) {
	    	for (OWLEntity entity : getRootOntology().getSignature(Imports.INCLUDED)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Collection<OWLObject> values = (Collection) EntitySearcher.getAnnotations(entity, getRootOntology(), property);
	    		if (values.isEmpty() || resultset.contains(entity))
	    			continue;
	    		
		    	if (valueCollectionContains(values, value))
		    		resultset.add(entity);
	    	}
	    }
		
	    return resultset;
	}
	
	
	private ArrayList<OWLEntity> extractOWLNamedIndividualByProperty(String name, String value) throws OWLOntologyCreationException {
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
	 * @throws OWLOntologyCreationException 
	 */
	private ArrayList<OWLDataProperty> getOWLDataPropertiesFromString(String name) throws OWLOntologyCreationException {
		ArrayList<OWLDataProperty> properties = new ArrayList<OWLDataProperty>();
		
		for (OWLDataProperty property : getRootOntology().getDataPropertiesInSignature(Imports.INCLUDED)) {
			if (XMLUtils.getNCNameSuffix(property.getIRI()).equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}

	
	/**
	 * Returns a list of all object properties witch matching name.
	 * @param name localname
	 * @return List of found OWLObjectPropertys
	 * @throws OWLOntologyCreationException 
	 */
	private ArrayList<OWLObjectProperty> getOWLObjectPropertiesFromString(String name) throws OWLOntologyCreationException {
		ArrayList<OWLObjectProperty> properties = new ArrayList<OWLObjectProperty>();
		
		for (OWLObjectProperty property : getRootOntology().getObjectPropertiesInSignature(Imports.INCLUDED)) {
			if (XMLUtils.getNCNameSuffix(property.getIRI()).equals(name) && !properties.contains(property))
				properties.add(property);
		}
		
		return properties;
	}
	
	
	/**
	 * Returns a list of all annotationproperties witch matching name.
	 * @param name localname
	 * @return List of found OWLAnnotationpropertys
	 * @throws OWLOntologyCreationException 
	 */
	private ArrayList<OWLAnnotationProperty> getOWLAnnotationPropertiesFromString(String name) throws OWLOntologyCreationException {
		ArrayList<OWLAnnotationProperty> properties = new ArrayList<OWLAnnotationProperty>();
		
		for (OWLAnnotationProperty property : getRootOntology().getAnnotationPropertiesInSignature(Imports.INCLUDED)) {
			if (XMLUtils.getNCNameSuffix(property.getIRI()).equals(name) && !properties.contains(property)) {
				properties.add(property);
			}
		}
		
		return properties;
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for a set of entities.
	 * @param entities set of OWLEntitys
	 * @return List of OWLEntityProperties
	 * @throws OWLOntologyCreationException 
	 */
 	private ArrayList<OWLEntityProperties> getPropertiesForOWLEntities(ArrayList<OWLEntity> entities) throws OWLOntologyCreationException {
		ArrayList<OWLEntityProperties> properties = new ArrayList<OWLEntityProperties>();

		for (OWLEntity entity : entities) {
	    	properties.add(getPropertiesForOWLEntity(entity));
	    }
		return properties;
	}
	
	/**
	 * Returns properties for a single entity.
	 * @param entity entity object
	 * @return properties as OWLEntityProperties
	 * @throws OWLOntologyCreationException 
	 */
	private OWLEntityProperties getPropertiesForOWLEntity(OWLEntity entity) throws OWLOntologyCreationException {
		OWLEntityProperties properties = new OWLEntityProperties();
    	
    	properties.setIri(entity.getIRI().toString());
    	properties.setJavaClass(entity.getClass().getName());
    	
    	for (OWLAnnotationProperty property : getRootOntology().getAnnotationPropertiesInSignature()) {
			Collection<OWLAnnotation> values = EntitySearcher.getAnnotations(entity, getRootOntology(), property);
			properties.addAnnotationProperty(property, values);
		}
    	
    	if (entity.isOWLClass()) {
    		properties.addSuperClassExpressions(EntitySearcher.getSuperClasses(entity.asOWLClass(), getRootOntology().getImportsClosure()));
    		properties.addSubClassExpressions(EntitySearcher.getSubClasses(entity.asOWLClass(), getRootOntology().getImportsClosure()));
    	}
    	
    	if (entity.isOWLNamedIndividual()) {
	    	Multimap<OWLDataPropertyExpression, OWLLiteral> dataProperties = EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), getRootOntology());
			for (OWLDataPropertyExpression property : dataProperties.keySet()) {
				properties.addDataTypeProperty(property, dataProperties.get(property));
			}
			
			Multimap<OWLObjectPropertyExpression, OWLIndividual> objectProperties = EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), getRootOntology());
			for (OWLObjectPropertyExpression property : objectProperties.keySet()) {
				properties.addObjectProperty(property, objectProperties.get(property));
			}
			
			for (OWLClassExpression type : EntitySearcher.getTypes(entity.asOWLNamedIndividual(), getRootOntology())) {
				properties.addTypeExpression(type);
			}
    	}
    	
    	return properties;
	}
	
	
	/**
	 * Returns a list of OWLEntities which match the given filter criteria.
	 * @param name entity name
	 * @param filter Filter object which uses parameter name
	 * @return Resulting list of OWLEntities
	 * @throws OWLOntologyCreationException 
	 */
	private ArrayList<OWLEntity> extractEntitiesWithFilter(String name, Class<?> cls, Boolean match) throws OWLOntologyCreationException {		
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
	    
	    for (OWLEntity entity : getRootOntology().getSignature(Imports.INCLUDED)) {
	    	if (!Filter.run(entity, name, cls, match)) continue;
	    	
	    	if (!resultset.contains(entity))
	    		resultset.add(entity);
	    }
		
	    return resultset;
	}
	
	
	/**
	 * Searches for OWLEntities with given type, and name.
	 * @param type 'entity', 'individual' or 'class', defaults to 'entity'
	 * @param name localename of the entity to search for
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of OWLEntityProperties for found entities
	 * @throws Exception If the specified type is not one of 'entity', 'individual' and 'class', or the project was not found
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntityByName(
		String type, String name, String match
	) throws Exception {
		if (StringUtils.isEmpty(type)) type = "entity";
		
		switch (type) {
			case "entity":
				return getEntityPropertiesByName(name, match);
			case "individual":
				return getNamedIndividualPropertiesByName(name, match);
			case "class":
				return getClassPropertiesByName(name, match);
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
	}
	
	
	/**
	 * Searches for OWLEntities which are annotated with a property with given name.
	 * @param type 'entity', 'individual' or 'class', defaults to 'entity'
	 * @param property localename of the property
	 * @param value with property annotated value or null for no value check
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of OWLEntityProperties for found entities
	 * @throws Exception If the specified type is not one of 'entity', 'individual' and 'class', or the project was not found
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntityByProperty(
		String type, String property, String value, String match
	) throws Exception {
		ArrayList<OWLEntity> entities;
		
		if (StringUtils.isEmpty(type)) type = "entity";
		
		switch (type) {
			case "individual":
				entities = extractOWLNamedIndividualByProperty(property, value);
				break;
			case "class":
				entities = extractOWLClassesByProperty(property, value);
				break;
			case "entity":
				entities = extractOWLEntitiesByProperty(property, value);
				break;
			default:
				throw new NoSuchAlgorithmException("OWL type '" + type + "' does not exist or is not implemented.");
		}
		
		return getPropertiesForOWLEntities(entities);
	}
	
	
	/**
	 * Returns the root-ontology with all loaded imports.
	 * This function tries to load all imports before loading the ontology.
	 * Depending on their filename, the order of loading may vary and errors can occure.
	 * When ever an error occures, the concerned document remains as 'not loaded',
	 * so the function can try to load it in the next iteration.
	 * @return root-ontology
	 * @throws OWLOntologyCreationException If there was an error while parsing the ontology
	 */	
	@SuppressWarnings("unchecked")
	private OWLOntology getRootOntology() throws OWLOntologyCreationException {
		if (ontology != null) return ontology;
		
		System.err.println("Parsing project " + getProjectId() + " for the first time.");
        ArrayList<File> documents = new ArrayList<File>(
        	Arrays.asList((new File(importsPath)).listFiles())
        );
        for (File ontologyDocument : documents) {
        	if (ontologyDocument.isHidden() || ontologyDocument.isDirectory())
        		documents.remove(ontologyDocument);
        }
        
        int counter  = 0;
        int maxTries = documents.size();
        while (!documents.isEmpty() && counter <= maxTries) {
        	counter++;
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
      
		ontology = manager.loadOntologyFromOntologyDocument(new File(rootPath));
		return ontology;
	}
	
	
	/**
	 * Creates and returns an OWLOntologyManager
	 * @return ontology manager
	 */
	private OWLOntologyManager getOWLOntologyManager() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		Set<OWLParserFactory> parserFactories = new HashSet<OWLParserFactory>();
		parserFactories.add(new BinaryOWLOntologyDocumentParserFactory());
		manager.setOntologyParsers(parserFactories);

		return manager;
	}
	

	/**
	 * Returns the full RDF document for this ontology as string.
	 * @return string containing the full RDF document.
	 * @throws OWLOntologyStorageException If ontology could not be transformed into a string.
	 * @throws OWLOntologyCreationException 
	 */
	public Object getFullRDFDocument() throws OWLOntologyStorageException, OWLOntologyCreationException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getRootOntology().getOWLOntologyManager().saveOntology(getRootOntology(), new RDFXMLDocumentFormat(), outputStream);
		return outputStream.toString();
	}

	
	/**
	 * Checks if set of values contains a value or value is null.
	 * @param values
	 * @param value
	 * @return true if valueSet contains value or value is null, else false
	 */
	private boolean valueCollectionContains(Collection<OWLObject> values, String value) {
		for (OWLObject curValue : values) {
    		if (StringUtils.isEmpty(value)
    			|| curValue.toString().replaceAll("^.*?\"|\"\\^.*$", "").equals(value)
    		) {
    			return true;
    		}
    	}
		return false;
	}
	
	
	/**
	 * Searches for individuals which match the class expression.
	 * @param string class expression as string
	 * @return List of named individuals
	 * @throws OWLOntologyCreationException 
	 */
	@SuppressWarnings("deprecation")
	public ArrayList<OWLEntityProperties> getIndividualPropertiesByClassExpression(String string) throws OWLOntologyCreationException {
		OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(getRootOntology());
		OWLClassExpression ce = convertStringToClassExpression(string);
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		for (Node<OWLNamedIndividual> node : reasoner.getInstances(ce, false)) {
			result.add(getPropertiesForOWLEntity(node.iterator().next()));
		}
		
		return result;
	}
	
	
	private OWLClassExpression convertStringToClassExpression(String expression) throws OWLOntologyCreationException {
        ManchesterOWLSyntaxParserImpl parser = (ManchesterOWLSyntaxParserImpl) OWLManager.createManchesterParser();
        OWLEntityChecker owlEntityChecker = new ShortFormEntityChecker(getShortFormProvider());
		parser.setOWLEntityChecker(owlEntityChecker);
        parser.setDefaultOntology(getRootOntology());

        return parser.parseClassExpression(expression);
    }
	
	
	private BidirectionalShortFormProvider getShortFormProvider() throws OWLOntologyCreationException {
        // TODO: fix non-short shortforms
        ShortFormProvider sfp = new ManchesterOWLSyntaxPrefixNameShortFormProvider(manager.getOntologyFormat(getRootOntology()));
        BidirectionalShortFormProvider shortFormProvider = new BidirectionalShortFormProviderAdapter(manager.getOntologies(), sfp);
        
        return shortFormProvider;
    }
	
	
	public String getProjectId() {
		return projectId;
	}
	
	public String getName() {
		return StringUtils.defaultString(name, "");
	}
	
	public String getDescription() {
		return StringUtils.defaultString(description, "");
	}
	
	
	public int getCountAxioms() throws OWLOntologyCreationException {
		return getRootOntology().getAxiomCount(Imports.INCLUDED);
	}
	
	public int getCountLogicalAxioms() throws OWLOntologyCreationException {
		return getRootOntology().getLogicalAxiomCount(Imports.INCLUDED);
	}
	
	public int getCountClasses() throws OWLOntologyCreationException {
		return getRootOntology().getClassesInSignature(Imports.INCLUDED).size();
	}
	
	public int getCountIndividuals() throws OWLOntologyCreationException {
		return getRootOntology().getIndividualsInSignature(Imports.INCLUDED).size();
	}
	
	public int getCountDataTypeProperties() throws OWLOntologyCreationException {
		return getRootOntology().getDataPropertiesInSignature(Imports.INCLUDED).size();
	}
	
	public int getCountObjectProperties() throws OWLOntologyCreationException {
		return getRootOntology().getObjectPropertiesInSignature(Imports.INCLUDED).size();
	}
	
	public int getCountAnnotationProperties() throws OWLOntologyCreationException {
		return getRootOntology().getAnnotationPropertiesInSignature(Imports.INCLUDED).size();
	}
	
	
	public String getProjectIri() throws OWLOntologyCreationException  {
		return getRootOntology().getOntologyID().getOntologyIRI().get().toString();
	}
	
	
	/**
	 * Returns shortforms and iris for each loaded ontology.
	 * @return HashMap with key: shortform and value: iri
	 * @throws OWLOntologyCreationException 
	 */
	public HashMap<String, String> getOntologyIris() throws OWLOntologyCreationException {
		getRootOntology();
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (OWLOntology ontology : manager.getOntologies()) {
			IRI iri = ontology.getOntologyID().getOntologyIRI().get();
			map.put(iri.getShortForm(), iri.toString());
		}
		
		return map;
	}
	
	
	/**
	 * Abstract filter class.
	 * @author Christoph Beger
	 */
	abstract static class Filter {
		
		public static boolean run(OWLEntity entity, String name, Class<?> cls, Boolean exact) {
			if (!cls.isAssignableFrom(entity.getClass())) return false;
			
			if (exact) {
				return exactMatch(entity, name);
			} else {
				return looseMatch(entity, name);
			}
		}
		
		private static boolean exactMatch(OWLEntity entity, String name) {
			return XMLUtils.getNCNameSuffix(entity.getIRI()).equals(name);
		}
		
		private static boolean looseMatch(OWLEntity entity, String name) {
			return StringUtils.getJaroWinklerDistance(XMLUtils.getNCNameSuffix(entity.getIRI()), name) >= THRESHOLD;
		}
	}




}
