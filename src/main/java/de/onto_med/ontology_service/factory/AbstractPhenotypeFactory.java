package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.AbstractBooleanPhenotype;
import org.lha.phenoman.model.phenotype.AbstractCalculationPhenotype;
import org.lha.phenoman.model.phenotype.AbstractSinglePhenotype;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Title;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.activation.UnsupportedDataTypeException;

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
		if (StringUtils.isBlank(data.getTitleDe()) && StringUtils.isBlank(data.getTitleEn()))
			throw new NullPointerException("Title of abstract phenotype is missing.");
		String datatype = data.getDatatype();

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
	private AbstractSinglePhenotype createAbstractSinglePhenotype(Phenotype data, OWL2Datatype datatype) {
		AbstractSinglePhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = factory.createAbstractSinglePhenotype(
				new Title(data.getTitleEn(), data.getAliasEn(), "en"), datatype, data.getCategories().split(";")
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(new Title(data.getTitleDe(), data.getAliasDe(), "de"));
		} else {
			phenotype = factory.createAbstractSinglePhenotype(
				new Title(data.getTitleDe(), data.getAliasDe(), "de"), datatype, data.getCategories().split(";")
			);
		}
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}

	/**
	 * Creates an AbstractBooleanPhenotype.
	 * @param data Phenotype data.
	 * @return An AbstractBooleanPhenotype
	 */
	private AbstractBooleanPhenotype createAbstractBooleanPhenotype(Phenotype data) {
		AbstractBooleanPhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = factory.createAbstractBooleanPhenotype(
				new Title(data.getTitleEn(), data.getAliasEn(), "en"), data.getCategories().split(";")
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(new Title(data.getTitleDe(), data.getAliasDe(), "de"));
		} else {
			phenotype = factory.createAbstractBooleanPhenotype(
				new Title(data.getTitleDe(), data.getAliasDe(), "de"), data.getCategories().split(";")
			);
		}
		return phenotype;
	}

	/**
	 * Creates an AbstractCalculationPhenotype.
	 * @param data Phenotype data.
	 * @return An AbstractCalculationPhenotype.
	 */
	private AbstractCalculationPhenotype createAbstractCalculationPhenotype(Phenotype data) {
		AbstractCalculationPhenotype phenotype;
		if (StringUtils.isBlank(data.getFormula()))
			throw new NullPointerException("Formula for abstract calculated phenotype is missing.");
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = factory.createAbstractCalculationPhenotype(
				new Title(data.getTitleEn(), data.getAliasEn(), "en"), data.getFormula(), data.getCategories().split(";")
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(new Title(data.getTitleDe(), data.getAliasDe(), "de"));
		} else {
			phenotype = factory.createAbstractCalculationPhenotype(
				new Title(data.getTitleDe(), data.getAliasDe(), "de"), data.getFormula(), data.getCategories().split(";")
			);
		}
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}
}
