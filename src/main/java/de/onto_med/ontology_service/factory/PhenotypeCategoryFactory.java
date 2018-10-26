package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.top_level.Category;
import java.util.UUID;

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
		if (StringUtils.isBlank(data.getIdentifier()))
			data.setIdentifier(UUID.randomUUID().toString());

		Category category = factory.createCategory(data.getIdentifier(), data.getMainTitle());
		data.getTitleObjects().forEach(category::addTitle);
		setPhenotypeBasicData(category, data);

		return category;
	}
}
