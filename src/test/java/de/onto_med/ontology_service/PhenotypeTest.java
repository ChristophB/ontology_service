package de.onto_med.ontology_service;

import org.eclipse.jetty.server.Response;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.lha.phenoman.man.ManchesterSyntaxExpression;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PhenotypeTest extends AbstractTest {
	private final String CREATE_ABSTRACT_PHENOTYPE_PATH = "/phenotype/create-abstract-phenotype";
	private final String CREATE_RESTRICTED_PHENOTYPE_PATH = "/phenotype/create-restricted-phenotype";

	@After
	public void cleanUp() throws Exception {
		Path path = Paths.get(RULE.getConfiguration().getPhenotypePath());
		if (Files.exists(path)) Files.delete(path);
	}

	@Test
	public void test1CategoryCreation() throws Exception {
		String id = "Category_1";
		Form form = new Form();

		form.param("id", id)
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition NONE").param("definition-language[]", "")
			.param("relation[]", "IRI 1").param("relation[]", "IRI 2");

		javax.ws.rs.core.Response response
			= client.target(url + "/phenotype/create-category")
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getCategory(id);

		Category expected = new Category(id);
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition NONE");
		expected.addRelatedConcept("IRI 2");
		expected.addRelatedConcept("IRI 1");

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void test2IntegerPhenotypeCreation() throws Exception {
		testAbstractIntegerPhenotypeCreation();
		testRestrictedIntegerPhenotypeCreation();
	}

	@Test
	public void testDoublePhenotypeCreation() throws Exception {
		testAbstractDoublePhenotypeCreation();
		testRestrictedDoublePhenotypeCreation();
	}

	@Test
	public void testStringPhenotypeCreation() throws Exception {
		testAbstractStringPhenotypeCreation();
		testRestrictedStringPhenotypeCreation();
	}

	@Test
	public void testDatePhenotypeCreation() throws Exception {
		testAbstractDatePhenotypeCreation();
		testRestrictedDatePhenotypeCreation();
	}

	@Test
	public void testBooleanPhenotypeCreation() throws Exception {
		testAbstractBooleanPhenotypeCreation();
		testRestrictedBooleanPhenotypeCreation();
	}

	@Test
	public void testCalculationPhenotypeCreation() throws Exception {
		testAbstractCalculationPhenotypeCreation();
		testRestrictedCalculationPhenotypeCreation();
	}

	@Test
	public void testManchesterSyntaxGeneration() throws Exception {
		String path = "src/test/resources/ontology-service/cop2.owl";
		String abstractName   = "Abstract_Single_Phenotype";
		String restrictedName = "Restricted_Single_Phenotype";
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(path, true);

		manager.addAbstractSinglePhenotype(new AbstractSinglePhenotype(abstractName, OWL2Datatype.XSD_INTEGER));
		manager.addRestrictedSinglePhenotype(new RestrictedSinglePhenotype(restrictedName, abstractName, new PhenotypeRange(1, 2)));
		manager.write();

		manager = new PhenotypeOntologyManager(path, false);
		org.lha.phenoman.man.ManchesterSyntaxExpression expression = manager.getManchesterSyntaxExpression(restrictedName);
		System.out.println(expression.get());

		if (Files.exists(Paths.get(path))) Files.delete(Paths.get(path));
	}



	/*******************************
	 * Tests for abstract phenotypes
	 *******************************/

	private void testAbstractIntegerPhenotypeCreation() throws Exception {
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
			.param("categories", "Category_1")
	    	.param("ucum", "m^2");
		
		javax.ws.rs.core.Response response
	    	= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
	    	.request(MediaType.APPLICATION_JSON_TYPE)
	    	.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
	    
	    assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
	    Category actual = manager.getPhenotype(id);

	    AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_INTEGER, "Category_1");
		expected.setUnit("m^2");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractDoublePhenotypeCreation() throws Exception {
		String id = "Abstract_Double_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "numeric")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("categories", "Category_1")
			.param("ucum", "kg")
			.param("is-decimal", "true");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_DOUBLE, "Category_1");
		expected.setUnit("kg");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractStringPhenotypeCreation() throws Exception {
		String id = "Abstract_String_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "string")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("categories", "Category_1");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_STRING, "Category_1");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractDatePhenotypeCreation() throws Exception {
		String id = "Abstract_Date_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "date")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("categories", "Category_1");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_DATE_TIME, "Category_1");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DATE_TIME);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractBooleanPhenotypeCreation() throws Exception {
		String id = "Abstract_Boolean_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "boolean")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("categories", "Category_1");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		AbstractBooleanPhenotype expected = new AbstractBooleanPhenotype(id, "Category_1");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCalculationPhenotypeCreation() throws Exception {
		String id = "Abstract_Calculation_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "calculation")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("categories", "Category_1")
			.param("ucum", "cm")
			.param("formula", "Abstract_Integer_Phenotype");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		AbstractCalculationPhenotype expected = new AbstractCalculationPhenotype(
			id, manager.getFormula("Abstract_Integer_Phenotype"), "Category_1"
		);
		expected.setUnit("cm");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractCalculationPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}


	/*********************************
	 * Tests for restricted phenotypes
	 *********************************/

	private void testRestrictedIntegerPhenotypeCreation() throws Exception {
		String id = "Restricted_Integer_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "numeric")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_Integer_Phenotype")
			.param("range-min", "5").param("range-min-operator", ">")
			.param("range-max", "10").param("range-max-operator", "<=");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Integer_Phenotype",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_EXCLUSIVE, OWLFacet.MAX_INCLUSIVE }, new Integer[] { 5, 10 }));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
		assertThat(actual).isEqualTo(expected);

		// TODO: fix this bug
		System.out.println(actual.getName());
		ManchesterSyntaxExpression expression = manager.getManchesterSyntaxExpression(actual.getName());
		System.out.println(expression.get());
	}

	private void testRestrictedDoublePhenotypeCreation() throws Exception {
		String id = "Restricted_Double_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "numeric")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_Double_Phenotype")
			.param("range-min", "5.3").param("range-min-operator", ">=")
			.param("range-max", "10.7").param("range-max-operator", "<");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Double_Phenotype",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Double[] { 5.3, 10.7 }));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedStringPhenotypeCreation() throws Exception {
		String id = "Restricted_String_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "string")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_String_Phenotype")
			.param("enum-value[]", "a")
			.param("enum-value[]", "b");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_String_Phenotype",
			new PhenotypeRange("a", "b"));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedDatePhenotypeCreation() throws Exception {
		String id = "Restricted_Date_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "date")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_Date_Phenotype")
			.param("range-min", "02.03.2015").param("range-min-operator", ">=")
			.param("range-max", "15.10.2017").param("range-max-operator", "<");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.MARCH, 2, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date min = calendar.getTime();
		calendar.set(2017, Calendar.OCTOBER,15, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date max = calendar.getTime();

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Date_Phenotype",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Date[] { min, max }));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_LONG);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedBooleanPhenotypeCreation() throws Exception {
		String id = "Restricted_Boolean_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "boolean")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_Boolean_Phenotype")
			.param("expression", "Restricted_Integer_Phenotype")
			.param("score", "15.4");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		RestrictedBooleanPhenotype expected = new RestrictedBooleanPhenotype(
			id, "Abstract_Boolean_Phenotype",
			manager.getManchesterSyntaxExpression("Restricted_Integer_Phenotype"));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");
		expected.setScore(15.4);

		assertThat(actual.isRestrictedBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCalculationPhenotypeCreation() throws Exception {
		String id = "Restricted_Calculation_Phenotype";
		Form form = new Form();

		form.param("id", id)
			.param("datatype", "calculation")
			.param("label[]", "Label EN").param("label-language[]", "en")
			.param("label[]", "Label DE").param("label-language[]", "de")
			.param("definition[]", "Definition EN").param("definition-language[]", "en")
			.param("definition[]", "Definition DE").param("definition-language[]", "de")
			.param("relation[]", "IRI 1")
			.param("relation[]", "IRI 2")
			.param("super-phenotype", "Abstract_Calculation_Phenotype")
			.param("range-min", "5.3").param("range-min-operator", ">=")
			.param("range-max", "10.7").param("range-max-operator", "<");

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(RULE.getConfiguration().getPhenotypePath(), false);
		Category actual = manager.getPhenotype(id);

		RestrictedCalculationPhenotype expected = new RestrictedCalculationPhenotype(
			id, "Abstract_Calculation_Phenotype",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Double[] { 5.3, 10.7 }));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedCalculationPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

}
