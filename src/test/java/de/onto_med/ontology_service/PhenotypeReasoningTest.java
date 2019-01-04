package de.onto_med.ontology_service;

import de.imise.onto_api.entities.restrictions.data_range.DecimalRangeLimited;
import de.onto_med.ontology_service.data_model.Property;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.lha.phenoman.exception.WrongPhenotypeTypeException;
import org.lha.phenoman.man.PhenotypeManager;
import org.lha.phenoman.model.phenotype.AbstractSingleDecimalPhenotype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public class PhenotypeReasoningTest extends AbstractTest {
	private static final String ID            = String.valueOf(new Date().getTime());
	private static final String ONTOLOGY_PATH = RULE.getConfiguration().getPhenotypePath().replace("%id%", ID);
	private static final String REASON_PATH   = "/phenotype/" + ID + "/reason";

	@BeforeClass
	public static void init() throws WrongPhenotypeTypeException {
		PhenotypeManager manager = new PhenotypeManager(ONTOLOGY_PATH, false);

		AbstractSingleDecimalPhenotype abstractPhenotype = new AbstractSingleDecimalPhenotype("height", "height");
		manager.addAbstractSinglePhenotype(abstractPhenotype);
		manager.addRestrictedSinglePhenotype(abstractPhenotype.createRestrictedPhenotype(
			"height_lt_1m", "height_lt_1m",
			new DecimalRangeLimited().setLimit(OWLFacet.MAX_EXCLUSIVE, "1.0")
		));
		abstractPhenotype = new AbstractSingleDecimalPhenotype("weight", "weight");
		manager.addAbstractSinglePhenotype(abstractPhenotype);
		manager.addRestrictedSinglePhenotype(abstractPhenotype.createRestrictedPhenotype(
			"weight_ge_100kg", "weight_ge_100kg",
			new DecimalRangeLimited().setLimit(OWLFacet.MIN_INCLUSIVE, "100")
		));
		manager.write();
	}

	@Test
	public void testReasoningWithRestrictedPhenotype() {
		List<Property> properties = new ArrayList<Property>() {{
			add(new Property() {{ setName("height_lt_1m"); }});
		}};

		javax.ws.rs.core.Response response
			= client.target(url + REASON_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(properties));

		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.readEntity(new GenericType<List<String>>(){})).contains("height_lt_1m");
	}

	@Test
	public void testReasoningWithSinglePhenotypeWithObservationDate() {
		List<Property> properties = new ArrayList<Property>() {{
			add(new Property() {{ setName("height"); setValue("0.5"); setObservationDate("2000-01-01"); }});
		}};

		javax.ws.rs.core.Response response
			= client.target(url + REASON_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(properties));

		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.readEntity(new GenericType<List<String>>(){})).contains("height_lt_1m");
	}

	@Test @Ignore
	public void testReasoningWithSinglePhenotypeWithoutObservationDate() {
		List<Property> properties = new ArrayList<Property>() {{
			add(new Property() {{ setName("weight"); setValue("120"); }});
		}};

		javax.ws.rs.core.Response response
			= client.target(url + REASON_PATH)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.post(Entity.json(properties));

		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.readEntity(new GenericType<List<String>>(){})).contains("weight_ge_100kg");
	}

	@AfterClass
	public static void cleanUp() throws IOException {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}
}
