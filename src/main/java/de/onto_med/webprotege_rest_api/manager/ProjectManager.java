package de.onto_med.webprotege_rest_api.manager;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import de.onto_med.webprotege_rest_api.api.OWLEntityProperties;
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

	
	/**
	 * Returns a list of OWLEntityProperties for all classes with matching localname.
	 * @param name Localename to search for
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 * @throws OWLOntologyCreationException 
	 */
	public ArrayList<OWLEntityProperties> getClassPropertiesByName(String name, String match) throws OWLOntologyCreationException {	    
	    return binaryOwlParser.getClassPropertiesByName(name, match);
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all individuals with matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties 
	 */
	public ArrayList<OWLEntityProperties> getNamedIndividualPropertiesByName(String name, String match) {		
		return binaryOwlParser.getNamedIndividualPropertiesByName(name, match);
	}
	
	
	/**
	 * Returns a list of OWLEntityProperties for all entities witch matching localname.
	 * @param name Localname to match with
	 * @param match 'exact' or 'loose', defaults to 'loose'
	 * @return List of found OWLEntityProperties
	 */
	public ArrayList<OWLEntityProperties> getEntityPropertiesByName(String name, String match) {
		return binaryOwlParser.getEntityPropertiesByName(name, match);
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
	 * @throws NoSuchAlgorithmException If the specified type is not one of 'entity', 'individual' and 'class', or the project was not found
	 */
	public ArrayList<OWLEntityProperties> searchOntologyEntityByProperty(
		String type, String property, String value, String match
	) throws NoSuchAlgorithmException {
		return binaryOwlParser.searchOntologyEntityByProperty(type, property, value, match);
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

	
	/**
	 * Searches for individuals which match the class expression.
	 * @param string class expression as string
	 * @return List of named individuals
	 */
	public ArrayList<OWLEntityProperties> getIndividualPropertiesByClassExpression(String string) {
		return binaryOwlParser.getIndividualPropertiesByClassExpression(string);
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

}
