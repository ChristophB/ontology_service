package de.onto_med.webprotege_rest_api;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

import io.dropwizard.testing.junit.DropwizardAppRule;

public abstract class AbstractTest {
	@ClassRule
    public static final DropwizardAppRule<RestApiConfiguration> RULE
    	= new DropwizardAppRule<RestApiConfiguration>(
    		RestApiApplication.class, "config.yml"
    	);

    protected Client client;
    
    protected final String url = "http://localhost:8080/webprotege-rest-api";
    protected final String adminUrl = "http://localhost:8081/webprotege-rest-api";

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }
}
