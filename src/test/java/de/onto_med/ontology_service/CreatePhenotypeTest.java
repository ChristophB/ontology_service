package de.onto_med.ontology_service;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.eclipse.jetty.server.Response;
import org.junit.AfterClass;
import org.junit.Before;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreatePhenotypeTest extends AbstractTest {
	private static final String ID = String.valueOf(new Date().getTime());
	private static final String ONTOLOGY_PATH = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);
	private static final String CREATE_PATH = "/phenotype/" + ID + "/create";

	@AfterClass
	public static void cleanUp() throws IOException {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}

	@Before
	public void createCategory() {
		String title = "Category_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(false);
			setIsRestricted(false);
			setIdentifier(title);
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description NONE"));
			setDescriptionLanguages(Collections.singletonList("en"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getCategory(title);

		Category expected = new Category(title, new Title(title, "en"));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description NONE", "en"); // language defaults to "en"
		expected.addRelatedConcept("IRI 2");
		expected.addRelatedConcept("IRI 1");

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void test1IntegerPhenotypeCreation() {
		testAbstractIntegerPhenotypeCreation();
		testRestrictedIntegerPhenotypeCreation();
	}

	@Test
	public void testDoublePhenotypeCreation() {
		testAbstractDoublePhenotypeCreation();
		testRestrictedDoublePhenotypeCreation();
	}

	@Test
	public void testStringPhenotypeCreation() {
		testAbstractStringPhenotypeCreation();
		testRestrictedStringPhenotypeCreation();
	}

	@Test
	public void testDatePhenotypeCreation() {
		testAbstractDatePhenotypeCreation();
		testRestrictedDatePhenotypeCreation();
	}

	@Test
	public void testBooleanPhenotypeCreation() {
		testAbstractBooleanPhenotypeCreation();
		testRestrictedBooleanPhenotypeCreation();
	}

	@Test
	public void testCompositeBooleanPhenotypeCreation() {
		testAbstractCompositeBooleanPhenotypeCreation();
		testRestrictedCompositeBooleanPhenotypeCreation();
	}

	@Test
	public void testCalculationPhenotypeCreation() {
		testAbstractCalculationPhenotypeCreation();
		testRestrictedCalculationPhenotypeCreation();
	}


	/*******************************
	 * Tests for abstract phenotypes
	 *******************************/

	private void testAbstractIntegerPhenotypeCreation() {
		String title = "Abstract_Integer_Phenotype_1";
		String title2 = title + "_title2";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			getTitleLanguages().add("en");
			getTitles().add(title2);
			getTitleLanguages().add("de");
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
			setUcum("m^2");
		}};

		javax.ws.rs.core.Response response
	    	= client.target(url + CREATE_PATH)
	    	.request(MediaType.APPLICATION_JSON_TYPE)
	    	.post(Entity.json(phenotype));

	    assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
	    Category actual = manager.getPhenotype(title);

	    AbstractSinglePhenotype expected = new AbstractSinglePhenotype(title, new Title(title, "en"), OWL2Datatype.XSD_INTEGER, "Category_1");
	    expected.addTitle(new Title(title2, "de"));
		expected.addTitle(new Title(title, "en"));
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

	private void testAbstractDoublePhenotypeCreation() {
		String title = "Abstract_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
			setUcum("kg");
			setIsDecimal(true);
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(title, new Title(title), OWL2Datatype.XSD_DOUBLE, "Category_1");
		expected.setUnit("kg");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractStringPhenotypeCreation() {
		String title = "Abstract_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("string");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(title, new Title(title), OWL2Datatype.XSD_STRING, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractDatePhenotypeCreation() {
		String title = "Abstract_Date_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("date");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(title, new Title(title), OWL2Datatype.XSD_DATE_TIME, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DATE_TIME);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractBooleanPhenotypeCreation() {
		String title = "Abstract_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("boolean");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractSinglePhenotype expected = new AbstractSinglePhenotype(title, new Title(title), OWL2Datatype.XSD_BOOLEAN, "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractSinglePhenotype()).isTrue();
		assertThat(actual.asAbstractSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCompositeBooleanPhenotypeCreation() {
		String title = "Abstract_Composite_Boolean_Phenotype_1";


		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("composite-boolean");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractBooleanPhenotype expected = new AbstractBooleanPhenotype(title, new Title(title), "Category_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isAbstractBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testAbstractCalculationPhenotypeCreation() {
		String title = "Abstract_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("calculation");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
			setUcum("cm");
			setFormula("Abstract_Integer_Phenotype_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		AbstractCalculationPhenotype expected = manager.getPhenotypeFactory().createAbstractCalculationPhenotype(
			title, new Title(title), "Abstract_Integer_Phenotype_1", "Category_1"
		);
		expected.setUnit("cm");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
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

	private void testRestrictedIntegerPhenotypeCreation() {
		String title = "Restricted_Integer_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
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
			= client.target(url + CREATE_PATH)
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
		expected.addTitle(new Title(title, "en"));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_INTEGER);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedDoublePhenotypeCreation() {
		String title = "Restricted_Double_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
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
			= client.target(url + CREATE_PATH)
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
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DOUBLE);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedStringPhenotypeCreation() {
		String title = "Restricted_String_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("string");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_String_Phenotype_1");
			setEnumValues(Arrays.asList("a", "b"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
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
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_STRING);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedDatePhenotypeCreation() {
		String title = "Restricted_Date_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("date");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
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
			= client.target(url + CREATE_PATH)
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
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_LONG);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedBooleanPhenotypeCreation() {
		String title = "Restricted_Boolean_Phenotype_1";
		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("boolean");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Boolean_Phenotype_1");
			setEnumValues(Collections.singletonList("true"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		RestrictedSinglePhenotype expected = manager.getPhenotypeFactory().createRestrictedSinglePhenotype(
			title, "Abstract_Boolean_Phenotype_1", new PhenotypeRange(true));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_BOOLEAN);
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCompositeBooleanPhenotypeCreation() {
		String title = "Restricted_Composite_Boolean_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("composite-boolean");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_Composite_Boolean_Phenotype_1");
			setExpression("Restricted_Integer_Phenotype_1");
			setScore(15.4);
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		Category actual = manager.getPhenotype(title);

		RestrictedBooleanPhenotype expected = manager.getPhenotypeFactory().createRestrictedBooleanPhenotype(
			title, title, "Abstract_Composite_Boolean_Phenotype_1", "Restricted_Integer_Phenotype_1");
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");
		expected.setScore(15.4);

		assertThat(actual.isRestrictedBooleanPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

	private void testRestrictedCalculationPhenotypeCreation() {
		String title = "Restricted_Calculation_Phenotype_1";

		Phenotype phenotype = new Phenotype() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier(title);
			getTitles().add(title);
			setDatatype("calculation");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
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
			= client.target(url + CREATE_PATH)
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
		expected.addTitle(new Title(title));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedCalculationPhenotype()).isTrue();
		assertThat(actual).isEqualTo(expected);
	}

}
