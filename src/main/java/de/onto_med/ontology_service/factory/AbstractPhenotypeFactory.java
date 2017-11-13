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

/**
 * Convenient factory to construct AbstractPhenotypes.
 * @author Christoph Beger
 */
public abstract class AbstractPhenotypeFactory extends PhenotypeFactory {

	/**
	 * Creates an AbstractPhenotype depending on the provided phenotype data.
	 * @param manager The associated PhenotypeOntologyManager.
	 * @param data Phenotype data.
	 * @return An AbstractPhenotype.
	 * @throws UnsupportedDataTypeException If the provided phenotype data contains invalid values.
	 */
	public static AbstractPhenotype createAbstractPhenotype(PhenotypeOntologyManager manager, Phenotype data) throws UnsupportedDataTypeException {
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
			phenotype = createAbstractCalculationPhenotype(manager, data);
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
	private static AbstractSinglePhenotype createAbstractSinglePhenotype(Phenotype data, OWL2Datatype datatype) {
		AbstractSinglePhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = createAbstractSinglePhenotype(data.getTitleEn(), "en", datatype, data.getCategories());
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = createAbstractSinglePhenotype(data.getTitleDe(), "de", datatype, data.getCategories());
		}
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}

	/**
	 * Creates an AbstractSinglePhenotype.
	 * @param title The title of the phenotype.
	 * @param lang The language of the title, e.g. "en" or "de".
	 * @param datatype The OWL2Datatype of the phenotype.
	 * @param categories Categories of the phenotype, separated by semicolon.
	 * @return An AbstractSinglePhenotype.
	 */
	private static AbstractSinglePhenotype createAbstractSinglePhenotype(String title, String lang, OWL2Datatype datatype, String categories) {
		return StringUtils.isBlank(categories)
			? new AbstractSinglePhenotype(title, lang, datatype)
			: new AbstractSinglePhenotype(title, lang, datatype, categories.split(";"));
	}

	/**
	 * Creates an AbstractBooleanPhenotype.
	 * @param data Phenotype data.
	 * @return An AbstractBooleanPhenotype
	 */
	private static AbstractBooleanPhenotype createAbstractBooleanPhenotype(Phenotype data) {
		AbstractBooleanPhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = createAbstractBooleanPhenotype(data.getTitleEn(), "en", data.getCategories());
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = createAbstractBooleanPhenotype(data.getTitleDe(), "de", data.getCategories());
		}
		return phenotype;
	}

	/**
	 * Creates an AbstractBooleanPhenotype.
	 * @param title The title of the phenotype.
	 * @param lang The language of the title, e.g. "en" or "de".
	 * @param categories The categories of the phenotype, separated by semicolon.
	 * @return An AbstractBooleanPhenotype.
	 */
	private static AbstractBooleanPhenotype createAbstractBooleanPhenotype(String title, String lang, String categories) {
		return StringUtils.isBlank(categories)
			? new AbstractBooleanPhenotype(title, lang)
			: new AbstractBooleanPhenotype(title, lang, categories.split(";"));
	}

	/**
	 * Creates an AbstractCalculationPhenotype.
	 * @param manager The associated PhenotypeOntologyManager.
	 * @param data Phenotype data.
	 * @return An AbstractCalculationPhenotype.
	 */
	private static AbstractCalculationPhenotype createAbstractCalculationPhenotype(PhenotypeOntologyManager manager, Phenotype data) {
		AbstractCalculationPhenotype phenotype;
		if (StringUtils.isBlank(data.getFormula()))
			throw new NullPointerException("Formula for abstract calculated phenotype is missing.");
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = createAbstractCalculationPhenotype(manager, data.getTitleEn(), "en", data.getFormula(), data.getCategories());
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = createAbstractCalculationPhenotype(manager, data.getTitleDe(), "de", data.getFormula(), data.getCategories());
		}
		if (StringUtils.isNoneBlank(data.getUcum())) phenotype.setUnit(data.getUcum());

		return phenotype;
	}

	/**
	 * Creates an AbstractCalculationPhenotype.
	 * @param manager The associated PhenotypeOntologyManager.
	 * @param title The title of the phenotype.
	 * @param lang The language of the title, e.g. "en", "de".
	 * @param formula The formula of the phenotype.
	 * @param categories The categories of the phenotype, separated by semicolon.
	 * @return An AbstractCalculationPhenotype.
	 */
	private static AbstractCalculationPhenotype createAbstractCalculationPhenotype(PhenotypeOntologyManager manager, String title, String lang, String formula, String categories) {
		return StringUtils.isBlank(categories)
			? new AbstractCalculationPhenotype(title, lang, manager.getFormula(formula))
			: new AbstractCalculationPhenotype(title, lang, manager.getFormula(formula), categories.split(";"));
	}
}
