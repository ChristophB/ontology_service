package de.uni_leipzig.imise.webprotege.rest_api;


import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import de.uni_leipzig.imise.webprotege.rest_api.manager.ProjectManager;
import io.dropwizard.testing.junit.DropwizardAppRule;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

public class IntegrationTest {
    @ClassRule
    public static final DropwizardAppRule<RestApiConfiguration> RULE
    	= new DropwizardAppRule<RestApiConfiguration>(
    		RestApiApplication.class, "config.yml"
    	);

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testProjectsList() throws Exception {
        Object response
        	= client.target("http://localhost:8080/projects")
        	.request(MediaType.APPLICATION_JSON_TYPE)
        	.get(Object.class);
        
        assertThat(response).isExactlyInstanceOf(new ArrayList<ProjectManager>().getClass());
        
        response
        	= client.target("http://localhost:8080/projects")
        	.request(MediaType.TEXT_HTML_TYPE)
        	.get(String.class);
        
        assertThat(response).isExactlyInstanceOf(String.class);
        assertThat((String) response).contains("Project List");
    }
    
    @Test
    public void testDocumentation() throws Exception {
    	String response
    		= client.target("http://localhost:8080")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("Documentation");
    }
    
    @Test
    public void testEntityForm() throws Exception {
    	String response
    		= client.target("http://localhost:8080/entity-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"/entity\" method=\"get\"");
    }
    
    @Test
    public void testReasonForm() throws Exception {
    	String response
    		= client.target("http://localhost:8080/reason-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"/reason\" method=\"get\"");
    }
    
    @Test
    public void testEntitySearch() throws Exception {
    	Object response
    		= client.target("http://localhost:8080/entity")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
                
        assertThat(((javax.ws.rs.core.Response) response).getStatus()).isNotEqualTo(Response.SC_FOUND);
    	
    	response
    		= client.target("http://localhost:8080/entity")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat((String) response).contains("Neither query param 'name' nor 'property' given.");
    }
    
    @Test
    public void testReasoning() throws Exception {
    	Object response
    		= client.target("http://localhost:8080/reason")
           	.request(MediaType.APPLICATION_JSON_TYPE)
           	.get();
            
        assertThat(((javax.ws.rs.core.Response) response).getStatus()).isNotEqualTo(Response.SC_FOUND);
        
        response
        	= client.target("http://localhost:8080/reason")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat((String) response).contains("No class expression given.");
    }
}
