package de.onto_med.ontology_service;

import de.onto_med.ontology_service.data_model.Property;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lha.phenoman.exception.WrongPhenotypeTypeException;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.instance.ComplexPhenotypeInstance;
import org.lha.phenoman.model.instance.SinglePhenotypeInstance;
import org.lha.phenoman.model.phenotype.PhenotypeFactory;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
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
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(ONTOLOGY_PATH, false);
		PhenotypeFactory factory = manager.getPhenotypeFactory();

		manager.addAbstractSinglePhenotype(factory.createAbstractSinglePhenotype("height", "height", OWL2Datatype.XSD_DOUBLE));
		manager.addRestrictedSinglePhenotype(factory.createRestrictedSinglePhenotype(
			"height_lt_1m", "height",
			new PhenotypeRange(new OWLFacet[]{ OWLFacet.MAX_EXCLUSIVE }, new Double[] { 1.0 })
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

	@AfterClass
	public static void cleanUp() throws IOException {
		Path path = Paths.get(ONTOLOGY_PATH);
		if (Files.exists(path)) Files.delete(path);
	}
}
