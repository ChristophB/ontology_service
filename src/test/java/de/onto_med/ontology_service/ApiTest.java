package de.onto_med.ontology_service;

import de.onto_med.ontology_service.factory.PhenotypeFactory;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lha.phenoman.man.PhenotypeManager;
import org.lha.phenoman.model.phenotype.AbstractBooleanPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.Title;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ApiTest extends AbstractTest {
	private static final String ID            = String.valueOf(new Date().getTime());
	private static       String ONTOLOGY_PATH = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);

	@AfterClass
	public static void cleanUp() throws IOException {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}

	@Test
	public void testLocalName() {
		assertThat(PhenotypeFactory.getLocalName("2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/test#2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/test")).isEqualTo("test");
	}

	@Test
	public void testRestrictedCompositeBooleanWithoutScore() throws Exception {
		String title = "abs_bool_phen";
		String title_res = title + "_res";
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, true);
		AbstractBooleanPhenotype abstractPhenotype = new AbstractBooleanPhenotype(title, new Title(title));

		manager.addAbstractBooleanPhenotype(abstractPhenotype);
		manager.addRestrictedBooleanPhenotype(abstractPhenotype.createRestrictedPhenotype(
			title_res, new Title(title_res), manager.getManchesterSyntaxExpression("Thing")));
		manager.getPhenotype(title_res).asRestrictedBooleanPhenotype().getScore();
	}

	@Test
	public void testCategoryMethodPhenotypeCategories() throws Exception {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, true);

		Category superCategory = new Category("super_category");
		Category subCategory   = new Category("sub_category");

		manager.addCategory(superCategory);
		manager.addCategory(subCategory, superCategory.getName());

		Category actual = manager.getCategory(subCategory.getName());

		assertThat(actual).isNotNull();
		try {
			Category.class.getMethod("getCategories");
		} catch (NoSuchMethodException e) {
			fail("Category does not have method getCategories()");
		}
	}
}
