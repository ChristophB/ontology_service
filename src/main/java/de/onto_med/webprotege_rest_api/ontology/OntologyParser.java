package de.onto_med.webprotege_rest_api.ontology;

/**
 * Abstract class for ontology parsers.
 * @author Christoph Beger
 */
public abstract class OntologyParser {
	/**
	 * Path to WebProtegés data folder.
	 */
	protected String dataPath;
	
	
	/**
	 * Constructor
	 */
	public OntologyParser(String dataPath) {
		this.dataPath = dataPath;
	}
	
}
