package de.onto_med.ontology_service.factory;

import de.onto_med.ontology_service.data_model.Phenotype;
import de.onto_med.ontology_service.util.Parser;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.phenotype.RestrictedBooleanPhenotype;
import org.lha.phenoman.model.phenotype.RestrictedCalculationPhenotype;
import org.lha.phenoman.model.phenotype.RestrictedSinglePhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.activation.UnsupportedDataTypeException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Convenient factory to construct RestrictedPhenotypes.
 * @author Christoph Beger
 */
public abstract class RestrictedPhenotypeFactory extends PhenotypeFactory {

	/**
	 * Creates a RestrictedPhenotype according to the provided phenotype data.
	 * @param manager The associated PhenotypeOntologyManager.
	 * @param data Phenotype data.
	 * @return A Restricted Phenotype.
	 * @throws UnsupportedDataTypeException If thr provided phenotype data contains invalid data.
	 */
	public static RestrictedPhenotype createRestrictedPhenotype(PhenotypeOntologyManager manager, Phenotype data) throws UnsupportedDataTypeException {
		Category superPhenotype = manager.getPhenotype(data.getSuperPhenotype());

		RestrictedPhenotype phenotype;
		if (superPhenotype == null) {
			throw new UnsupportedDataTypeException("Super phenotype does not exist");
		} else if (superPhenotype.isAbstractBooleanPhenotype()) {
			phenotype = RestrictedPhenotypeFactory.createRestrictedBooleanPhenotype(manager, data, superPhenotype);
		} else if (superPhenotype.isAbstractCalculationPhenotype()) {
			phenotype = RestrictedPhenotypeFactory.createRestrictedCalculationPhenotype(data, superPhenotype);
		} else if (superPhenotype.isAbstractSinglePhenotype()) {
			phenotype = RestrictedPhenotypeFactory.createRestrictedSinglePhenotype(data, superPhenotype);
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
	private static RestrictedSinglePhenotype createRestrictedSinglePhenotype(Phenotype data, Category superPhenotype) {
		RestrictedSinglePhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = new RestrictedSinglePhenotype(
				data.getTitleEn(), "en", data.getSuperPhenotype(),
				createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data)
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = new RestrictedSinglePhenotype(
				data.getTitleDe(), "de", data.getSuperPhenotype(),
				createRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), data)
			);
		}
		return phenotype;
	}

	/**
	 * Creates a restricted calculation phenotype.
	 * @param data Phenotype data.
	 * @param superPhenotype The super phenotype.
	 * @return A RestrictedCalculationPhenotype.
	 */
	private static RestrictedCalculationPhenotype createRestrictedCalculationPhenotype(Phenotype data, Category superPhenotype) {
		RestrictedCalculationPhenotype phenotype;
		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = new RestrictedCalculationPhenotype(
				data.getTitleEn(), "en", superPhenotype.getName(),
				createRestrictedPhenotypeRange(OWL2Datatype.XSD_DOUBLE, data)
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = new RestrictedCalculationPhenotype(
				data.getTitleDe(), "de", superPhenotype.getName(),
				createRestrictedPhenotypeRange(OWL2Datatype.XSD_DOUBLE, data)
			);
		}
		return phenotype;
	}

	/**
	 * Creates a restricted boolean phenotype.
	 * @param manager The associated PhenotypeOntologyManager.
	 * @param data Phenotype data.
	 * @param superPhenotype The super phenotype.
	 * @return A RestrictedBooleanPhenotype.
	 */
	private static RestrictedBooleanPhenotype createRestrictedBooleanPhenotype(PhenotypeOntologyManager manager, Phenotype data, Category superPhenotype) {
		RestrictedBooleanPhenotype phenotype;
		if (StringUtils.isBlank(data.getExpression()))
			throw new NullPointerException("Boolean expression for restricted boolean phenotype is missing.");

		if (StringUtils.isNoneBlank(data.getTitleEn())) {
			phenotype = new RestrictedBooleanPhenotype(
				data.getTitleEn(), "en", superPhenotype.getName(),
				manager.getManchesterSyntaxExpression(data.getExpression())
			);
			if (StringUtils.isNoneBlank(data.getTitleDe())) phenotype.addTitle(data.getTitleDe(), "de");
		} else {
			phenotype = new RestrictedBooleanPhenotype(
				data.getTitleDe(), "de", superPhenotype.getName(),
				manager.getManchesterSyntaxExpression(data.getExpression())
			);
		}
		phenotype.asRestrictedBooleanPhenotype().setScore(data.getScore());
		return phenotype;
	}

	/**
	 * Creates a range restriction for the given datatype and phenotype data
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param data The phenotype data.
	 * @return A value range for a restricted phenotype.
	 * @throws NullPointerException If no range could be generated.
	 */
	private static PhenotypeRange createRestrictedPhenotypeRange(OWL2Datatype datatype, Phenotype data) throws NullPointerException {
		PhenotypeRange range = Optional.ofNullable(createRestrictedPhenotypeRange(
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
	private static PhenotypeRange createRestrictedPhenotypeRange(OWL2Datatype datatype, String min, String minOperator, String max, String maxOperator) {
		List<OWLFacet> facets = new ArrayList<>();

		if ((StringUtils.isBlank(min) || StringUtils.isBlank(minOperator)) && (StringUtils.isBlank(max) || StringUtils.isBlank(maxOperator))) {
			return null;
		} else if (datatype.equals(OWL2Datatype.XSD_INTEGER)) {
			List<Integer> values = new ArrayList<>();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Integer.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Integer.valueOf(max));
			}
			return new PhenotypeRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Integer[values.size()]));
		} else if (datatype.equals(OWL2Datatype.XSD_DOUBLE)) {
			List<Double> values = new ArrayList<>();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Double.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Double.valueOf(max));
			}
			return new PhenotypeRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Double[values.size()]));
		} else if (datatype.equals(OWL2Datatype.XSD_DATE_TIME)) {
			List<Date> values = new ArrayList<>();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				try { values.add(Parser.parseStringToDate(min)); } catch (Exception e) { values.add(null); }
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				try { values.add(Parser.parseStringToDate(max)); } catch (Exception e) { values.add(null); }
			}
			return new PhenotypeRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Date[values.size()]));
		}
		return null;
	}

	/**
	 * Creates potentially multiple restricted phenotypes depending on the values and labels lists
	 * and adds them to the provided phenotype.
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param enumValues A list of enumeration values.
	 */
	private static PhenotypeRange createRestrictedPhenotypeRange(OWL2Datatype datatype, List<String> enumValues) {
		if (OWL2Datatype.XSD_INTEGER.equals(datatype)) {
			List<Integer> values = new ArrayList<>();
			enumValues.stream().filter(StringUtils::isNoneBlank).forEach(
				v -> { try { values.add(Integer.valueOf(v)); } catch (Exception ignored) { } });
			return new PhenotypeRange(values.toArray(new Integer[values.size()]));
		} else if (OWL2Datatype.XSD_DOUBLE.equals(datatype)) {
			List<Double> values = new ArrayList<>();
			enumValues.stream().filter(StringUtils::isNoneBlank).forEach(
				v -> { try { values.add(Double.valueOf(v)); } catch (Exception ignored) { } });
			return new PhenotypeRange(values.toArray(new Double[values.size()]));
		} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype)) {
			List<Date> values = new ArrayList<>();
			enumValues.stream().filter(StringUtils::isNoneBlank).forEach(
				v -> { try {
					values.add(Parser.parseStringToDate(v));
				} catch (ParseException ignored) { } });
			return new PhenotypeRange(values.toArray(new Date[values.size()]));
		} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
			return new PhenotypeRange(enumValues.stream().filter(StringUtils::isNoneBlank).toArray(String[]::new));
		} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
			if (enumValues.size() > 0 && StringUtils.isNoneBlank(enumValues.get(0)))
				return new PhenotypeRange(Boolean.valueOf(enumValues.get(0)));
		}

		return null;
	}
}
