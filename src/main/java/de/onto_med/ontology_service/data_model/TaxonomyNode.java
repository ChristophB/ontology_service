package de.onto_med.ontology_service.data_model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class corresponds to a node in a taxonomy.
 *
 * @author Christoph Beger
 */
public class TaxonomyNode {
	private String text;
	private int instanceCount = 0;

	public String icon;
	public State              state    = new State();
	public List<TaxonomyNode> children = new ArrayList<>();
	public AttributeList      a_attr   = new AttributeList();

	public TaxonomyNode(String iri, String text, String title) {
		this(iri, text);
		a_attr.title = title;
	}

	public TaxonomyNode(String id, String text) {
		a_attr.iri = id;
		this.text = text;
	}

	public String getText() {
		return text + (instanceCount > 0 ? String.format(" [%d]", instanceCount) : "");
	}

	public TaxonomyNode setOpened(Boolean opened) {
		state.opened = opened;
		return this;
	}

	public TaxonomyNode setSelected(Boolean selected) {
		state.selected = selected;
		return this;
	}

	public TaxonomyNode addCategory(TaxonomyNode child) {
		children.add(child);
		return this;
	}

	public TaxonomyNode addInstance(TaxonomyNode instance) {
		instance.icon = "fa fa-leaf text-primary";
		children.add(instance);
		instanceCount++;
		return this;
	}

	public class AttributeList {
		public String type;
		public String iri;
		public String title;
	}

	public class State {
		public Boolean opened   = false;
		public Boolean selected = false;
	}

}
