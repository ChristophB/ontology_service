package de.onto_med.ontology_service;

import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import org.eclipse.jetty.server.Response;
import org.junit.After;
import org.junit.Test;
import org.lha.phenoman.exception.WrongPhenotypeTypeException;
import org.lha.phenoman.man.PhenotypeManager;
import org.lha.phenoman.model.phenotype.AbstractCalculationDecimalPhenotype;
import org.lha.phenoman.model.phenotype.AbstractCalculationPhenotype;
import org.lha.phenoman.model.phenotype.AbstractSingleDecimalPhenotype;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.top_level.Phenotype;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DeletePhenotypeTest extends AbstractTest {
	private static final String ID                     = String.valueOf(new Date().getTime());
	private static final String ONTOLOGY_PATH          = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);
	private static final String DELETE_PHENOTYPES_PATH = "/phenotype/" + ID + "/delete-phenotypes";
	private static final String CREATE_PATH            = "/phenotype/" + ID + "/create";


	@Test
	public void testGetDependentPhenotypes() {
		String id = "Double_Phenotype";

		PhenotypeFormData phenotype = new PhenotypeFormData() {{
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
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
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
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);
		List<Phenotype>  list    = manager.getDependentPhenotypes("Abstract_" + id);
		assertThat(list).isNotEmpty();
		assertThat(list.get(0).getName()).isEqualTo(phenotype.getIdentifier());
	}

	@Test
	public void testDeletePhenotype() {
		String id = "Abstract_Integer_Phenotype";

		PhenotypeFormData phenotype = new PhenotypeFormData() {{
			setIsPhenotype(true);
			setIsRestricted(false);
			getTitles().add(id);
			setDatatype("numeric");
			setSynonyms(Arrays.asList("Label EN", "Label DE"));
			setSynonymLanguages(Arrays.asList("en", "de"));
			setDescriptions(Arrays.asList("Description EN", "Description DE"));
			setDescriptionLanguages(Arrays.asList("en", "de"));
			setRelations(Arrays.asList("IRI 1", "IRI 2"));
			setSuperCategory("Category_1");
			setUcum("kg");
		}};

		javax.ws.rs.core.Response response
			= client.target(url + CREATE_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(phenotype));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		response
			= client.target(url + DELETE_PHENOTYPES_PATH)
			.request(MediaType.TEXT_HTML)
			.post(Entity.json(Collections.singletonList(id)));

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);

		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);
		assertThat(manager.getPhenotype(id)).isNull();
	}

	@Test
	public void testDeletePhenotypeByApi() throws WrongPhenotypeTypeException {
		String id = "Phenotype_to_be_deleted";

		PhenotypeManager manager   = new PhenotypeManager(ONTOLOGY_PATH, false);
		AbstractSinglePhenotype  phenotype = new AbstractSingleDecimalPhenotype(id, id);
		manager.addAbstractSinglePhenotype(phenotype);
		manager.write();

		manager.removeEntities(new HashSet<>(Collections.singletonList(id)));
		manager.write();

		assertThat(manager.getPhenotype(id)).isNull();
	}

	@Test
	public void testDeleteDependentInCalculation() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);

		manager.addAbstractSinglePhenotype(new AbstractSingleDecimalPhenotype("Height", "Height"));
		manager.addAbstractSinglePhenotype(new AbstractSingleDecimalPhenotype("Weight", "Height"));
		manager.write();

		manager.addAbstractCalculationPhenotype(new AbstractCalculationDecimalPhenotype("BMI", "BMI", manager.getFormula("Weight / Height ^ 2")));
		manager.write();

		((AbstractCalculationPhenotype) manager.getPhenotype("BMI")).getCalculatedValue();
		assertThat(manager.getDependentPhenotypes("Height")).isNotEmpty();
	}

	@After
	public void cleanUp() {
		javax.ws.rs.core.Response response
			= client.target(url + "/phenotype/" + ID + "/delete")
			.request(MediaType.APPLICATION_JSON)
			.post(null);

		assertThat(response.getStatus()).isEqualTo(Response.SC_OK);
		assertThat(Files.exists(Paths.get(ONTOLOGY_PATH))).isFalse();
	}
}
