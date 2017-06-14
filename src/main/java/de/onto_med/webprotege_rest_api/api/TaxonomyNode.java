package de.onto_med.webprotege_rest_api.api;

import java.util.ArrayList;

/**
 * This class corresponse to a node in a taxonomy.
 * @author Christoph Beger
 */
public class TaxonomyNode {
	private String name;
	private String iri;
	private int individuals;
	private ArrayList<TaxonomyNode> children;
	
	public TaxonomyNode(String name, String iri, int individuals) {
		this.name = name;
		this.iri = iri;
		this.individuals = individuals;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIri() {
		return iri;
	}
	
	public int getIndividuals() {
		return individuals;
	}
	
	public ArrayList<TaxonomyNode> getChildren() {
		return children;
	}
	
	/**
	 * Creates a new Node and adds it as a child node.
	 * @param name			node name
	 * @param iri			iri of the represented ontological class
	 * @param individuals	number of individuals, wich instantiate the class
	 */
	public void addChild(String name, String iri, int individuals) {
		if (children == null) children = new ArrayList<TaxonomyNode>();
		children.add(new TaxonomyNode(name, iri, individuals));
	}
	
	/**
	 * Adds a TaxonomyNode as child to this node.
	 * @param child	the TaxonomyNode
	 */
	public void addChildNode(TaxonomyNode child) {
		if (children == null) children = new ArrayList<TaxonomyNode>();
		children.add(child);
	}
	
}
