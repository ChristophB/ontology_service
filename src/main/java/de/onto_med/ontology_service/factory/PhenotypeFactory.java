package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.Category;

import java.util.List;

/**
 * Super factory, which provides some intermediate methods.
 * @author Christoph Beger
 */
public abstract class PhenotypeFactory {
	protected static final String DEFAULT_LANG = "en";

	protected org.lha.phenoman.model.phenotype.PhenotypeFactory factory;

	/**
	 * Adds basic information to the provided phenotype based on formData.
	 * Basic information includes only fields, which are available for all types of phenotypes.
	 * If the phenotype is abstract, categories are added too.
	 * @param phenotype An abstract or restricted phenotype.
	 * @param formData Data which was provided via form or JSON post request.
	 */
	protected void setPhenotypeBasicData(Category phenotype, Phenotype formData) {
		addPhenotypeSynonyms(phenotype, formData.getSynonyms(), formData.getSynonymLanguages());
		addPhenotypeDescriptions(phenotype, formData.getDescriptions(), formData.getDescriptionLanguages());
		addPhenotypeRelations(phenotype, formData.getRelations());
	}

	/**
	 * Adds provided labels and languages to the phenotype.
	 * @param phenotype The phenotype, where the labels will be added to.
	 * @param synonyms Synonyms to be added.
	 * @param languages The languages of the synonyms. They must be in the same order as the synonyms.
	 */
	private void addPhenotypeSynonyms(Category phenotype, List<String> synonyms, List<String> languages) {
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
	private void addPhenotypeDescriptions(Category phenotype, List<String> descriptions, List<String> languages) {
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
	private void addPhenotypeRelations(Category phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}
}
