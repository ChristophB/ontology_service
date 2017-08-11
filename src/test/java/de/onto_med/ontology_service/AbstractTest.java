package de.onto_med.ontology_service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

import de.onto_med.ontology_service.OntologyServiceApplication;
import de.onto_med.ontology_service.OntologyServiceConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;

public abstract class AbstractTest {
	@ClassRule
    public static final DropwizardAppRule<OntologyServiceConfiguration> RULE
    	= new DropwizardAppRule<OntologyServiceConfiguration>(
    		OntologyServiceApplication.class, "config_test.yml"
    	);

    protected Client client;
    
    protected final String url = "http://localhost:8080/ontology-service";
    protected final String adminUrl = "http://localhost:8081/ontology-service";

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
}
