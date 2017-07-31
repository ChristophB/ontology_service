package de.onto_med.ontology_service.api;

import java.util.ArrayList;
import java.util.List;

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
	 * List of subclasses.
	 */
	private List<TaxonomyNode> subclasses = new ArrayList<TaxonomyNode>();
	/**
	 * List of instances.
	 */
	private List<TaxonomyNode> instances = new ArrayList<TaxonomyNode>();

	
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
	public int getCountInstances() {
		return instances == null ? 0 : instances.size();
	}
	
	public List<TaxonomyNode> getSubclasses() {
		return subclasses;
	}
	
	public List<TaxonomyNode> getInstances() {
		return instances;
	}
	
	/**
	 * Creates a new Node and adds it as a child node.
	 * @param name			node name
	 * @param iri			iri of the represented ontological class
	 * @param individuals	number of individuals, wich instantiate the class
	 */
	public void addSubclass(String name, String iri) {
		subclasses.add(new TaxonomyNode(name, iri));
	}
	
	/**
	 * Adds a TaxonomyNode as child to this node.
	 * @param child	the TaxonomyNode
	 */
	public void addSubclassNode(TaxonomyNode subclassNode) {
		subclasses.add(subclassNode);
	}
	
	/**
	 * Adds an instance to this node.
	 * @param name displayname of the instance
	 * @param iri IRI of the instance
	 */
	public void addInstance(String name, String iri) {
		instances.add(new TaxonomyNode(name, iri));
	}
	
	/**
	 * Adds a new instance node to this node.
	 * @param instance an instance node
	 */
	public void addInstance(TaxonomyNode instanceNode) {
		instances.add(instanceNode);
	}
	
}
