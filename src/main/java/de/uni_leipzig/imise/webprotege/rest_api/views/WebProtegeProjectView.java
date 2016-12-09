package de.uni_leipzig.imise.webprotege.rest_api.views;

import de.uni_leipzig.imise.webprotege.rest_api.ontology.OntologyManager;
import io.dropwizard.views.View;

public class WebProtegeProjectView extends View {
	private final OntologyManager ontologyManager;
	
	public WebProtegeProjectView(OntologyManager ontologyManager) {
		super("WebProtegeProject.ftl");
		this.ontologyManager = ontologyManager;
	}

	public OntologyManager getProject() {
		return ontologyManager;
	}
}
