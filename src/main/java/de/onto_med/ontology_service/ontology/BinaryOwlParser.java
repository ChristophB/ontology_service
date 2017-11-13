package de.onto_med.ontology_service.ontology;

import de.onto_med.ontology_service.api.TaxonomyNode;
import de.onto_med.ontology_service.api.Timer;
import de.onto_med.ontology_service.data_model.Entity;
import de.onto_med.ontology_service.data_model.Individual;
import de.onto_med.owlapi_utils.binaryowl.BinaryOwlUtils;
import de.onto_med.owlapi_utils.owlapi.OwlApiUtils;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instances of this class are parsers for binary formated ontologies.
 * @author Christoph Beger
 */
public class BinaryOwlParser extends OntologyParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryOwlParser.class);
	private static final Double MATCH_THRESHOLD = 0.8;
	
	private String importsPath;
	private String rootPath;
	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private String projectId;

	/**
	 * Constructor
	 * @param projectId id of the project
	 * @param dataPath path to WebProtégé data folder
	 */
	public BinaryOwlParser(String projectId, String dataPath) {
		super(dataPath);
		this.projectId = projectId;
		String projectPath = dataPath + "/data-store/project-data/" + projectId;
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
	public IRI getProjectIri() {
		return getRootOntology().getOntologyID().getOntologyIRI().orElse(null);
	}
	
	public long countEntities(Class<?> cls) {
		return OwlApiUtils.countEntities(cls, getRootOntology());
	}
	
	/**
	 * Classifies an individual by adding it to the ontology and running a reasoner.
	 * @param individual the individual which will be classifiy
	 * @return list of reasoned classes
	 */
	public List<String> classifyIndividual(Individual individual) {
		return OwlApiUtils.getHermiTReasoner(getRootOntology())
			.getTypes(createNamedIndividual(individual), true).entities().parallel()
			.map(e -> e.getIRI().toString())
			.collect(Collectors.toList());
	}
	
	/**
	 * Searches for entities which match the class expression.
	 * @param classExpression class expression as string
	 * @return List of entities 
	 */
	public List<Entity> getEntityPropertiesByClassExpression(String classExpression) {
		OWLClassExpression ce = OwlApiUtils.convertStringToClassExpression(classExpression, getRootOntology());
		
		return OwlApiUtils.getHermiTReasoner(getRootOntology())
			.getInstances(ce, false).entities().parallel()
			.map(this::getEntity).collect(Collectors.toList());
	}

	public List<Entity> getEntityPropertiesByIri(String iri) {
		return getEntityProperties(iri, null, null, null, true, false);
	}
	
	/**
	 * Search for OWLNamedIndividuals by name.
	 */
	public List<Entity> annotate(String name, Boolean exact) {
		return getEntityProperties(null, name, null, null, exact, false);
	}
	
	/**
	 * Search for OWLEntities by name.
	 */
	public List<Entity> getEntityProperties(String name, Boolean exact) {
		return getEntityProperties(null, name, null, null, exact, false);
	}
	
	/**
	 * Search for OWLEntities without specified IRI.
	 */
	public List<Entity> getEntityProperties(String name, String property, String value, Boolean exact, Boolean and) {
		return getEntityProperties(null, name, property, value, exact, and);
	}
	
	/**
	 * Search for OWLEntities.
	 */
	private List<Entity> getEntityProperties(String iri, String name, String property, String value, Boolean exact, Boolean and) {
		try {
			return getEntityProperties(iri, name, property, value, exact, and, OWLEntity.class);
		} catch (Exception e) { throw new WebApplicationException(); }
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
	 * @throws NoSuchAlgorithmException If the provided class is not one of OWLEntity, OWLClass, OWLNamedIndividual.
	 */
	public List<Entity> getEntityProperties(
		String iri, String name, String property, String value, Boolean exact, Boolean and, Class<?> cls
	) throws NoSuchAlgorithmException {
		if (!cls.equals(OWLEntity.class) && !cls.equals(OWLClass.class) && !cls.equals(OWLNamedIndividual.class))
			throw new NoSuchAlgorithmException("Error: class " + cls.getName() + " is not supported by this method.");
		
		if (StringUtils.isBlank(iri) && StringUtils.isBlank(name) && StringUtils.isBlank(property))
			return null;
		
		return getRootOntology().signature(Imports.INCLUDED).parallel()
			.filter(entity -> {
				Boolean iriMatch      = false;
				Boolean nameMatch     = false;
				Boolean propertyMatch = false;
				
				if (StringUtils.isNotBlank(iri)) {
					if (exact && iri.equals(entity.getIRI().getIRIString()))
						iriMatch = true;
					else if (!exact && StringUtils.getJaroWinklerDistance(iri, entity.getIRI()) >= MATCH_THRESHOLD)
						iriMatch = true;
				}
				
				if (StringUtils.isNotBlank(name)) {
					if (exact && iri.equals(entity.getIRI().getIRIString()))
						nameMatch = true;
					else if (!exact && (
						StringUtils.getJaroWinklerDistance(
							name, StringUtils.defaultString(OwlApiUtils.getLabel(entity, getRootOntology()), "")
						) >= MATCH_THRESHOLD
						|| StringUtils.getJaroWinklerDistance(
							name, StringUtils.defaultString(XMLUtils.getNCNameSuffix(entity.getIRI()), "")
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
	public String getFullRdfDocument() {
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
		return getRootOntology().imports().parallel()
			.map(o -> o.getOntologyID().toString()).collect(Collectors.toList());
	}
	
	
	/**
	 * Returns shortforms and iris for each loaded ontology.
	 * @return HashMap with key: shortform and value: iri
	 */
	public Map<String, String> getOntologyIris() {
		getRootOntology();
		return manager.ontologies().parallel()
			.map(o -> o.getOntologyID().getOntologyIRI().orElse(null))
			.collect(Collectors.toMap(IRI::getShortForm, IRI::toString));
	}
	
	public boolean isConsistent() {
		return OwlApiUtils.getHermiTReasoner(getRootOntology()).isConsistent();
	}
	
	public OWLOntology getOntology() {
		return getRootOntology();
	}
	
	private OWLNamedIndividual createNamedIndividual(Individual individual) {
		OWLDataFactory factory = manager.getOWLDataFactory();
		Set<OWLAxiom> axioms   = new TreeSet<>();
		OWLNamedIndividual namedIndividual = factory.getOWLNamedIndividual(
			IRI.create(String.valueOf(individual.getProperties().hashCode()))
		);
		
		individual.getTypes().parallelStream().forEach(type -> {
			OWLClass cls = factory.getOWLClass(IRI.create(type));
			axioms.add(factory.getOWLClassAssertionAxiom(cls, namedIndividual));
		});
		
		individual.getProperties().parallelStream().forEach(property -> {
			IRI iri = IRI.create(property.getName());
			String value = property.getValue();

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
		});
		
		manager.addAxioms(getRootOntology(), axioms.stream());
		
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
			&& (extractPropertyByNameFromStream(property, getRootOntology().dataPropertiesInSignature(Imports.INCLUDED), exact)
					.parallelStream().anyMatch(dataProperty -> valueStreamContains(EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), dataProperty, getRootOntology()), value))
				|| extractPropertyByNameFromStream(property, getRootOntology().objectPropertiesInSignature(Imports.INCLUDED), exact)
		    		.parallelStream().anyMatch(objectProperty -> valueStreamContains(EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), objectProperty, getRootOntology()), value))
		    )
			|| extractPropertyByNameFromStream(property, getRootOntology().annotationPropertiesInSignature(Imports.INCLUDED), exact)
	    		.parallelStream().anyMatch(annotationProperty -> valueStreamContains(EntitySearcher.getAnnotations(entity, getRootOntology(), annotationProperty), value));
	}
	
	
	private TaxonomyNode getTaxonomyForOWLClass(OWLClass cls, OWLReasoner reasoner) {
		TaxonomyNode taxonomy = new TaxonomyNode(
			StringUtils.defaultString(OwlApiUtils.getLabel(cls, getRootOntology()), XMLUtils.getNCNameSuffix(cls.getIRI())),
			cls.getIRI().toString()
		);
		
		reasoner.getSubClasses(cls, true).entities().filter(
			subclass -> !subclass.isBottomEntity()
		).forEach(subclass -> taxonomy.addSubclassNode(getTaxonomyForOWLClass(subclass, reasoner)));
		
		reasoner.getInstances(cls, true).entities().forEach(
			instance ->	taxonomy.addInstance(OwlApiUtils.getLabel(instance, getRootOntology()), instance.getIRI().toString())
		);
		
		return taxonomy;
	}
	
	private <T> List<T> extractPropertyByNameFromStream(String name, Stream<T> properties, Boolean exact) {
		return properties.parallel().filter(property -> {
				String iri = XMLUtils.getNCNameSuffix(((OWLNamedObject) property).getIRI());
				return !StringUtils.isBlank(iri) && (exact && iri.equals(name) || StringUtils.getJaroWinklerDistance(iri, name) >= MATCH_THRESHOLD);
			}
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
    	
    	EntitySearcher.getAnnotationAssertionAxioms(entity, getRootOntology()).parallel().forEach(
    		property -> properties.addAnnotationProperty(property.getProperty(), property.getValue())
    	);
    	
    	if (entity.isOWLClass()) {
    		properties.addSuperClassExpressions(reasoner.getSuperClasses(entity.asOWLClass(), true).entities());
    		properties.addSubClassExpressions(reasoner.getSubClasses(entity.asOWLClass(), true).entities());
			properties.addIndividuals(reasoner.getInstances(entity.asOWLClass(), true).entities());
    		properties.addDisjointClasses(reasoner.getDisjointClasses(entity.asOWLClass()).entities());
    		properties.addEquivalentClasses(reasoner.getEquivalentClasses(entity.asOWLClass()).entities());
    	}
    	
    	if (entity.isOWLNamedIndividual()) {
	    	EntitySearcher.getDataPropertyValues(entity.asOWLNamedIndividual(), getRootOntology()).entries().parallelStream().forEach(
	    		property ->	properties.addDataProperty(property.getKey(), property.getValue())
			);
			
			EntitySearcher.getObjectPropertyValues(entity.asOWLNamedIndividual(), getRootOntology()).entries().parallelStream().forEach(
				property ->	properties.addObjectProperty(property.getKey(), property.getValue())
			);
			
			properties.addTypes(reasoner.getTypes(entity.asOWLNamedIndividual(), true).entities());
			properties.addSameIndividuals(reasoner.getSameIndividuals(entity.asOWLNamedIndividual()).entities());
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
		
        ArrayList<File> documents = new ArrayList<>();
        File[] imports = (new File(importsPath)).listFiles();
        if (imports != null) documents.addAll(Arrays.asList(imports));
        documents.removeIf(d -> d.isHidden() || d.isDirectory());
        
        try {
	        for (int i = 0; !documents.isEmpty() && i <= documents.size(); i++) {
	        	((ArrayList<File>) documents.clone()).forEach(document -> {
	        		try {
	        			manager.loadOntologyFromOntologyDocument(document);
	        			documents.remove(document);
	        		} catch (OWLOntologyCreationException ignored) {}
	        	});
	        }

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
	@SuppressWarnings("unused")
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
	
	/**
	 * Checks if set of values contains a value or value is null.
	 * @param <T> a stream type
	 * @param values value set
	 * @param value value to search for
	 * @return true if valueSet contains value or value is null, else false
	 */
	private <T> boolean valueStreamContains(Stream<T> values, String value) {
		for (T curValue : values.collect(Collectors.toList())) {
    		if (StringUtils.isEmpty(value)
    			|| curValue.toString().replaceAll("^.*?\"|\"\\^.*$", "").equals(value)
    		) {
    			return true;
    		}
    	}
		return false;
	}
}
