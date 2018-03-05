package de.onto_med.ontology_service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.*;

import io.dropwizard.testing.junit.DropwizardAppRule;

public abstract class AbstractTest {
	@ClassRule
    public static final DropwizardAppRule<OntologyServiceConfiguration> RULE
    	= new DropwizardAppRule<>(
    		OntologyServiceApplication.class, "config_test.yml"
    	);

    protected Client client;
    
    protected final String url = "http://localhost:8080" + RULE.getConfiguration().getRootPath();

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() {
        client.close();
    }
}
