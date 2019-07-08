package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.smith.phenoman.model.phenotype.top_level.Entity;

import java.net.URI;
import java.util.List;

/**
 * Super factory, which provides some intermediate methods.
 * @author Christoph Beger
 */
public abstract class PhenotypeFactory {
	protected static final String DEFAULT_LANG = "en";

	/**
	 * Adds basic information to the provided phenotype based on formData.
	 * Basic information includes only fields, which are available for all types of phenotypes.
	 * If the phenotype is abstract, categories are added too.
	 * @param phenotype An abstract or restricted phenotype.
	 * @param formData Data which was provided via form or JSON post request.
	 */
	protected void setPhenotypeBasicData(Entity phenotype, PhenotypeFormData formData) {
		addPhenotypeSynonyms(phenotype, formData.getSynonyms(), formData.getSynonymLanguages());
		addPhenotypeDescriptions(phenotype, formData.getDescriptions(), formData.getDescriptionLanguages());
		addPhenotypeRelations(phenotype, formData.getRelations());
		addPhenotypeCodes(phenotype, formData.getCodeSystems(), formData.getCodes());
	}

	/**
	 * Returns the local name of given iri. If the iri is already a local name it is returned directly.
	 * @param iri the iri
	 * @return the local name
	 */
	public static String getLocalName(String iri) {
		if (iri.contains("#")) {
			return URI.create(iri).getFragment();
		} else if (iri.contains("/")) {
			return FilenameUtils.getName(URI.create(iri).getPath());
		} else return iri;
	}

	/**
	 * Adds provided labels and languages to the phenotype.
	 * @param phenotype The phenotype, where the labels will be added to.
	 * @param synonyms Synonyms to be added.
	 * @param languages The languages of the synonyms. They must be in the same order as the synonyms.
	 */
	private void addPhenotypeSynonyms(Entity phenotype, List<String> synonyms, List<String> languages) {
		for (int i = 0; i < synonyms.size(); i++) {
			String synonym = synonyms.get(i);
			if (StringUtils.isBlank(synonym)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addLabel(synonym, languages.get(i));
			else phenotype.addLabel(synonym, DEFAULT_LANG);
		}
	}

	/**
	 * Adds provided descriptions to the phenotype.
	 * @param phenotype The phenotype, where the descriptions will be added to.
	 * @param descriptions The descriptions.
	 * @param languages The languages of the descriptions. They must be in the same order as the descriptions.
	 */
	private void addPhenotypeDescriptions(Entity phenotype, List<String> descriptions, List<String> languages) {
		for (int i = 0; i < descriptions.size(); i++) {
			String description = descriptions.get(i);
			if (StringUtils.isBlank(description)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addDescription(description, languages.get(i));
			else phenotype.addDescription(description, DEFAULT_LANG);
		}
	}

	/**
	 * Adds provided relations to the phenotype.
	 * @param phenotype The phenotype, where the relations will be added to.
	 * @param relations The relations.
	 */
	private void addPhenotypeRelations(Entity phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}

	/**
	 * Adds provided codes to the phenotype.
	 * @param phenotype The phenotype, where the codes will be added to.
	 * @param codes The codes.
	 */
	private void addPhenotypeCodes(Entity phenotype, List<String> codeSystems, List<String> codes) {
		for (int i = 0; i < codeSystems.size(); i++) {
			String codeSystem = codeSystems.get(i);
			if (StringUtils.isBlank(codeSystem)) continue;
			if (codes.size() > i && StringUtils.isNoneBlank(codes.get(i)))
				phenotype.addCodeSystemAndCode(codeSystem, codeSystem, codes.get(i), codes.get(i));
		}
	}
}
