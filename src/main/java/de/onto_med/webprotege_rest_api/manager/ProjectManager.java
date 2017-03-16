package de.onto_med.webprotege_rest_api.manager;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.onto_med.webprotege_rest_api.api.Entity;
import de.onto_med.webprotege_rest_api.api.TaxonomyNode;
import de.onto_med.webprotege_rest_api.ontology.BinaryOwlParser;

/**
 * Instances of this class can be used to query a specific project ontology of WebProtegé.
 * 
 * @author Christoph Beger
 */
public class ProjectManager {
	private String projectId;
	private String name;
	private String description;
	private BinaryOwlParser binaryOwlParser;
	
	
	
	/**
	 * Constructor.
	 * @param project Instance of class Project in WebProtegés metaproject
	 * @param dataPath Absolute path to WebProtegés data folder.
	 * @throws OWLOntologyCreationException 
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
	
	
	/**
	 * Returns a list of imported ontology ids.
	 * @return List of imported ontology ids
	 */
	public ArrayList<String> getImportedOntologyIds() {
		return binaryOwlParser.getImportedOntologyIds();
	}
	
	
	public TaxonomyNode getTaxonomy() {
		return binaryOwlParser.getTaxonomy();
	}
	
	
	/**
	 * Searches for entities which match the class expression.
	 * @param string class expression as string
	 * @return List of entities
	 */
	public ArrayList<Entity> getEntityProperties(String ce) {
		return binaryOwlParser.getEntityProperties(ce);
	}
	
	
	public ArrayList<Entity> getEntityProperties(
		String iri, String name, String property, String value, String match, String operator, String type
	) throws NoSuchAlgorithmException {
		Class<?> cls;
		
		if ("class".equals(type)) {
			cls = OWLClass.class;
		} else if ("individual".equals(type)) {
			cls = OWLIndividual.class;
		} else {
			cls = OWLEntity.class;
		}
		
		return binaryOwlParser.getEntityProperties(
			iri, name, property, value, "exact".equals(match), "and".equals(operator), cls
		);
	}
	

	/**
	 * Returns the full RDF document for this ontology as string.
	 * @return string containing the full RDF document.
	 * @throws OWLOntologyStorageException If ontology could not be transformed into a string.
	 * @throws OWLOntologyCreationException 
	 */
	public Object getFullRDFDocument() {
		return binaryOwlParser.getFullRDFDocument();
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
	
	
	public int getCountAxioms() {
		return binaryOwlParser.countEntities(OWLAxiom.class);
	}
	
	
	public int getCountLogicalAxioms() {
		return binaryOwlParser.countEntities(OWLLogicalAxiom.class);
	}
	
	
	public int getCountClasses() {
		return binaryOwlParser.countEntities(OWLClass.class);
	}
	
	
	public int getCountIndividuals() {
		return binaryOwlParser.countEntities(OWLIndividual.class);
	}
	
	
	public int getCountDataProperties() {
		return binaryOwlParser.countEntities(OWLDataProperty.class);
	}
	
	
	public int getCountObjectProperties() {
		return binaryOwlParser.countEntities(OWLObjectProperty.class);
	}
	
	
	public int getCountAnnotationProperties() {
		return binaryOwlParser.countEntities(OWLAnnotationProperty.class);
	}
	
	
	public String getProjectIri()  {
		return binaryOwlParser.getProjectIri();
	}
	
	
	/**
	 * Returns shortforms and iris for each loaded ontology.
	 * @return HashMap with key: shortform and value: iri
	 * @throws OWLOntologyCreationException 
	 */
	public HashMap<String, String> getOntologyIris() throws OWLOntologyCreationException {
		return binaryOwlParser.getOntologyIris();
	}

	public String getProjectShortForm() {
		return XMLUtils.getNCNameSuffix(this.getProjectIri());
	}

}
