package de.onto_med.ontology_service;

import de.imise.onto_api.entities.restrictions.data_range.DecimalRangeLimited;
import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import org.eclipse.jetty.server.Response;
import org.junit.*;
import org.lha.phenoman.exception.WrongPhenotypeTypeException;
import org.lha.phenoman.man.PhenotypeManager;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.Phenotype;
import org.lha.phenoman.model.phenotype.top_level.Title;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdatePhenotypeTest extends AbstractTest {
	private static final String ID            = String.valueOf(new Date().getTime());
	private static final String ONTOLOGY_PATH = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);
	private static final String UPDATE_PATH   = "/phenotype/" + ID + "/create";

	@AfterClass
	public static void cleanUp() throws IOException {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}

	@Before
	public void createPhenotypes() {
		PhenotypeFormData phenotype = new PhenotypeFormData() {{
			setIsPhenotype(false);
			setIsRestricted(false);
			setIdentifier("Category_1");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		phenotype = new PhenotypeFormData() {{
			setIsPhenotype(false);
			setIsRestricted(false);
			setIdentifier("Category_2");
		}};

		response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		String id = "Double_Phenotype_1";
		phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier("Abstract_" + id);
			getTitles().add("Abstract_" + id);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setUcum("kg");
			setSuperCategory("Category_1");
		}};

		response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier("Restricted_" + id);
			getTitles().add("Restricted_" + id);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_" + id);
			setRangeMin("5.3");
			setRangeMinOperator(">=");
			setRangeMax("10.7");
			setRangeMaxOperator("<");
		}};

		response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);
	}

	@Test
	public void testUpdateCategoriesOfPhenotype() {
		String id = "Abstract_Double_Phenotype_1";
		PhenotypeFormData phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			setIdentifier(id);
			getTitles().add(id);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setUcum("kg");
			setSuperCategory("Category_1;Category_2");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);
		Phenotype        actual  = manager.getPhenotype(id);

		assertThat(actual.asAbstractSinglePhenotype().getCategories()).isEqualTo(phenotype.getSuperCategories());
	}

	@Test
	public void testUpdatePhenotypeWithSameType() {
		String id = "Double_Phenotype_1";

		PhenotypeFormData phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(true);
			setIdentifier("Restricted_" + id);
			getTitles().add("Restricted_" + id);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperPhenotype("Abstract_" + id);
			setRangeMin("8.0");
			setRangeMinOperator(">");
			setRangeMax("12.0");
			setRangeMaxOperator("<=");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);
		Phenotype        actual  = manager.getPhenotype("Restricted_" + id);

		RestrictedSinglePhenotype expected = manager.getPhenotype("Abstract_" + id)
			.asAbstractSinglePhenotype().asAbstractSingleDecimalPhenotype().createRestrictedPhenotype(
				"Restricted_" + id, "Restricted_" + id, new DecimalRangeLimited().setLimit(OWLFacet.MIN_EXCLUSIVE, "8.0").setLimit(OWLFacet.MAX_INCLUSIVE, "12.0" ));
		expected.addDescription("Description EN", "en");
		expected.addDescription("Description DE", "de");
		expected.addTitle(new Title("Restricted_" + id));
		expected.addLabel("Label EN", "en");
		expected.addLabel("Label DE", "de");
		expected.addRelatedConcept("IRI 1");
		expected.addRelatedConcept("IRI 2");

		assertThat(actual.isRestrictedSinglePhenotype()).isTrue();
		assertThat(actual.asRestrictedSinglePhenotype().getDatatype()).isEqualTo(OWL2Datatype.XSD_DECIMAL);
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void testUpdatePhenotypeWithSameTypeByApi() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);
		AbstractSingleDecimalPhenotype abstractPhenotype = new AbstractSingleDecimalPhenotype("Weight", "Weight");

		manager.addAbstractSinglePhenotype(abstractPhenotype);
		manager.addRestrictedSinglePhenotype(abstractPhenotype.createRestrictedPhenotype(
			"High weight", "Weight", new DecimalRangeLimited().setLimit(OWLFacet.MIN_INCLUSIVE, "100.0")));
		manager.write();

		RestrictedSinglePhenotype update = abstractPhenotype.createRestrictedPhenotype(
			"High weight", "Weight", new DecimalRangeLimited().setLimit(OWLFacet.MIN_INCLUSIVE, "110.0")	);
		manager.addRestrictedSinglePhenotype(update);
		manager.write();
		assertThat(manager.getPhenotype(update.getName())).isEqualTo(update);
	}

	@Test
	public void testUpdatePhenotypeWithDifferentType() {
		String title = "Abstract_Double_Phenotype_1";

		PhenotypeFormData phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			getTitles().add(title);
			setDatatype("boolean");
			setSynonyms(Arrays.asList("Label EN", "Label2 DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 3", "IRI 2"));
		}};

		javax.ws.rs.core.Response response
			= client.target(url + UPDATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);
	}

	@Test(expected = WrongPhenotypeTypeException.class)
	public void testUpdatePhenotypeWithDifferentTypeByApi() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);

		AbstractSinglePhenotype phenotype = new AbstractSingleDecimalPhenotype("Weight", "Weight");
		Category                category  = new Category("Anthropometric");
		try {
			manager.addAbstractSinglePhenotype(phenotype);
		} catch (WrongPhenotypeTypeException ignored) { }
		manager.addCategory(category);
		manager.write();

		manager.addAbstractBooleanPhenotype(new AbstractBooleanPhenotype(phenotype.getName(), category.getName()));
		manager.write();
	}

	@Test
	public void testUpdatePhenotypeWithDifferentSingleTypeByApi() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);

		AbstractSinglePhenotype phenotype = new AbstractSingleDecimalPhenotype("Weight", "Weight");
		Category                category  = new Category("Anthropometric");

		try {
			manager.addAbstractSinglePhenotype(phenotype);
		} catch (WrongPhenotypeTypeException ignored) { }
		manager.addCategory(category);
		manager.write();

		manager.addAbstractSinglePhenotype(new AbstractSingleDecimalPhenotype(
			phenotype.getName(), phenotype.getName(), category.getName()));
	}

	@Test
	public void testPhenotypeCreation() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(
			RULE.getConfiguration().getPhenotypePath().replace("%id%", "test0815"), false);

		AbstractSingleDecimalPhenotype abstractPhenotype = new AbstractSingleDecimalPhenotype("abstract", "abstract");
		manager.addAbstractSinglePhenotype(new AbstractSingleDecimalPhenotype("abstract", "abstract"));

		abstractPhenotype.createRestrictedPhenotype(
			"restricted", "abstract", new DecimalRangeLimited().setLimit(OWLFacet.MIN_EXCLUSIVE, "5.0"));
	}
}
