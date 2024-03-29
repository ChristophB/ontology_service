package de.onto_med.ontology_service.manager;

import de.imise.ontomed.owl2graphml.onto.MainOntology;
import de.onto_med.ontology_service.data_model.TaxonomyNode;
import de.onto_med.ontology_service.data_model.Entity;
import de.onto_med.ontology_service.data_model.Individual;
import de.onto_med.ontology_service.ontology.BinaryOwlParser;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.*;

import javax.ws.rs.WebApplicationException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Instances of this class can be used to query a specific project ontology of WebProtégé.
 * @author Christoph Beger
 */
public class ProjectManager {
	private String projectId;
	private String name;
	private String description;
	private BinaryOwlParser binaryOwlParser;
	
	
	
	/**
	 * Constructor.
	 * @param projectId Instance of class Project in WebProtégé's meta project
	 * @param dataPath 	Absolute path to WebProtégé's data folder.
	 */
	public ProjectManager(String projectId, String dataPath) {
		this.projectId = projectId;
		binaryOwlParser = new BinaryOwlParser(projectId, dataPath);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public BinaryOwlParser getBinaryOwlParser() {
		return binaryOwlParser;
	}
	
	
	/**
	 * Returns a list of imported ontology ids.
	 * @return List of imported ontology ids
	 */
	public List<String> getImportedOntologyIds() {
		return binaryOwlParser.getImportedOntologyIds();
	}
	
	
	/**
	 * Returns the top taxonomy node for this project.
	 * @return Top most taxonomy node
	 */
	public TaxonomyNode getTaxonomy() {
		return binaryOwlParser.getTaxonomy();
	}
	
	
	/**
	 * Searches for entities which match the class expression.
	 * @param ce class expression as string
	 * @return List of entities
	 */
	public List<Entity> getEntityProperties(String ce) {
		return binaryOwlParser.getEntityPropertiesByClassExpression(ce);
	}
	
	public MainOntology getGraphMl(String startClassIri, String taxonomyDirection, int taxonomyDepth) {
        return new MainOntology(binaryOwlParser.getOntology(), startClassIri, taxonomyDirection, taxonomyDepth);
	}
	
	
	/**
	 * Returns a list of entities and their properties for a set of search parameters.
	 * @param iri		IRI
	 * @param name		local name
	 * @param property	name of a property the entity has
	 * @param value		property value
	 * @param match		matching method for strings: exact (default), loose
	 * @param operator	logical operator to connect name and property search: and (default), or
	 * @param type		ontological type to search for: entity (default), class, individual
	 * @return list of entities and their properties
	 */
	public List<Entity> getEntityProperties(
		String iri, String name, String property, String value, String match, String operator, String type
	) {
		Class<?> cls;
		
		if ("class".equals(type)) cls = OWLClass.class;
		else if ("individual".equals(type)) cls = OWLIndividual.class;
		else cls = OWLEntity.class;

		try {
			return binaryOwlParser.getEntityProperties(
				iri, name, property, value, "exact".equals(match), "and".equals(operator), cls
			);
		} catch (NoSuchAlgorithmException e) { throw new WebApplicationException(); }
	}
	

	/**
	 * Returns a list of matching superclasses for an individual.
	 * @param individual Individual object parsed from JSON
	 * @return List of found superclasses
	 */
	public List<String> classifyIndividual(Individual individual) {
		return binaryOwlParser.classifyIndividual(individual);
	}
	
	
	/**
	 * Returns the full RDF document for this ontology as string.
	 * @return string containing the full RDF document.
	 */
	public String getFullRdfDocument() {
		return binaryOwlParser.getFullRdfDocument();
	}
	
	
	public String getProjectId() {
		return projectId;
	}
	
	
	public String getName() {
		return StringUtils.defaultString(name, "");
	}
	
	
	public boolean getIsConsistent() {
		return binaryOwlParser.isConsistent();
	}
	
	public String getDescription() {
		return StringUtils.defaultString(description, "");
	}
	
	
	public long getCountAxioms() {
		return binaryOwlParser.countEntities(OWLAxiom.class);
	}
	
	
	public long getCountLogicalAxioms() {
		return binaryOwlParser.countEntities(OWLLogicalAxiom.class);
	}
	
	
	public long getCountClasses() {
		return binaryOwlParser.countEntities(OWLClass.class);
	}
	
	
	public long getCountIndividuals() {
		return binaryOwlParser.countEntities(OWLIndividual.class);
	}
	
	
	public long getCountDataProperties() {
		return binaryOwlParser.countEntities(OWLDataProperty.class);
	}
	
	
	public long getCountObjectProperties() {
		return binaryOwlParser.countEntities(OWLObjectProperty.class);
	}
	
	
	public long getCountAnnotationProperties() {
		return binaryOwlParser.countEntities(OWLAnnotationProperty.class);
	}
	
	
	public String getProjectIri()  {
		return binaryOwlParser.getProjectIri().getIRIString();
	}
	
	
	/**
	 * Returns short forms and iris for each loaded ontology.
	 * @return HashMap with key: short form and value: iri
	 */
	public Map<String, String> getOntologyIris() {
		return binaryOwlParser.getOntologyIris();
	}

	public String getProjectShortForm() {
		return XMLUtils.getNCNameSuffix(this.getProjectIri());
	}

}
