package de.onto_med.ontology_service;


import org.eclipse.jetty.server.Response;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest extends AbstractTest {

    @Test
    public void testProjectsList() {
        Object response
        	= client.target(url + "/projects")
        	.request(MediaType.APPLICATION_JSON_TYPE)
        	.get(Object.class);
        
        assertThat(response).isExactlyInstanceOf(ArrayList.class);
        
        response
        	= client.target(url + "/projects")
        	.request(MediaType.TEXT_HTML_TYPE)
        	.get(String.class);
        
        assertThat(response).isExactlyInstanceOf(String.class);
        assertThat((String) response).contains("Project List");
    }
    
    @Test
    public void testDocumentation() {
    	String response
    		= client.target(url)
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("Documentation");
    }
    
    @Test
    public void testEntityForm() {
    	String response
    		= client.target(url + "/entity-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"" + RULE.getConfiguration().getRootPath() + "/entity\" method=\"get\"");
    }
    
    @Test
    public void testReasonForm() {
    	String response
    		= client.target(url + "/reason-form")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	
    	assertThat(response).isExactlyInstanceOf(String.class);
    	assertThat(response).contains("<form action=\"" + RULE.getConfiguration().getRootPath() + "/reason\" method=\"get\"");
    }
    
    @Test
    public void testEntitySearch() {
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
    public void testReasoning() {
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
    public void testTaxonomy() {
    	String response
    		= client.target(url + "/project/e2906cf8-ae16-4162-989c-66b83291b5cf/taxonomy")
    		.request(MediaType.TEXT_HTML_TYPE)
    		.get(String.class);
    	assertThat(response).contains("Patient [1]", "Normal [1]");
    }
    
//    @Test
//    public void testImports() {
//    	String response
//			= client.target(url + "/project/702fdf23-882e-41cf-9d8d-0f589e7632a0/overview")
//			.request(MediaType.TEXT_HTML_TYPE)
//			.get(String.class);
//    	assertThat(response).contains("http://www.lha.org/duo");
//    }
    
}
