package de.onto_med.webprotege_rest_api;


import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Response;
import org.junit.Test;

import de.onto_med.webprotege_rest_api.manager.ProjectManager;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

public class IntegrationTest extends AbstractTest {

    @Test
    public void testProjectsList() throws Exception {
        Object response
        	= client.target(url + "/projects")
        	.request(MediaType.APPLICATION_JSON_TYPE)
        	.get(Object.class);
        
        assertThat(response).isExactlyInstanceOf(new ArrayList<ProjectManager>().getClass());
        
        response
        	= client.target(url + "/projects")
        	.request(MediaType.TEXT_HTML_TYPE)
        	.get(String.class);
        
        assertThat(response).isExactlyInstanceOf(String.class);
        assertThat((String) response).contains("Project List");
    }
    
    @Test
    public void testDocumentation() throws Exception {
    	String response
    		= client.target(url)
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("Documentation");
    }
    
    @Test
    public void testEntityForm() throws Exception {
    	String response
    		= client.target(url + "/entity-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"/webprotege-rest-api/entity\" method=\"get\"");
    }
    
    @Test
    public void testReasonForm() throws Exception {
    	String response
    		= client.target(url + "/reason-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"/webprotege-rest-api/reason\" method=\"get\"");
    }
    
    @Test
    public void testEntitySearch() throws Exception {
    	Object response
    		= client.target(url + "/entity")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .get();
                
        assertThat(((javax.ws.rs.core.Response) response).getStatus()).isNotEqualTo(Response.SC_FOUND);
    	
    	response
    		= client.target(url + "/entity")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat((String) response).contains("Neither query param 'name' nor 'iri', nor 'property' given.");
    }
    
    @Test
    public void testReasoning() throws Exception {
    	Object response
    		= client.target(url + "/reason")
           	.request(MediaType.APPLICATION_JSON_TYPE)
           	.get();
            
        assertThat(((javax.ws.rs.core.Response) response).getStatus()).isNotEqualTo(Response.SC_FOUND);
        
        response
        	= client.target(url + "/reason")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat((String) response).contains("No class expression given.");
    }
    
    @Test
    public void testTaxonomy() throws Exception {
    	String response
    		= client.target(url + "/project/e2906cf8-ae16-4162-989c-66b83291b5cf/taxonomy")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	assertThat(response).contains("Patient [1]", "Normal [1]");
    }
    
}
