package de.onto_med.ontology_service.factory;

import care.smith.phep.phenoman.core.model.function.Functions;
import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import org.apache.commons.lang3.StringUtils;
import care.smith.phep.phenoman.core.man.PhenotypeManager;
import care.smith.phep.phenoman.core.model.function.Function;
import care.smith.phep.phenoman.core.model.phenotype.*;
import care.smith.phep.phenoman.core.model.phenotype.top_level.AbstractPhenotype;

import javax.activation.UnsupportedDataTypeException;
import java.util.UUID;

/**
 * Convenient factory to construct AbstractPhenotypes.
 *
 * @author Christoph Beger
 */
public class AbstractPhenotypeFactory extends PhenotypeFactory {
	protected PhenotypeManager manager;

	/**
	 * Constructor for Abstract Phenotype Factories.
	 * This Factory requires a PhenotypeManager, because formulas are only processable by the manager.
	 * @param manager The PhenotypeManager
	 */
	public AbstractPhenotypeFactory(PhenotypeManager manager) {
		this.manager = manager;
	}

	/**
	 * Creates an AbstractPhenotype depending on the provided phenotype data.
	 *
	 * @param data Phenotype data.
	 * @return An AbstractPhenotype.
	 * @throws UnsupportedDataTypeException If the provided phenotype data contains invalid values.
	 */
	public AbstractPhenotype createAbstractPhenotype(PhenotypeFormData data) throws UnsupportedDataTypeException, NullPointerException {
		String datatype = data.getDatatype();

		if (StringUtils.isBlank(data.getIdentifier()))
			data.setIdentifier(UUID.randomUUID().toString());

		AbstractPhenotype phenotype;
		if ("composite-boolean".equals(datatype)) {
			phenotype = createAbstractBooleanPhenotype(data);
		} else if ("calculation".equals(datatype)) {
			phenotype = createAbstractCalculationPhenotype(data);
		} else if (StringUtils.isNoneBlank(datatype)) {
			phenotype = createAbstractSinglePhenotype(data, datatype);
		} else {
			throw new UnsupportedDataTypeException("Datatype is missing.");
		}

		setPhenotypeBasicData(phenotype, data);

		return phenotype;
	}

	/**
	 * Creates an AbstractSinglePhenotype.
	 *
	 * @param data     Phenotype data.
	 * @param datatype An OWL2Datatype.
	 * @return An AbstractSinglePhenotype.
	 */
	private AbstractSinglePhenotype createAbstractSinglePhenotype(PhenotypeFormData data, String datatype)
		throws NullPointerException, UnsupportedDataTypeException
	{
		AbstractSinglePhenotype phenotype;
		switch (datatype) {
			case "string":
				phenotype = new AbstractSingleStringPhenotype(
					data.getIdentifier(), data.getMainTitle(), data.getSuperCategories());
				break;
			case "numeric":
				Function function = null;
				try { function = Functions.getFunction(data.getAggregateFunction()); } catch (IllegalArgumentException | NullPointerException ignored) { }

				phenotype = new AbstractSingleDecimalPhenotype(
					data.getIdentifier(), data.getMainTitle(), data.getSuperCategories());
				((AbstractSingleDecimalPhenotype) phenotype).setFunction(function);
				break;
			case "date":
				phenotype = new AbstractSingleDatePhenotype(
					data.getIdentifier(), data.getMainTitle(), data.getSuperCategories());
				break;
			case "boolean":
				phenotype = new AbstractSingleBooleanPhenotype(
					data.getIdentifier(), data.getMainTitle(), data.getSuperCategories());
				break;
			default: throw new UnsupportedDataTypeException("Could not determine Datatype.");
		}

		data.getTitleObjects().forEach(phenotype::addTitle);
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.addUnit(data.getUcum());

		return phenotype;
	}

	/**
	 * Creates an AbstractBooleanPhenotype.
	 *
	 * @param data Phenotype data.
	 * @return An AbstractBooleanPhenotype
	 */
	private AbstractBooleanPhenotype createAbstractBooleanPhenotype(PhenotypeFormData data) {
		AbstractBooleanPhenotype phenotype =
			new AbstractBooleanPhenotype(data.getIdentifier(), data.getMainTitle(), data.getSuperCategories());

		data.getTitleObjects().forEach(phenotype::addTitle);
		phenotype.setMainResult(data.getIsMainResult());

		return phenotype;
	}

	/**
	 * Creates an AbstractCalculationPhenotype.
	 *
	 * @param data Phenotype data.
	 * @return An AbstractCalculationPhenotype.
	 */
	private AbstractCalculationPhenotype createAbstractCalculationPhenotype(PhenotypeFormData data) {
		if (StringUtils.isBlank(data.getFormula()))
			throw new NullPointerException("Formula for abstract calculated phenotype is missing.");

		AbstractCalculationPhenotype phenotype;
		if ("numeric".equals(data.getFormulaDatatype())) {
			phenotype = new AbstractCalculationDecimalPhenotype(data.getIdentifier(), data.getMainTitle(), manager.getFormula(data.getFormula()), data.getSuperCategories());
		} else if ("date".equals(data.getFormulaDatatype())) {
			phenotype = new AbstractCalculationDatePhenotype(data.getIdentifier(), data.getMainTitle(), manager.getFormula(data.getFormula()), data.getSuperCategories());
		} else if ("boolean".equals(data.getFormulaDatatype())) {
			phenotype = new AbstractCalculationBooleanPhenotype(data.getIdentifier(), data.getMainTitle(), manager.getFormula(data.getFormula()), data.getSuperCategories());
		} else throw new IllegalArgumentException("Missing formula datatype");

		data.getTitleObjects().forEach(phenotype::addTitle);
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.addUnit(data.getUcum());
		phenotype.setMainResult(data.getIsMainResult());

		return phenotype;
	}
}
