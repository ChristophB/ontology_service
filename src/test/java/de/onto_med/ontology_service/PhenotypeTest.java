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
import org.lha.phenoman.model.phenotype.top_level.Title;
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
		String title = "Category_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			getTitleLanguages().add("de");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description NONE"));
			setDescriptionLanguages(Collections.singletonList("en"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_CATEGORY_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getCategory(title);

		Category expected = new Category(new Title(title, "de"));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description NONE", "en"); // language defaults to "en"
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
	public void testUpdateAbstractPhenotype() throws Exception {
		// TODO: implement test for update of abstract phenotype
	}

	@Test
	public void testUpdateRestrictedPhenotype() throws Exception {
		// TODO: implement test for update of restricted phenotype
	}

	@Test
	public void testGetTaxonomyNode() throws Exception {
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(
			RULE.getConfiguration().getPhenotypePath().replace("%id%", "2"), false
		);
		assertThat(manager.getPhenotypeCategoryTree(true).getCategory()).isNotNull();
	}



	/*******************************
	 * Tests for abstract phenotypes
	 *******************************/

	private void testAbstractIntegerPhenotypeCreation() throws Exception {
		String title = "Abstract_Integer_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
	    Category actual = manager.getPhenotype(title);

	    AbstractSinglePhenotype expected = new AbstractSinglePhenotype(new Title(title), OWL2Datatype.XSD_INTEGER, "Category_1");
		expected.setUnit("m^2");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractDoublePhenotypeCreation() throws Exception {
		String title = "Abstract_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(new Title(title), OWL2Datatype.XSD_DOUBLE, "Category_1");
		expected.setUnit("kg");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractStringPhenotypeCreation() throws Exception {
		String title = "Abstract_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("string");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(new Title(title), OWL2Datatype.XSD_STRING, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractDatePhenotypeCreation() throws Exception {
		String title = "Abstract_Date_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("date");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(new Title(title), OWL2Datatype.XSD_DATE_TIME, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DATE_TIME);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractBooleanPhenotypeCreation() throws Exception {
		String title = "Abstract_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(new Title(title), OWL2Datatype.XSD_BOOLEAN, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCompositeBooleanPhenotypeCreation() throws Exception {
		String title = "Abstract_Composite_Boolean_Phenotype_1";


		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("composite-boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setCategories("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_ABSTRACT_PHENOTYPE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractBooleanPhenotype expected = new AbstractBooleanPhenotype(new Title(title), "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCalculationPhenotypeCreation() throws Exception {
		String title = "Abstract_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("calculation");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		AbstractCalculationPhenotype expected = manager.getPhenotypeFactory().createAbstractCalculationPhenotype(
			new Title(title), "Abstract_Integer_Phenotype_1", "Category_1"
		);
		expected.setUnit("cm");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
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
		String title = "Restricted_Integer_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_Integer_Phenotype_1",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_EXCLUSIVE, OWLFacet.MAX_INCLUSIVE }, new Integer[] { 5, 10 }));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedDoublePhenotypeCreation() throws Exception {
		String title = "Restricted_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("numeric");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_Double_Phenotype_1",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Double[] { 5.3, 10.7 }));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedStringPhenotypeCreation() throws Exception {
		String title = "Restricted_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("string");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_String_Phenotype_1",
			new PhenotypeRange("a", "b"));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedDatePhenotypeCreation() throws Exception {
		String title = "Restricted_Date_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("date");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.MARCH, 2, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date min = calendar.getTime();
		calendar.set(2017, Calendar.OCTOBER,15, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date max = calendar.getTime();

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_Date_Phenotype_1",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Date[] { min, max }));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_LONG);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedBooleanPhenotypeCreation() throws Exception {
		String title = "Restricted_Boolean_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_Boolean_Phenotype_1", new PhenotypeRange(true));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCompositeBooleanPhenotypeCreation() throws Exception {
		String title = "Restricted_Composite_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("composite-boolean");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedBooleanPhenotype expected = manager.getPhenotypeFactory().createRestrictedBooleanPhenotype(
			title, "Abstract_Composite_Boolean_Phenotype_1", "Restricted_Integer_Phenotype_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");
		expected.setScore(15.4);

		assertThat(actual.isRestrictedBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCalculationPhenotypeCreation() throws Exception {
		String title = "Restricted_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			getTitles().add(title);
			setDatatype("calculation");
			setLabels(Arrays.asList("Label EN", "Label DE"));
			setLabelLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
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
		Category actual = manager.getPhenotype(title);

		RestrictedCalculationPhenotype expected = manager.getPhenotypeFactory().createRestrictedCalculationPhenotype(
			title, "Abstract_Calculation_Phenotype_1",
			new PhenotypeRange(new OWLFacet[] { OWLFacet.MIN_INCLUSIVE, OWLFacet.MAX_EXCLUSIVE }, new Double[] { 5.3, 10.7 }));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedCalculationPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

}
