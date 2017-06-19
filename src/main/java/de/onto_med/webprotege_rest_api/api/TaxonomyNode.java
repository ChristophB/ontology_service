package de.onto_med.webprotege_rest_api.api;

import java.util.ArrayList;

import org.semanticweb.owlapi.io.XMLUtils;

/**
 * This class corresponse to a node in a taxonomy.
 * @author Christoph Beger
 */
public class TaxonomyNode {
	/**
	 * Human readable name of the represented class.
	 */
	private String name;
	/**
	 * IRI of the class.
	 */
	private String iri;
	/**
	 * Number of instances, default 0.
	 */
	private int individuals = 0;
	/** 
	 * List of subclasses.
	 */
	private ArrayList<TaxonomyNode> subclasses;
	
	/**
	 * Constructs a taxonomy node from a string (name), and class IRI and a number of individuals.
	 * @param name human readable name of the represented class
	 * @param iri classes IRI
	 * @param individuals number of instances
	 */
	public TaxonomyNode(String name, String iri, int individuals) {
		this(name, iri);
		this.individuals = individuals;
	}
	
	/**
	 * Constructs a taxonomy node from iri and name.
	 * @param name name of the class
	 * @param iri iri of the class
	 */
	public TaxonomyNode(String name, String iri) {
		this.name = name;
		this.iri  = iri;
	}
	
	/**
	 * Constructs a taxonomy node from an iri. The suffix of the iri is used as name.
	 * @param iri iri of the class
	 */
	public TaxonomyNode(String iri) {
		this(XMLUtils.getNCNameSuffix(iri), iri);
	}
	
	public String getName() {
		return name;
	}
	
	public String getIri() {
		return iri;
	}
	
	/**
	 * Returns the current classes number of instances.
	 * @return number of instances
	 */
	public int getIndividuals() {
		return individuals;
	}
	
	public ArrayList<TaxonomyNode> getSubclasses() {
		return subclasses;
	}
	
	/**
	 * Creates a new Node and adds it as a child node.
	 * @param name			node name
	 * @param iri			iri of the represented ontological class
	 * @param individuals	number of individuals, wich instantiate the class
	 */
	public void addSubclass(String name, String iri, int individuals) {
		if (subclasses == null) subclasses = new ArrayList<TaxonomyNode>();
		subclasses.add(new TaxonomyNode(name, iri, individuals));
	}
	
	/**
	 * Adds a TaxonomyNode as child to this node.
	 * @param child	the TaxonomyNode
	 */
	public void addSubclassNode(TaxonomyNode subclassNode) {
		if (subclasses == null) subclasses = new ArrayList<TaxonomyNode>();
		subclasses.add(subclassNode);
	}
	
}
