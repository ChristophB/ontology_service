package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
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
	public Category createPhenotypeCategory(Phenotype data) throws NullPointerException{
		Category category = null;
		for (Title title : data.getTitleObjects()) {
			if (category == null) {
				category = factory.createCategory(title);
			} else {
				category.addTitle(title);
			}
		}

		if (category == null) throw new NullPointerException("Could not create category, because title is missing.");

		setPhenotypeBasicData(category, data);

		return category;
	}
}
