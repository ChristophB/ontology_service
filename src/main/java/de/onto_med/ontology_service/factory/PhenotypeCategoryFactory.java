package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.Category;

/**
 * Factory for creation of phenotype Categories.
 * @author Christoph Beger
 */
public abstract class PhenotypeCategoryFactory extends PhenotypeFactory {

	/**
	 * Creates a Category.
	 * @param data Category data.
	 * @return A Category.
	 */
	public static Category createPhenotypeCategory(Phenotype data) {
		Category category;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			category = new Category(data.getTitleEn(), "en");
			if (StringUtils.isNoneBlank(data.getTitleDe())) category.addTitle(data.getTitleDe(), "de");
		} else {
			category = new Category(data.getTitleDe(), "de");
		}

		setPhenotypeBasicData(category, data);

		return category;
	}
}
