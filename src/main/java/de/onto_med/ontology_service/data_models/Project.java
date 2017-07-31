package de.onto_med.ontology_service.data_models;

import java.util.List;

import de.onto_med.ontology_service.manager.ProjectManager;

/**
 * Instances of this class represent projects with all available properties.
 * @author Christoph Beger
 */
public class Project extends CondencedProject {
	private String iri;
	private int countAxioms;
	private int countLogicalAxioms;
	private int countClasses;
	private int countIndividuals;
	private int countDataProperties;
	private int countObjectProperties;
	private int countAnnotationProperties;
	private boolean isConsistent;
	private List<String> importedOntologyIds;
	
	public Project(ProjectManager project) {
		super(project);
		setIri(project.getProjectIri());
		setCountAxioms(project.getCountAxioms());
		setCountLogicalAxioms(project.getCountLogicalAxioms());
		setCountClasses(project.getCountClasses());
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

	public int getCountAxioms() {
		return countAxioms;
	}

	public void setCountAxioms(int countAxioms) {
		this.countAxioms = countAxioms;
	}

	public int getCountLogicalAxioms() {
		return countLogicalAxioms;
	}

	public void setCountLogicalAxioms(int countLogicalAxioms) {
		this.countLogicalAxioms = countLogicalAxioms;
	}

	public int getCountClasses() {
		return countClasses;
	}

	public void setCountClasses(int countClasses) {
		this.countClasses = countClasses;
	}

	public int getCountIndividuals() {
		return countIndividuals;
	}

	public void setCountIndividuals(int countIndividuals) {
		this.countIndividuals = countIndividuals;
	}

	public int getCountDataProperties() {
		return countDataProperties;
	}

	public void setCountDataProperties(int countDataProperties) {
		this.countDataProperties = countDataProperties;
	}

	public int getCountObjectProperties() {
		return countObjectProperties;
	}

	public void setCountObjectProperties(int countObjectProperties) {
		this.countObjectProperties = countObjectProperties;
	}

	public int getCountAnnotationProperties() {
		return countAnnotationProperties;
	}

	public void setCountAnnotationProperties(int countAnnotationProperties) {
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
