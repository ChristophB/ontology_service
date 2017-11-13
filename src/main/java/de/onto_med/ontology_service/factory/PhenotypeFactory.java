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
	/**
	 * Adds basic information to the provided phenotype based on formData.
	 * Basic information includes only fields, which are available for all types of phenotypes.
	 * If the phenotype is abstract, categories are added too.
	 * @param phenotype An abstract or restricted phenotype.
	 * @param formData Data which was provided via form or JSON post request.
	 */
	protected static void setPhenotypeBasicData(Category phenotype, Phenotype formData) {
		addPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		addPhenotypeDescriptions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		addPhenotypeRelations(phenotype, formData.getRelations());
		if (StringUtils.isNoneBlank(formData.getAliasEn())) phenotype.addAlias(formData.getAliasEn(), "en");
		if (StringUtils.isNoneBlank(formData.getAliasDe())) phenotype.addAlias(formData.getAliasDe(), "de");
	}

	/**
	 * Adds provided labels and languages to the phenotype.
	 * @param phenotype The phenotype, where the labels will be added to.
	 * @param labels Labels to be added.
	 * @param languages The languages of the labels. They must be in the same order as the labels.
	 */
	private static void addPhenotypeLabels(Category phenotype, List<String> labels, List<String> languages) {
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			if (StringUtils.isBlank(label)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addLabel(label, languages.get(i));
			else phenotype.addLabel(label);
		}
	}

	/**
	 * Adds provided descriptions to the phenotype.
	 * @param phenotype The phenotype, where the descriptions will be added to.
	 * @param descriptions The descriptions.
	 * @param languages The languages of the descriptions. They must be in the same order as the descriptions.
	 */
	private static void addPhenotypeDescriptions(Category phenotype, List<String> descriptions, List<String> languages) {
		for (int i = 0; i < descriptions.size(); i++) {
			String description = descriptions.get(i);
			if (StringUtils.isBlank(description)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addDescription(description, languages.get(i));
			else phenotype.addDescription(description);
		}
	}

	/**
	 * Adds provided relations to the phenotype.
	 * @param phenotype The phenotype, where the relations will be added to.
	 * @param relations The relations.
	 */
	private static void addPhenotypeRelations(Category phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}
}
