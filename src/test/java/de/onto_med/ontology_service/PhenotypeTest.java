package de.onto_med.ontology_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Response;
import org.junit.Test;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.top_level.Phenotype;
import org.lha.phenoman.model.top_level.TextLang;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class PhenotypeTest extends AbstractTest {

	@SuppressWarnings("serial")
	@Test
	public void testIntegerPhenotypeCreation() throws Exception {
		String id = "Integer_Phenotype";
		Form form = new Form();
		
		form.param("type", "single")
			.param("id", id)
			.param("datatype", "integer")
	    	.param("label[]", "Label EN").param("label-language[]", "en")
	    	.param("label[]", "Label DE").param("label-language[]", "de")
	    	.param("definition[]", "Definition EN").param("definition-language[]", "en")
	    	.param("definition[]", "Definition DE").param("definition-language[]", "de")
	    	.param("relation[]", "IRI 1")
	    	.param("relation[]", "IRI 2")
	    	.param("ucum", "m^2")
	    	.param("range-min[]", "1").param("range-min-operator[]", ">=").param("range-max[]", "5").param("range-max-operator", "<").param("range-label[]", "Range 1")
	    	.param("range-min[]", "5").param("range-min-operator[]", ">").param("range-label[]", "Range 2");
		
		javax.ws.rs.core.Response response
	    	= client.target(url + "/phenotype/create")
	    	.request(MediaType.APPLICATION_JSON_TYPE)
	    	.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
	    
	    assertThat(response.getStatus()).isEqualTo(Response.SC_OK);
	    
	    PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
	    Phenotype phenotype = manager.getPhenotype(id);
	    
	    assertThat(phenotype.getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
	    assertThat(phenotype.getLabels())
	    	.containsAll(new ArrayList<TextLang>() {{ add(new TextLang("Label EN", "en")); add(new TextLang("Label DE", "de")); }});
	    assertThat(phenotype.getDefinitions())
	    	.containsAll(new ArrayList<TextLang>() {{ add(new TextLang("Definition EN", "en")); add(new TextLang("Definition DE", "de")); }});
	    assertThat(phenotype.getRelatedConcepts())
	    	.containsAll(new ArrayList<String>() {{ add("IRI 1"); add("IRI 2"); }});
	    assertThat(phenotype.getUnit()).isEqualTo("m^2");
	    // TODO: test ranges
	}
}
