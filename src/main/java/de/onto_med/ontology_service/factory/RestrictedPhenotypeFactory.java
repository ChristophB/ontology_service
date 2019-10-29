package de.onto_med.ontology_service.factory;

import de.imise.onto_api.entities.restrictions.data_range.*;
import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import de.onto_med.ontology_service.util.Parser;
import org.apache.commons.lang3.StringUtils;
import org.smith.phenoman.man.PhenotypeManager;
import org.smith.phenoman.model.phenotype.*;
import org.smith.phenoman.model.phenotype.top_level.Phenotype;
import org.smith.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.activation.UnsupportedDataTypeException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * Convenient factory to construct RestrictedPhenotypes.
 * @author Christoph Beger
 */
public class RestrictedPhenotypeFactory extends PhenotypeFactory {
	public PhenotypeManager manager;

	public RestrictedPhenotypeFactory(PhenotypeManager manager) {
		this.manager = manager;
	}

	/**
	 * Creates a RestrictedPhenotype according to the provided phenotype data.
	 * @param data Phenotype data.
	 * @return A Restricted Phenotype.
	 * @throws UnsupportedDataTypeException If thr provided phenotype data contains invalid data.
	 */
	public RestrictedPhenotype createRestrictedPhenotype(PhenotypeFormData data) throws UnsupportedDataTypeException, NullPointerException, ParseException {
		Phenotype superPhenotype = manager.getPhenotype(data.getSuperPhenotype());

		if (StringUtils.isBlank(data.getIdentifier()))
			data.setIdentifier(UUID.randomUUID().toString());

		RestrictedPhenotype phenotype;
		if (superPhenotype == null) {
			throw new UnsupportedDataTypeException("Super phenotype does not exist");
		} else if (superPhenotype.isAbstractBooleanPhenotype()) {
			phenotype = createRestrictedBooleanPhenotype(data, superPhenotype);
		} else if (superPhenotype.isAbstractCalculationPhenotype()) {
			phenotype = createRestrictedCalculationPhenotype(data, superPhenotype);
		} else if (superPhenotype.isAbstractSinglePhenotype()) {
			phenotype = createRestrictedSinglePhenotype(data, superPhenotype);
		} else {
			throw new UnsupportedDataTypeException("Could not determine datatype of super phenotype.");
		}

		setPhenotypeBasicData(phenotype, data);

		return phenotype;
	}

	/**
	 * Creates a RestrictedSinglePhenotype.
	 * @param data Phenotype data.
	 * @param superPhenotype The super phenotype.
	 * @return A RestrictedSinglePhenotype.
	 */
	private RestrictedSinglePhenotype createRestrictedSinglePhenotype(PhenotypeFormData data, Phenotype superPhenotype) throws UnsupportedDataTypeException, ParseException {
		RestrictedSinglePhenotype phenotype;
		AbstractSinglePhenotype abstractPhenotype = superPhenotype.asAbstractSinglePhenotype();

		if (abstractPhenotype.hasBooleanDatatype()) {
			phenotype = abstractPhenotype.asAbstractSingleBooleanPhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data).asBooleanRange()
			);
		} else if (abstractPhenotype.hasDecimalDatatype()) {
			phenotype = abstractPhenotype.asAbstractSingleDecimalPhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data).asDecimalRange()
			);
		} else if (abstractPhenotype.hasDateDatatype()) {
			phenotype = abstractPhenotype.asAbstractSingleDatePhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data).asDateRange()
			);
		} else if (abstractPhenotype.hasStringDatatype()) {
			phenotype = abstractPhenotype.asAbstractSingleStringPhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data).asStringRange()
			);
		} else {
			throw new UnsupportedDataTypeException(
				"Super phenotype has a not supported data type."
				+ "Maybe the ontology is inconsistent or was created with an old version of PhenoMan?");
		}

		data.getTitleObjects().forEach(phenotype::addTitle);

		return phenotype;
	}

	/**
	 * Creates a restricted calculation phenotype.
	 * @param data Phenotype data.
	 * @param superPhenotype The super phenotype.
	 * @return A RestrictedCalculationPhenotype.
	 */
	private RestrictedCalculationPhenotype createRestrictedCalculationPhenotype(PhenotypeFormData data, org.smith.phenoman.model.phenotype.top_level.Phenotype superPhenotype) throws ParseException {
		AbstractCalculationPhenotype abstractPhenotype = superPhenotype.asAbstractCalculationPhenotype();
		RestrictedCalculationPhenotype phenotype;

		if (abstractPhenotype.hasBooleanDatatype()) {
			phenotype = superPhenotype.asAbstractCalculationPhenotype().asAbstractCalculationBooleanPhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(OWL2Datatype.XSD_DECIMAL, data).asBooleanRange()
			);
		} else if (abstractPhenotype.hasDateDatatype()) {
			phenotype = superPhenotype.asAbstractCalculationPhenotype().asAbstractCalculationDatePhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(OWL2Datatype.XSD_DECIMAL, data).asDateRange()
			);
		} else if (abstractPhenotype.hasDecimalDatatype()) {
			phenotype = superPhenotype.asAbstractCalculationPhenotype().asAbstractCalculationDecimalPhenotype().createRestrictedPhenotype(
				data.getIdentifier(), data.getMainTitle(), createRestrictedPhenotypeRange(OWL2Datatype.XSD_DECIMAL, data).asDecimalRange()
			);
		} else throw new IllegalArgumentException("RestrictedCalculationPhenotype could not be created because its super phenotype has an invalid datatype");

		data.getTitleObjects().forEach(phenotype::addTitle);
		// TODO: What is the replacement for this?!
		// phenotype.setMainResult(data.getIsMainResult());

		return phenotype;
	}

	/**
	 * Creates a restricted boolean phenotype.
	 * @param data Phenotype data.
	 * @param superPhenotype The super phenotype.
	 * @return A RestrictedBooleanPhenotype.
	 */
	private RestrictedBooleanPhenotype createRestrictedBooleanPhenotype(PhenotypeFormData data, org.smith.phenoman.model.phenotype.top_level.Phenotype superPhenotype) throws NullPointerException {
		if (StringUtils.isBlank(data.getExpression()))
			throw new NullPointerException("Boolean expression for restricted boolean phenotype is missing.");

		RestrictedBooleanPhenotype phenotype = superPhenotype.asAbstractBooleanPhenotype().createRestrictedPhenotype(
			data.getIdentifier(), data.getMainTitle(), manager.getManchesterSyntaxExpression(data.getExpression())
		);

		data.getTitleObjects().forEach(phenotype::addTitle);
		if (data.getScore() != null) phenotype.asRestrictedBooleanPhenotype().setScore(data.getScore());
		phenotype.setMainResult(data.getIsMainResult());

		return phenotype;
	}

	/**
	 * Creates a range restriction for the given datatype and phenotype data
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param data The phenotype data.
	 * @return A value range for a restricted phenotype.
	 * @throws NullPointerException If no range could be generated.
	 */
	private DataRange createRestrictedPhenotypeRange(OWL2Datatype datatype, PhenotypeFormData data) throws NullPointerException, ParseException {
		DataRange range = Optional.ofNullable(createRestrictedPhenotypeRange(
			datatype,
			data.getRangeMin(), data.getRangeMinOperator(),
			data.getRangeMax(), data.getRangeMaxOperator()
		)).orElse(createRestrictedPhenotypeRange(datatype, data.getEnumValues()));

		if (range == null) throw new NullPointerException("No Restriction for restricted phenotype provided.");

		return range;
	}

	/**
	 * Creates a single restricted phenotype by range for the given phenotype, if min and minOperator or max and maxOperator are valid.
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param min Minimum of the range restriction.
	 * @param max Maximum of the range restriction.
	 * @param minOperator Operator for bottom border.
	 * @param maxOperator Operator for top border.
	 */
	private DataRange createRestrictedPhenotypeRange(OWL2Datatype datatype, String min, String minOperator, String max, String maxOperator) throws ParseException {
		if ((StringUtils.isBlank(min) || StringUtils.isBlank(minOperator)) && (StringUtils.isBlank(max) || StringUtils.isBlank(maxOperator))) {
			return null;
		} else if (OWL2Datatype.XSD_DECIMAL.equals(datatype)) {
			DecimalRangeLimited range = new DecimalRangeLimited();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				range.setLimit(Objects.requireNonNull(OWLFacet.getFacetBySymbolicName(minOperator)), min);
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				range.setLimit(Objects.requireNonNull(OWLFacet.getFacetBySymbolicName(maxOperator)), max);
			}
			return range;
		} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype) || OWL2Datatype.XSD_LONG.equals(datatype)) {
			DateRangeLimited range = new DateRangeLimited();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				range.setLimit(Objects.requireNonNull(OWLFacet.getFacetBySymbolicName(minOperator)), min);
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				range.setLimit(Objects.requireNonNull(OWLFacet.getFacetBySymbolicName(maxOperator)), max);
			}
			return range;
		}

		return null;
	}

	/**
	 * Creates potentially multiple restricted phenotypes depending on the values and labels lists
	 * and adds them to the provided phenotype.
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param enumValues A list of enumeration values.
	 */
	private DataRange createRestrictedPhenotypeRange(OWL2Datatype datatype, List<String> enumValues) {
		if (OWL2Datatype.XSD_DECIMAL.equals(datatype)) {
			return new DecimalRangeEnumerated(enumValues.stream().filter(StringUtils::isNoneBlank).map(
				v -> { try { return BigDecimal.valueOf(Double.valueOf(v)); } catch (Exception ignored) { return null; } }).toArray(BigDecimal[]::new));
		} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype)) {
			return new DateRangeEnumerated(enumValues.stream().filter(StringUtils::isNoneBlank).map(
				v -> { try { return Parser.parseStringToDate(v); } catch (ParseException ignored) { return null; } }).toArray(Date[]::new));
		} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
			return new StringRange(enumValues.stream().filter(StringUtils::isNoneBlank).toArray(String[]::new));
		} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
			if (enumValues.size() > 0 && StringUtils.isNoneBlank(enumValues.get(0)))
				return new BooleanRange(Boolean.valueOf(enumValues.get(0)));
		}

		return null;
	}
}
