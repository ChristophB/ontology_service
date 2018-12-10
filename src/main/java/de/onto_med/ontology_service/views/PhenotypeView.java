package de.onto_med.ontology_service.views;

import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import de.onto_med.ontology_service.manager.PhenotypeManager;
import org.lha.phenoman.model.phenotype.top_level.Entity;

import java.util.List;
import java.util.Map;

public class PhenotypeView extends RestApiView {
	private String                  id;
	private Entity                  phenotype;
	private PhenotypeManager        manager;
	private List<PhenotypeFormData> parts;
	private Map<String, Long>       ontologies;

	public PhenotypeView(String template, String rootPath, String id) {
		super(template, rootPath);
		this.id = id;
	}

	public PhenotypeView(String template, String rootPath, Map<String, Long> ontologies) {
		super(template, rootPath);
		this.ontologies = ontologies;
	}

	public String getId() {
		return id;
	}

	public PhenotypeManager getManager() {
		return manager;
	}

	public void setManager(PhenotypeManager manager) {
		this.manager = manager;
	}

	public Entity getPhenotype() {
		return phenotype;
	}

	public void setPhenotype(Entity phenotype) {
		this.phenotype = phenotype;
	}

	public List<PhenotypeFormData> getParts() {
		return parts;
	}

	public void setParts(List<PhenotypeFormData> parts) {
		this.parts = parts;
	}

	public Map<String, Long> getOntologies() {
		return ontologies;
	}
}
