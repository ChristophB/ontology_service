package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.model.phenotype.top_level.Category;

import java.util.UUID;

/**
 * Factory for creation of phenotype Categories.
 * @author Christoph Beger
 */
public class PhenotypeCategoryFactory extends PhenotypeFactory {

	/**
	 * Creates a Category.
	 * @param data Category data.
	 * @return A Category.
	 */
	public Category createPhenotypeCategory(PhenotypeFormData data) throws NullPointerException{
		if (StringUtils.isBlank(data.getIdentifier()))
			data.setIdentifier(UUID.randomUUID().toString());

		Category category = new Category(data.getIdentifier(), data.getMainTitle());
		data.getTitleObjects().forEach(category::addTitle);
		category.setSuperCategories(data.getSuperCategories());
		setPhenotypeBasicData(category, data);

		return category;
	}
}
