package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.Title;

/**
 * Factory for creation of phenotype Categories.
 * @author Christoph Beger
 */
public class PhenotypeCategoryFactory extends PhenotypeFactory {

	public PhenotypeCategoryFactory(PhenotypeOntologyManager manager) {
		this.factory = manager.getPhenotypeFactory();
	}
	/**
	 * Creates a Category.
	 * @param data Category data.
	 * @return A Category.
	 */
	public Category createPhenotypeCategory(Phenotype data) {
		Category category;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			category = factory.createCategory(new Title(data.getTitleEn(), data.getAliasEn(), "en"));
			if (StringUtils.isNoneBlank(data.getTitleDe())) category.addTitle(new Title(data.getTitleDe(), data.getAliasDe(), "de"));
		} else {
			category = factory.createCategory(new Title(data.getTitleDe(), data.getAliasDe(), "de"));
		}

		setPhenotypeBasicData(category, data);

		return category;
	}
}
