package de.onto_med.webprotege_rest_api.ontology;

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
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

import de.onto_med.webprotege_rest_api.RestApiApplication;
import de.onto_med.webprotege_rest_api.api.OWLEntityProperties;

public class BinaryOwlParser extends OntologyParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestApiApplication.class);
	private static final Double MATCH_THRESHOLD = 0.8;
	
	private String importsPath;
	private String projectPath;
	private String rootPath;
	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private String projectId;
	
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BinaryOwlParser parser = new BinaryOwlParser(
			"702fdf23-882e-41cf-9d8d-0f589e7632a0",
			"H:/Projekte/Leipzig Health Atlas/Development/Web-Service/data/webprotege/"
		);
		
		OWLOntology ontology      = parser.getRootOntology();
		OWLDataFactory factory    = parser.manager.getOWLDataFactory();
		OWLNamedIndividual entity = factory.getOWLNamedIndividual(IRI.create("http://www.lha.org/pol#GSE61374_RAW"));
		
		// ...
	}

	/**
	 * Constructor
	 * @param projectId id of the project
	 * @param dataPath path to WebProtégé data folder
	 */
	public BinaryOwlParser(String projectId, String dataPath) {
		super(dataPath);
		this.projectId = projectId;
		projectPath = dataPath + "/data-store/project-data/" + projectId;
		rootPath    = projectPath + "/ontology-data/root-ontology.binary";
		importsPath = projectPath + "/imports-cache";
		manager = getOwlOntologyManager();
	}
	
	
	public String getProjectIri()  {
		return getRootOntology().getOntologyID().getOntologyIRI().get().toString();
	}
	
	
	/**
	 * Searches for entities which match the class expression.
	 * @param string class expression as string
	 * @return List of entities 
	 */
	public ArrayList<OWLEntityProperties> getEntityProperties(String string) {
		@SuppressWarnings("deprecation")
		OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(getRootOntology());
		OWLClassExpression ce = convertStringToClassExpression(string);
		ArrayList<OWLEntityProperties> result = new ArrayList<OWLEntityProperties>();
		
		for (Node<OWLNamedIndividual> node : reasoner.getInstances(ce, false)) {
			result.add(getPropertiesForOWLEntity(node.iterator().next()));
		}
		
		return result;
	}
	
	
	public ArrayList<OWLEntityProperties> getEntityProperties(
		String iri, String name, String property, String value, Boolean exact, Boolean and, Class<?> cls
	) throws NoSuchAlgorithmException {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
		
		for (OWLEntity entity : getRootOntology().getSignature(Imports.INCLUDED)) {
			Boolean iriMatch      = false;
			Boolean nameMatch     = false;
			Boolean propertyMatch = false;
			
			if (StringUtils.isNotEmpty(iri)) {
				if (exact) {
					if (iri.equals(entity.getIRI().toString()))
						iriMatch = true;
				} else {
					if (StringUtils.getJaroWinklerDistance(iri, entity.getIRI().toString()) >= MATCH_THRESHOLD)
						iriMatch = true;
				}
			}
			
			if (StringUtils.isNotEmpty(name)) {
				if (exact) {
					if (iri.equals(entity.getIRI().toString()))
						nameMatch = true;
				} else {
					if (StringUtils.getJaroWinklerDistance(name, getLabel(entity)) >= MATCH_THRESHOLD
						|| StringUtils.getJaroWinklerDistance(name, XMLUtils.getNCNameSuffix(entity.getIRI())) >= MATCH_THRESHOLD
					) nameMatch = true;
				}
			}
			
			if (StringUtils.isNotEmpty(property)) {
				if (hasProperty(entity, property, value, exact))
					propertyMatch = true;
			}
			
			if (and) {
				if (StringUtils.isNotEmpty(iri) && !iriMatch
					|| StringUtils.isNotEmpty(name) && !nameMatch
					|| StringUtils.isNotEmpty(property) && !propertyMatch
				) continue;
			} else {
				if (!iriMatch && !nameMatch && !propertyMatch)
					continue;
			}
			
			if (cls.equals(OWLEntity.class)) {
				resultset.add(entity);
			} else if (cls.equals(OWLClass.class)) {
				if (entity.isOWLClass())
					resultset.add(entity);
			} else if (cls.equals(OWLIndividual.class)) {
				if (entity.isOWLNamedIndividual())
					resultset.add(entity);
			} else {
				throw new NoSuchAlgorithmException("Error: class " + cls.getName() + " is not supported by this method.");
			}
		}
		
		return getPropertiesForOWLEntities(resultset);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Boolean hasProperty(OWLEntity entity, String property, String value, Boolean exact) {
		ArrayList<OWLEntity> resultset = new ArrayList<OWLEntity>();
    	
	    for (OWLDataProperty dataProperty : extractPropertyByNameFromSet(
	    		property, getRootOntology().getDataPropertiesInSignature(Imports.INCLUDED), exact
	    )) {
			Collection<OWLObject> values = (Collection) EntitySearcher.getDataPropertyValues((OWLIndividual) entity, dataProperty, getRootOntology());
	    	
			if (values.isEmpty()) continue;
	    	if (valueCollectionContains(values, value)) resultset.add(entity);
	    }
	    	
	    for (OWLObjectProperty objectProperty : extractPropertyByNameFromSet(
	    		property, getRootOntology().getObjectPropertiesInSignature(Imports.INCLUDED), exact
	    )) {
	    	Collection<OWLObject> values = (Collection) EntitySearcher.getObjectPropertyValues((OWLIndividual) entity, objectProperty, getRootOntology());
	    	
	    	if (values.isEmpty()) continue;
		    if (valueCollectionContains(values, value)) return true;
	    }
	    
	    for (OWLAnnotationProperty annotationProperty : extractPropertyByNameFromSet(
	    		property, getRootOntology().getAnnotationPropertiesInSignature(Imports.INCLUDED), exact
	    )) {
	    	Collection<OWLObject> values = (Collection) EntitySearcher.getAnnotations(entity, getRootOntology(), annotationProperty);
	    		
	    	if (values.isEmpty()) continue;
	    	if (valueCollectionContains(values, value)) return true;
	    }
		
		return false;
	}
	
	
	public int countEntities(Class<?> cls) {
		if (cls.equals(OWLObjectProperty.class)) {
			return getRootOntology().getObjectPropertiesInSignature(Imports.INCLUDED).size();
		} else if (cls.equals(OWLLogicalAxiom.class)) {
			return getRootOntology().getLogicalAxiomCount(Imports.INCLUDED);
		} else if (cls.equals(OWLIndividual.class)) {
			return getRootOntology().getIndividualsInSignature(Imports.INCLUDED).size();
		} else if (cls.equals(OWLDataProperty.class)) {
			return getRootOntology().getDataPropertiesInSignature(Imports.INCLUDED).size();
		} else if (cls.equals(OWLClass.class)) {
			return getRootOntology().getClassesInSignature(Imports.INCLUDED).size();
		} else if (cls.equals(OWLAxiom.class)) {
			return getRootOntology().getAxiomCount(Imports.INCLUDED);
		} else if (cls.equals(OWLAnnotationProperty.class)) {
			return getRootOntology().getAnnotationPropertiesInSignature(Imports.INCLUDED).size();
		} else {
			return 0;
		}
	}
	
	
	/**
	 * Returns the full RDF document for this ontology as string.
	 * @return string containing the full RDF document.
	 */
	public Object getFullRDFDocument() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		try {
			getRootOntology().getOWLOntologyManager().saveOntology(
				getRootOntology(), new RDFXMLDocumentFormat(), outputStream
			);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		
		return outputStream.toString();
	}
	
	
	/**
	 * Returns a list of imported ontology ids.
	 * @return List of imported ontology ids
	 */
	public ArrayList<String> getImportedOntologyIds() {
		ArrayList<String> imports = new ArrayList<String>();
		
		for (OWLOntology ontology : getRootOntology().getImports()) {
			imports.add(ontology.getOntologyID().toString());
		}
		
		return imports;
	}
	
	
	/**
	 * Returns shortforms and iris for each loaded ontology.
	 * @return HashMap with key: shortform and value: iri
	 */
	public HashMap<String, String> getOntologyIris() {
		getRootOntology();
		HashMap<String, String> map = new HashMap<String, String>();
		
		for (OWLOntology ontology : manager.getOntologies()) {
			IRI iri = ontology.getOntologyID().getOntologyIRI().get();
			map.put(iri.getShortForm(), iri.toString());
		}
		
		return map;
	}
	
	public boolean isConsistent() {
		@SuppressWarnings("deprecation")
		OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(getRootOntology());
		
		return reasoner.isConsistent();
	}
	
	
	private OWLClassExpression convertStringToClassExpression(String expression) {
        ManchesterOWLSyntaxParserImpl parser = (ManchesterOWLSyntaxParserImpl) OWLManager.createManchesterParser();
        OWLEntityChecker owlEntityChecker = new ShortFormEntityChecker(getShortFormProvider());
		parser.setOWLEntityChecker(owlEntityChecker);
        parser.setDefaultOntology(getRootOntology());

        return parser.parseClassExpression(expression);
    }
	
	
	private <T> ArrayList<T> extractPropertyByNameFromSet(String name, Set<T> properties, Boolean exact) {
		ArrayList<T> results = new ArrayList<T>();
		
		for (T property : properties)
			if (exact) {
				if (XMLUtils.getNCNameSuffix(((OWLNamedObject) property).getIRI()).equals(name) && !results.contains(property))
					results.add(property);
			} else {
				if (StringUtils.getJaroWinklerDistance(XMLUtils.getNCNameSuffix(((OWLNamedObject) property).getIRI()), name) >= MATCH_THRESHOLD)
					results.add(property);
			}
		
		return results;
	}
	
	
	/**
	 * Creates and returns an OWLOntologyManager
	 * @return ontology manager
	 */
	private OWLOntologyManager getOwlOntologyManager() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		Set<OWLParserFactory> parserFactories = new HashSet<OWLParserFactory>();
		parserFactories.add(new BinaryOWLOntologyDocumentParserFactory());
		manager.setOntologyParsers(parserFactories);

		return manager;
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
	 * Returns properties for a single entity.
	 * @param entity entity object
	 * @return properties as OWLEntityProperties
	 */
	private OWLEntityProperties getPropertiesForOWLEntity(OWLEntity entity) {
		OWLEntityProperties properties = new OWLEntityProperties();
		@SuppressWarnings("deprecation")
		OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(getRootOntology());
		
		properties.setProjectId(projectId);
    	properties.setIri(entity.getIRI().toString());
    	properties.setJavaClass(entity.getClass().getName());
    	
    	for (OWLAnnotationAssertionAxiom property : EntitySearcher.getAnnotationAssertionAxioms(entity, getRootOntology())) {
			properties.addAnnotationProperty(property.getProperty(), property.getValue());
		}
    	
    	if (entity.isOWLClass()) {
    		properties.addSuperClassExpressions(reasoner.getSuperClasses(entity.asOWLClass(), true));
    		properties.addSubClassExpressions(reasoner.getSubClasses(entity.asOWLClass(), true));
			properties.addIndividuals(reasoner.getInstances(entity.asOWLClass(), true));
    		properties.addDisjointClasses(reasoner.getDisjointClasses(entity.asOWLClass()));
    		properties.addEquivalentClasses(reasoner.getEquivalentClasses(entity.asOWLClass()));
    	}
    	
    	if (entity.isOWLNamedIndividual()) {
	    	Multimap<OWLDataPropertyExpression, OWLLiteral> dataProperties = EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), getRootOntology());
			for (OWLDataPropertyExpression property : dataProperties.keySet()) {
				properties.addDataProperty(property, dataProperties.get(property));
			}
			
			Multimap<OWLObjectPropertyExpression, OWLIndividual> objectProperties = EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), getRootOntology());
			for (OWLObjectPropertyExpression property : objectProperties.keySet()) {
				properties.addObjectProperty(property, objectProperties.get(property));
			}
			
			properties.addTypes(reasoner.getTypes(entity.asOWLNamedIndividual(), true));
			properties.addSameIndividuals(reasoner.getSameIndividuals(entity.asOWLNamedIndividual()));
    	}
    	
    	reasoner.dispose();
    	return properties;
	}
	
	
	/**
	 * Returns the root-ontology with all loaded imports or null if the ontology loading fails.
	 * This function tries to load all imports before loading the ontology.
	 * Depending on their filename, the order of loading may vary and errors can occure.
	 * When ever an error occures, the concerned document remains as 'not loaded',
	 * so the function can try to load it in the next iteration.
	 * @return root-ontology
	 */	
	@SuppressWarnings("unchecked")
	private OWLOntology getRootOntology() {
		if (ontology != null) return ontology;
		
		LOGGER.info(String.format("Parsing project '%s' for the first time.", projectId));
		
        ArrayList<File> documents = new ArrayList<File>(
        	Arrays.asList((new File(importsPath)).listFiles())
        );
        documents.removeIf(d -> d.isHidden() || d.isDirectory());
        
        try {
	        int counter  = 0;
	        int maxTries = documents.size();
	        while (!documents.isEmpty() && counter <= maxTries) {
	        	counter++;
	        	for (File ontologyDocument : (ArrayList<File>) documents.clone()) {
	        		try {
	        			manager.loadOntologyFromOntologyDocument(ontologyDocument);
	        			documents.remove(ontologyDocument);
	        		} catch (UnparsableOntologyException e) {}
	        	}
	        }
	      
			ontology = manager.loadOntologyFromOntologyDocument(new File(rootPath));
        } catch (OWLOntologyCreationException e) {
        	e.printStackTrace();
        }
        
		return ontology;
	}
	
	
	private BidirectionalShortFormProvider getShortFormProvider() {
        // TODO: fix non-short shortforms
        ShortFormProvider sfp = new ManchesterOWLSyntaxPrefixNameShortFormProvider(manager.getOntologyFormat(getRootOntology()));
        BidirectionalShortFormProvider shortFormProvider = new BidirectionalShortFormProviderAdapter(manager.getOntologies(), sfp);
        
        return shortFormProvider;
    }
	
	
	private String getLabel(OWLEntity entity) {
		for (OWLAnnotation a : EntitySearcher.getAnnotations(entity, getRootOntology())) {
			if (a.getValue() instanceof OWLLiteral)
				return ((OWLLiteral) a.getValue()).getLiteral();
		}
		return "";
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
}
