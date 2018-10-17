package de.onto_med.ontology_service;

import de.onto_med.ontology_service.factory.PhenotypeFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiTest {

	@Test
	public void LocalNameTest() {
		assertThat(PhenotypeFactory.getLocalName("2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/test#2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/2.16.840")).isEqualTo("2.16.840");
		assertThat(PhenotypeFactory.getLocalName("lha.org/test")).isEqualTo("test");
	}
}
