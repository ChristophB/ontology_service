package de.onto_med.webprotege_rest_api.ontology;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.onto_med.webprotege_rest_api.api.TaxonomyNode;
import de.onto_med.webprotege_rest_api.api.Timer;
import de.onto_med.webprotege_rest_api.api.json.Entity;
import de.onto_med.webprotege_rest_api.api.json.Individual;
import de.onto_med.owlapi_utils.binaryowl.BinaryOwlUtils;
import de.onto_med.owlapi_utils.owlapi.OwlApiUtils;

/**
 * Instances of this class are parsers for binary formated ontologies.
 * @author Christoph Beger
 */
public class BinaryOwlParser extends OntologyParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryOwlParser.class);
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
		
		File projectDir = new File(projectPath);
		if (!(projectDir.exists() && projectDir.isDirectory() && projectDir.canRead()))
			throw new WebApplicationException(String.format(
				"BinaryOwlParser could not access directory for project '%s'.", projectId
			));
		manager = BinaryOwlUtils.getOwlOntologyManager();
	}
	
	/**
	 * Returns the root ontologies IRI as string.
	 * @return root ontology IRI
	 */
	public String getProjectIri() {
		return getRootOntology().getOntologyID().getOntologyIRI().get().toString();
	}
	
	public int countEntities(Class<?> cls) {
		return OwlApiUtils.countEntities(cls, getRootOntology());
	}
	
	/**
	 * Classifies an individual by adding it to the ontology and running a reasoner.
	 * @param individual the individual which will be classifiy
	 * @return list of reasoned classes
	 * @throws NoSuchAlgorithmException
	 */
	public List<String> classifyIndividual(Individual individual) throws NoSuchAlgorithmException {
		return OwlApiUtils.getHermiTReasoner(getRootOntology())
			.getTypes(createNamedIndividual(individual), true).getFlattened().parallelStream()
			.map(e -> e.getIRI().toString())
			.collect(Collectors.toList());
	}
	
	/**
	 * Searches for entities which match the class expression.
	 * @param string class expression as string
	 * @return List of entities 
	 */
	public List<Entity> getEntityPropertiesByClassExpression(String classExpression) {
		OWLClassExpression ce = OwlApiUtils.convertStringToClassExpression(classExpression, getRootOntology());
		
		return OwlApiUtils.getHermiTReasoner(getRootOntology())
			.getInstances(ce, false).getFlattened().parallelStream()
			.map(this::getEntity).collect(Collectors.toList());
	}
	
	
	public List<Entity> getEntityPropertiesByIri(String iri) throws NoSuchAlgorithmException {
		return getEntityProperties(iri, null, null, null, true, false, OWLEntity.class);
	}
	
	/**
	 * Search for OWLNamedIndividuals by name.
	 * @throws NoSuchAlgorithmException
	 */
	public List<Entity> annotate(String name, Boolean exact) throws NoSuchAlgorithmException {
		return getEntityProperties(null, name, null, null, exact, false, OWLClass.class);
	}
	
	/**
	 * Search for OWLEntitys by name.
	 * @throws NoSuchAlgorithmException
	 */
	public List<Entity> getEntityProperties(String name, Boolean exact) throws NoSuchAlgorithmException {
		return getEntityProperties(null, name, null, null, exact, false, OWLEntity.class);
	}
	
	/**
	 * Search for OWLEntities without specified IRI.
	 * @throws NoSuchAlgorithmException
	 */
	public List<Entity> getEntityProperties(String name, String property, String value, Boolean exact, Boolean and) throws NoSuchAlgorithmException {
		return getEntityProperties(null, name, property, value, exact, and, OWLEntity.class);
	}
	
	/**
	 * Search for OWLEntitys.
	 * @throws NoSuchAlgorithmException
	 */
	public List<Entity> getEntityProperties(String iri, String name, String property, String value, Boolean exact, Boolean and) throws NoSuchAlgorithmException {
		return getEntityProperties(iri, name, property, value, exact, and, OWLEntity.class);
	}
	
	/**
	 * Searches for entities based on provided arguments and returns them with their properties.
	 * @param iri the IRI
	 * @param name entity name
	 * @param property property name the searched entities must have
	 * @param value property value
	 * @param exact string match method (true = exact, false = loose)
	 * @param and logical operator (true = and, false = or)
	 * @param cls ontology type restriction (OWLClass, OWLIndividual, OWLEntity)
	 * @return set of entities with properties
	 * @throws NoSuchAlgorithmException
	 */
	public List<Entity> getEntityProperties(
		String iri, String name, String property, String value, Boolean exact, Boolean and, Class<?> cls
	) throws NoSuchAlgorithmException {
		if (!cls.equals(OWLEntity.class) && !cls.equals(OWLClass.class) && !cls.equals(OWLNamedIndividual.class))
			throw new NoSuchAlgorithmException("Error: class " + cls.getName() + " is not supported by this method.");
		
		if (StringUtils.isBlank(iri) && StringUtils.isBlank(name) && StringUtils.isBlank(property))
			return null;
		
		return getRootOntology().getSignature(Imports.INCLUDED).parallelStream()
			.filter(entity -> {
				Boolean iriMatch      = false;
				Boolean nameMatch     = false;
				Boolean propertyMatch = false;
				
				if (StringUtils.isNotBlank(iri)) {
					if (exact && iri.equals(entity.getIRI().toString()))
						iriMatch = true;
					else if (!exact && StringUtils.getJaroWinklerDistance(iri, entity.getIRI().toString()) >= MATCH_THRESHOLD)
						iriMatch = true;
				}
				
				if (StringUtils.isNotBlank(name)) {
					if (exact && iri.equals(entity.getIRI().toString()))
						nameMatch = true;
					else if (!exact && (
						StringUtils.getJaroWinklerDistance(
							name, StringUtils.defaultString(OwlApiUtils.getLabel(entity, getRootOntology()), "")
						) >= MATCH_THRESHOLD
						|| StringUtils.getJaroWinklerDistance(
							name, XMLUtils.getNCNameSuffix(entity.getIRI())
						) >= MATCH_THRESHOLD
					)) nameMatch = true;
				}
				
				if (StringUtils.isNotBlank(property) && hasProperty(entity, property, value, exact))
					propertyMatch = true; 
				
				return (and
					? (StringUtils.isBlank(iri) || iriMatch)
					&& (StringUtils.isBlank(name) || nameMatch)
					&& (StringUtils.isBlank(property) || propertyMatch)
					: iriMatch || nameMatch || propertyMatch
				);
			})
			.filter(entity -> cls.equals(OWLEntity.class)
				|| (cls.equals(OWLClass.class) && entity.isOWLClass())
				|| (cls.equals(OWLIndividual.class) && entity.isOWLNamedIndividual())
			).map(this::getEntity)
			.collect(Collectors.toList());
	}
	
	
	/**
	 * Returns a multidimensional Array of class labels/names.
	 * @return ArrayList containing the taxonomy of this ontology.
	 */
	public TaxonomyNode getTaxonomy() {
		OWLReasoner reasoner = OwlApiUtils.getHermiTReasoner(getRootOntology());
		OWLClass topClass = reasoner.getTopClassNode().iterator().next();
		
		return getTaxonomyForOWLClass(topClass, reasoner);
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
	public List<String> getImportedOntologyIds() {
		return getRootOntology().getImports().parallelStream()
			.map(o -> o.getOntologyID().toString()).collect(Collectors.toList());
	}
	
	
	/**
	 * Returns shortforms and iris for each loaded ontology.
	 * @return HashMap with key: shortform and value: iri
	 */
	public Map<String, String> getOntologyIris() {
		getRootOntology();
		return manager.getOntologies().parallelStream()
			.map(o -> o.getOntologyID().getOntologyIRI().get())
			.collect(Collectors.toMap(i -> i.getShortForm(), i -> i.toString()));
	}
	
	public boolean isConsistent() {
		return OwlApiUtils.getHermiTReasoner(getRootOntology()).isConsistent();
	}
	
	private OWLNamedIndividual createNamedIndividual(Individual individual) throws NoSuchAlgorithmException {
		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<OWLAxiom> axioms   = new TreeSet<OWLAxiom>();
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(
			IRI.create(String.valueOf(individual.getProperties().hashCode()))
		);
		
		individual.getTypes().parallelStream().forEach(type -> {
			OWLClass cls = factory.getOWLClass(IRI.create(type));
			axioms.add(factory.getOWLClassAssertionAxiom(cls, namedIndividual));
		});
		
		individual.getProperties().parallelStream().forEach(property -> {
			IRI iri = IRI.create(property.getIri());
			
			for (String value : property.getValues()) {
				if (getRootOntology().containsAnnotationPropertyInSignature(iri))
					axioms.add(factory.getOWLAnnotationAssertionAxiom(
						namedIndividual.getIRI(), factory.getOWLAnnotation(
							factory.getOWLAnnotationProperty(iri),
							OwlApiUtils.getLiteralForValueAndClassName(value, property.getClassName(), manager)
						)
					));
				
				if (getRootOntology().containsDataPropertyInSignature(iri))
					axioms.add(factory.getOWLDataPropertyAssertionAxiom(
						factory.getOWLDataProperty(iri),
						namedIndividual,
						OwlApiUtils.getLiteralForValueAndClassName(value, property.getClassName(), manager)
					));
				
				if (getRootOntology().containsObjectPropertyInSignature(iri))
					axioms.add(factory.getOWLObjectPropertyAssertionAxiom(
							factory.getOWLObjectProperty(iri),
							namedIndividual,
							factory.getOWLNamedIndividual(IRI.create(value))
					));
			}
		});
		
		manager.addAxioms(getRootOntology(), axioms);
		
		return namedIndividual;
	}
	
	/**
	 * Checks if the entity has a property with the provided string as name and the provided value.
	 * If value is null, this function only checks if the entity has the property.
	 * Use exact to specify the string match method.
	 * @param entity an OWLEntity
	 * @param property Property name to search for
	 * @param value Value to search for or null
	 * @param exact if true: exact match else loose
	 * @return Result of the check.
	 */
	private Boolean hasProperty(OWLEntity entity, String property, String value, Boolean exact) {
		return
			entity.isOWLNamedIndividual()
			&& (extractPropertyByNameFromSet(property, getRootOntology().getDataPropertiesInSignature(Imports.INCLUDED), exact)
					.parallelStream().anyMatch(dataProperty -> {
						return valueCollectionContains(EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), dataProperty, getRootOntology()), value);
					})
				|| extractPropertyByNameFromSet(property, getRootOntology().getObjectPropertiesInSignature(Imports.INCLUDED), exact)
		    		.parallelStream().anyMatch(objectProperty -> {
		    			return valueCollectionContains(EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), objectProperty, getRootOntology()), value);
		    		})
		    )
			|| extractPropertyByNameFromSet(property, getRootOntology().getAnnotationPropertiesInSignature(Imports.INCLUDED), exact)
	    		.parallelStream().anyMatch(annotationProperty -> {
	    			return valueCollectionContains(EntitySearcher.getAnnotations(entity, getRootOntology(), annotationProperty), value);
	    		});
	}
	
	
	private TaxonomyNode getTaxonomyForOWLClass(OWLClass cls, OWLReasoner reasoner) {
		TaxonomyNode taxonomy = new TaxonomyNode(
			StringUtils.defaultString(OwlApiUtils.getLabel(cls, getRootOntology()), XMLUtils.getNCNameSuffix(cls.getIRI())),
			cls.getIRI().toString()
		);
		
		reasoner.getSubClasses(cls, true).getFlattened().stream().filter(
			subclass -> !subclass.isBottomEntity()
		).forEach(subclass -> taxonomy.addSubclassNode(getTaxonomyForOWLClass(subclass, reasoner)));
		
		reasoner.getInstances(cls, true).getFlattened().forEach(
			instance ->	taxonomy.addInstance(OwlApiUtils.getLabel(instance, getRootOntology()), instance.getIRI().toString())
		);
		
		return taxonomy;
	}
	
	
	private <T> List<T> extractPropertyByNameFromSet(String name, Set<T> properties, Boolean exact) {
		return properties.parallelStream().filter(property ->
			exact && XMLUtils.getNCNameSuffix(((OWLNamedObject) property).getIRI()).equals(name)
			|| StringUtils.getJaroWinklerDistance(XMLUtils.getNCNameSuffix(((OWLNamedObject) property).getIRI()), name) >= MATCH_THRESHOLD
		).collect(Collectors.toList());
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for a set of entities.
	 * @param entities set of OWLEntitys
	 * @return List of OWLEntityProperties
	 */
 	@SuppressWarnings("unused")
	private List<Entity> getEntities(List<OWLEntity> entities) {
		return entities.parallelStream().map(this::getEntity).collect(Collectors.toList());
	}
	
	
	/**
	 * Returns properties for a single entity.
	 * @param entity entity object
	 * @return properties as OWLEntityProperties
	 */
	private Entity getEntity(OWLEntity entity) {
		Entity properties    = new Entity();
		OWLReasoner reasoner = OwlApiUtils.getHermiTReasoner(getRootOntology());
		
		properties.setProjectId(projectId);
    	properties.setIri(entity.getIRI().toString());
    	properties.setJavaClass(entity.getClass().getName());
    	
    	EntitySearcher.getAnnotationAssertionAxioms(entity, getRootOntology()).parallelStream().forEach(
    		property -> properties.addAnnotationProperty(property.getProperty(), property.getValue())
    	);
    	
    	if (entity.isOWLClass()) {
    		properties.addSuperClassExpressions(reasoner.getSuperClasses(entity.asOWLClass(), true).getFlattened());
    		properties.addSubClassExpressions(reasoner.getSubClasses(entity.asOWLClass(), true).getFlattened());
			properties.addIndividuals(reasoner.getInstances(entity.asOWLClass(), true).getFlattened());
    		properties.addDisjointClasses(reasoner.getDisjointClasses(entity.asOWLClass()).getFlattened());
    		properties.addEquivalentClasses(reasoner.getEquivalentClasses(entity.asOWLClass()).getEntities());
    	}
    	
    	if (entity.isOWLNamedIndividual()) {
	    	EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), getRootOntology()).entries().parallelStream().forEach(
	    		property ->	properties.addDataProperty(property.getKey(), property.getValue())
			);
			
			EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), getRootOntology()).entries().parallelStream().forEach(
				property ->	properties.addObjectProperty(property.getKey(), property.getValue())
			);
			
			properties.addTypes(reasoner.getTypes(entity.asOWLNamedIndividual(), true).getFlattened());
			properties.addSameIndividuals(reasoner.getSameIndividuals(entity.asOWLNamedIndividual()).getEntities());
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
		
		Timer timer = new Timer();
		
        ArrayList<File> documents = new ArrayList<File>(
        	Arrays.asList((new File(importsPath)).listFiles())
        );
        documents.removeIf(d -> d.isHidden() || d.isDirectory());
        
        try {
	        for (int i = 0; !documents.isEmpty() && i <= documents.size(); i++) {
	        	((ArrayList<File>) documents.clone()).forEach(document -> {
	        		try {
	        			manager.loadOntologyFromOntologyDocument(document);
	        			documents.remove(document);
	        		} catch (OWLOntologyCreationException e) {}
	        	});
	        }
	        
	        /** this is very slow for large ontologies. Any improvement possible? **/
			ontology = manager.loadOntologyFromOntologyDocument(new File(rootPath));
        } catch (OWLOntologyCreationException e) {
        	e.printStackTrace();
        }
        
        LOGGER.info(String.format("Parsed project '%s' for the first time. " + timer.getDiffFromStart(), projectId));
		return ontology;
	}
	
	
	/**
	 * Checks if set of values contains a value or value is null.
	 * @param <T> a collection type
	 * @param values value set
	 * @param value value to search for
	 * @return true if valueSet contains value or value is null, else false
	 */
	private <T> boolean valueCollectionContains(Collection<T> values, String value) {
		for (T curValue : values) {
    		if (StringUtils.isEmpty(value)
    			|| curValue.toString().replaceAll("^.*?\"|\"\\^.*$", "").equals(value)
    		) {
    			return true;
    		}
    	}
		return false;
	}
}
