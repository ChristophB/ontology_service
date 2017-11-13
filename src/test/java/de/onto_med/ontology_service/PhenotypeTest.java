package de.onto_med.ontology_service;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.eclipse.jetty.server.Response;
import org.junit.After;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PhenotypeTest extends AbstractTest {
	private final String ID = "1";
	private final String CREATE_ABSTRACT_PHENOTYPE_PATH = "/phenotype/" + ID + "/create-abstract-phenotype";
	private final String CREATE_RESTRICTED_PHENOTYPE_PATH = "/phenotype/" + ID + "/create-restricted-phenotype";
	private final String CREATE_CATEGORY_PATH = "/phenotype/" + ID + "/create-category";
	private final String ONTOLOGY_PATH = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);
	
	@After
	public void cleanUp() throws Exception {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}

	@Test
	public void test1CategoryCreation() throws Exception {
		String id = "Category_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition NONE"));
			setDefinitionLanguages(Collections.singletonList("en"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_CATEGORY_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
	public void testCompositeBooleanPhenotypeCreation() throws Exception {
		testAbstractCompositeBooleanPhenotypeCreation();
		testRestrictedCompositeBooleanPhenotypeCreation();
	}

	@Test
	public void testCalculationPhenotypeCreation() throws Exception {
		testAbstractCalculationPhenotypeCreation();
		testRestrictedCalculationPhenotypeCreation();
	}

	@Test
	public void testManchesterSyntaxGeneration() throws Exception {
		String path = "src/test/resources/data/ontology-service/cop2.owl";
		String abstractName = "Abstract_Single_Phenotype_1";

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(path, true);
		manager.addAbstractSinglePhenotype(new AbstractSinglePhenotype(abstractName, OWL2Datatype.XSD_INTEGER));
		manager.getManchesterSyntaxExpression(abstractName);

		if (Files.exists(Paths.get(path))) Files.delete(Paths.get(path));
	}

	@Test
	public void testUpdateAbstractPhenotype() throws Exception {
		// TODO: implement test for update of abstract phenotype
	}

	@Test
	public void testUpdateRestrictedPhenotype() throws Exception {
		// TODO: implement test for update of restricted phenotype
	}



	/*******************************
	 * Tests for abstract phenotypes
	 *******************************/

	private void testAbstractIntegerPhenotypeCreation() throws Exception {
		String id = "Abstract_Integer_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
			setUcum("m^2");
		}};
		
		javax.ws.rs.core.Response response
	    	= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
	    	.request(MediaType.APPLICATION_JSON_TYPE)
	    	.post(Entity.json(phenotype));
	    
	    assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
		String id = "Abstract_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
			setUcum("kg");
			setIsDecimal(true);
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
		String id = "Abstract_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("string");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
		String id = "Abstract_Date_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("date");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
		String id = "Abstract_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(id, OWL2Datatype.XSD_BOOLEAN, "Category_1");
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCompositeBooleanPhenotypeCreation() throws Exception {
		String id = "Abstract_Composite_Boolean_Phenotype_1";


		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("composite-boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
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
		String id = "Abstract_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("calculation");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
			setUcum("cm");
			setFormula("Abstract_Integer_Phenotype_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		AbstractCalculationPhenotype expected = new AbstractCalculationPhenotype(
			id, manager.getFormula("Abstract_Integer_Phenotype_1"), "Category_1"
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
		String id = "Restricted_Integer_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Integer_Phenotype_1");
			setRangeMin("5");
			setRangeMinOperator(">");
			setRangeMax("10");
			setRangeMaxOperator("<=");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Integer_Phenotype_1",
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
	}

	private void testRestrictedDoublePhenotypeCreation() throws Exception {
		String id = "Restricted_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Double_Phenotype_1");
			setRangeMin("5.3");
			setRangeMinOperator(">=");
			setRangeMax("10.7");
			setRangeMaxOperator("<");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Double_Phenotype_1",
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
		String id = "Restricted_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("string");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_String_Phenotype_1");
			setEnumValues(Arrays.asList("a", "b"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_String_Phenotype_1",
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
		String id = "Restricted_Date_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("date");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Date_Phenotype_1");
			setRangeMin("02.03.2015");
			setRangeMinOperator(">=");
			setRangeMax("15.10.2017");
			setRangeMaxOperator("<");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.MARCH, 2, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date min = calendar.getTime();
		calendar.set(2017, Calendar.OCTOBER,15, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date max = calendar.getTime();

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Date_Phenotype_1",
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
		String id = "Restricted_Boolean_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Boolean_Phenotype_1");
			setEnumValues(Collections.singletonList("true"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedSinglePhenotype expected = new RestrictedSinglePhenotype(
			id, "Abstract_Boolean_Phenotype_1",
			new PhenotypeRange(true));
		expected.addDefinition("Definition EN", "en");
		expected.addDefinition("Definition DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCompositeBooleanPhenotypeCreation() throws Exception {
		String id = "Restricted_Composite_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("composite-boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Composite_Boolean_Phenotype_1");
			setExpression("Restricted_Integer_Phenotype_1");
			setScore(15.4);
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedBooleanPhenotype expected = new RestrictedBooleanPhenotype(
			id, "Abstract_Composite_Boolean_Phenotype_1",
			manager.getManchesterSyntaxExpression("Restricted_Integer_Phenotype_1"));
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
		String id = "Restricted_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setId(id);
			setDatatype("calculation");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDefinitions(Arrays.asList("Definition EN", "Definition DE"));
			setDefinitionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Calculation_Phenotype_1");
			setRangeMin("5.3");
			setRangeMinOperator(">=");
			setRangeMax("10.7");
			setRangeMaxOperator("<");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_RESTRICTED_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(id);

		RestrictedCalculationPhenotype expected = new RestrictedCalculationPhenotype(
			id, "Abstract_Calculation_Phenotype_1",
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
