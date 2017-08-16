package de.onto_med.ontology_service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Response;
import org.junit.Test;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.TextLang;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

public class PhenotypeTest extends AbstractTest {
	private final String CREATE_ABSTRACT_PHENOTYPE_PATH = "/phenotype/create-abstract-phenotype";
	private final String CREATE_RESTRICTED_PHENOTYPE_PATH = "/phenotype/create-restricted-phenotype";
	private final String CREATE_CATEGORY_PATH = "/phenotype/create-category";

	@Test
	public void testCategoryCreation() throws Exception {
		String id = "Category_1";
		Form form = new Form();

		form.param("id", id);

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_CATEGORY_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);

		Category actual = manager.getPhenotype(id);
		Category expected = new Category(id);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testAbstractIntegerPhenotypeCreation() throws Exception {
		String id = "Abstract_Integer_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "numeric")
	    	.param("label[]", "Label EN").param("label-language[]", "en")
	    	.param("label[]", "Label DE").param("label-language[]", "de")
	    	.param("definition[]", "Definition EN").param("definition-language[]", "en")
	    	.param("definition[]", "Definition DE").param("definition-language[]", "de")
	    	.param("relation[]", "IRI 1")
	    	.param("relation[]", "IRI 2")
			.param("categories", "Medicine")
	    	.param("ucum", "m^2");
		
		javax.ws.rs.core.Response response
	    	= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
	    	.request(MediaType.APPLICATION_JSON_TYPE)
	    	.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
	    
	    assertThat(response.getStatus()).isEqualTo(Response.SC_OK);
	    
	    PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
	    Category actual = manager.getPhenotype(id);

	    AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_INTEGER, "Medicine");
		expected.setUnit("m^2");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

	    assertThat(actual).isEqualTo(expected);

	    assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
	    assertThat(actual.getLabels())
	    	.containsAll(new ArrayList<TextLang>() {{ add(new TextLang("Label EN", "en")); add(new TextLang("Label DE", "de")); }});
	    assertThat(actual.getDefinitions())
	    	.containsAll(new ArrayList<TextLang>() {{ add(new TextLang("Definition EN", "en")); add(new TextLang("Definition DE", "de")); }});
	    assertThat(actual.getRelatedConcepts())
	    	.containsAll(new ArrayList<String>() {{ add("IRI 1"); add("IRI 2"); }});
	    assertThat(actual.asAbstractSinglePhenotype().getUnit()).isEqualTo("m^2");
	    // TODO: test ranges
	}

	@Test
	public void testRestrictedIntegerPhenotypeCreation() throws Exception {

	}
}
