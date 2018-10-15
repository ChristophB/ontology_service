package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.AbstractBooleanPhenotype;
import org.lha.phenoman.model.phenotype.AbstractCalculationPhenotype;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.activation.UnsupportedDataTypeException;
import java.util.UUID;

/**
 * Convenient factory to construct AbstractPhenotypes.
 * @author Christoph Beger
 */
public class AbstractPhenotypeFactory extends PhenotypeFactory {

	public AbstractPhenotypeFactory(PhenotypeOntologyManager manager) {
		this.factory = manager.getPhenotypeFactory();
	}
	/**
	 * Creates an AbstractPhenotype depending on the provided phenotype data.
	 * @param data Phenotype data.
	 * @return An AbstractPhenotype.
	 * @throws UnsupportedDataTypeException If the provided phenotype data contains invalid values.
	 */
	public AbstractPhenotype createAbstractPhenotype(Phenotype data) throws UnsupportedDataTypeException, NullPointerException {
		String datatype = data.getDatatype();

		if (StringUtils.isBlank(data.getIdentifier()))
			data.setIdentifier(UUID.randomUUID().toString());

		AbstractPhenotype phenotype;
		if ("numeric".equals(datatype)) {
			phenotype = createAbstractSinglePhenotype(
				data, data.getIsDecimal() != null && data.getIsDecimal()
					? OWL2Datatype.XSD_DOUBLE : OWL2Datatype.XSD_INTEGER
			);
		} else if ("string".equals(datatype)) {
			phenotype = createAbstractSinglePhenotype(data, OWL2Datatype.XSD_STRING);
		} else if ("date".equals(datatype)) {
			phenotype = createAbstractSinglePhenotype(data, OWL2Datatype.XSD_DATE_TIME);
		} else if ("boolean".equals(datatype)) {
			phenotype = createAbstractSinglePhenotype(data, OWL2Datatype.XSD_BOOLEAN);
		} else if ("composite-boolean".equals(datatype)) {
			phenotype = createAbstractBooleanPhenotype(data);
		} else if ("calculation".equals(datatype)) {
			phenotype = createAbstractCalculationPhenotype(data);
		} else {
			throw new UnsupportedDataTypeException("Could not determine Datatype.");
		}

		setPhenotypeBasicData(phenotype, data);

		return phenotype;
	}

	/**
	 * Creates an AbstractSinglePhenotype.
	 * @param data Phenotype data.
	 * @param datatype An OWL2Datatype.
	 * @return An AbstractSinglePhenotype.
	 */
	private AbstractSinglePhenotype createAbstractSinglePhenotype(Phenotype data, OWL2Datatype datatype) throws NullPointerException {
		AbstractSinglePhenotype phenotype = data.getCategories() != null
			? factory.createAbstractSinglePhenotype(data.getIdentifier(), datatype, data.getCategories().split(";"))
			: factory.createAbstractSinglePhenotype(data.getIdentifier(), datatype);

		data.getTitleObjects().forEach(phenotype::addTitle);
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}

	/**
	 * Creates an AbstractBooleanPhenotype.
	 * @param data Phenotype data.
	 * @return An AbstractBooleanPhenotype
	 */
	private AbstractBooleanPhenotype createAbstractBooleanPhenotype(Phenotype data) {
		AbstractBooleanPhenotype phenotype = data.getCategories() != null
			? factory.createAbstractBooleanPhenotype(data.getIdentifier(), data.getCategories().split(";"))
			: factory.createAbstractBooleanPhenotype(data.getIdentifier());

		data.getTitleObjects().forEach(phenotype::addTitle);

		return phenotype;
	}

	/**
	 * Creates an AbstractCalculationPhenotype.
	 * @param data Phenotype data.
	 * @return An AbstractCalculationPhenotype.
	 */
	private AbstractCalculationPhenotype createAbstractCalculationPhenotype(Phenotype data) {
		if (StringUtils.isBlank(data.getFormula()))
			throw new NullPointerException("Formula for abstract calculated phenotype is missing.");

		AbstractCalculationPhenotype phenotype = data.getCategories() != null
			? factory.createAbstractCalculationPhenotype(data.getIdentifier(), data.getFormula(), data.getCategories().split(";"))
			: factory.createAbstractCalculationPhenotype(data.getIdentifier(), data.getFormula());

		data.getTitleObjects().forEach(phenotype::addTitle);
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}
}
