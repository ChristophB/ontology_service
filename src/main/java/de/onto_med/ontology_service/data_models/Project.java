package de.onto_med.ontology_service.data_models;

import java.util.List;

import de.onto_med.ontology_service.manager.ProjectManager;

/**
 * Instances of this class represent projects with all available properties.
 * @author Christoph Beger
 */
public class Project extends CondencedProject {
	private String iri;
	private long countAxioms;
	private long countLogicalAxioms;
	private long countClasses;
	private long countIndividuals;
	private long countDataProperties;
	private long countObjectProperties;
	private long countAnnotationProperties;
	private boolean isConsistent;
	private List<String> importedOntologyIds;
	
	public Project(ProjectManager project) {
		super(project);
		setIri(project.getProjectIri());
		setCountAxioms(project.getCountAxioms());
		setCountLogicalAxioms(project.getCountLogicalAxioms());
		setCountClasses(project.getCountClasses());
		setCountIndividuals(project.getCountIndividuals());
		setCountDataProperties(project.getCountDataProperties());
		setCountObjectProperties(project.getCountObjectProperties());
		setCountAnnotationProperties(project.getCountAnnotationProperties());
		setConsistent(project.getIsConsistent());
		setImportedOntologyIds(project.getImportedOntologyIds());
	}

	public String getIri() {
		return iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}

	public long getCountAxioms() {
		return countAxioms;
	}

	public void setCountAxioms(long countAxioms) {
		this.countAxioms = countAxioms;
	}

	public long getCountLogicalAxioms() {
		return countLogicalAxioms;
	}

	public void setCountLogicalAxioms(long countLogicalAxioms) {
		this.countLogicalAxioms = countLogicalAxioms;
	}

	public long getCountClasses() {
		return countClasses;
	}

	public void setCountClasses(long countClasses) {
		this.countClasses = countClasses;
	}

	public long getCountIndividuals() {
		return countIndividuals;
	}

	public void setCountIndividuals(long countIndividuals) {
		this.countIndividuals = countIndividuals;
	}

	public long getCountDataProperties() {
		return countDataProperties;
	}

	public void setCountDataProperties(long countDataProperties) {
		this.countDataProperties = countDataProperties;
	}

	public long getCountObjectProperties() {
		return countObjectProperties;
	}

	public void setCountObjectProperties(long countObjectProperties) {
		this.countObjectProperties = countObjectProperties;
	}

	public long getCountAnnotationProperties() {
		return countAnnotationProperties;
	}

	public void setCountAnnotationProperties(long countAnnotationProperties) {
		this.countAnnotationProperties = countAnnotationProperties;
	}

	public List<String> getImportedOntologyIds() {
		return importedOntologyIds;
	}

	public void setImportedOntologyIds(List<String> importedOntologyIds) {
		this.importedOntologyIds = importedOntologyIds;
	}

	public boolean isConsistent() {
		return isConsistent;
	}

	public void setConsistent(boolean isConsistent) {
		this.isConsistent = isConsistent;
	}
}
