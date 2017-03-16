package de.onto_med.webprotege_rest_api.api;

import java.util.ArrayList;

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
	
	public void addChild(String name, String iri, int individuals) {
		if (children == null) children = new ArrayList<TaxonomyNode>();
		children.add(new TaxonomyNode(name, iri, individuals));
	}
	
	public void addChildNode(TaxonomyNode child) {
		if (children == null) children = new ArrayList<TaxonomyNode>();
		children.add(child);
	}
	
}
