package de.onto_med.ontology_service.manager;

import de.imise.graph_api.graph.Graph;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.data_models.Property;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.category_tree.PhenotypeCategoryTreeNode;
import org.lha.phenoman.model.instance.ComplexPhenotypeInstance;
import org.lha.phenoman.model.instance.SinglePhenotypeInstance;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.*;
import org.lha.phenoman.model.reasoner_result.ReasonerReport;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.activation.UnsupportedDataTypeException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PhenotypeManager {
	private final List<String> DATE_PATTERNS = Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd");

	private PhenotypeOntologyManager manager;

	public PhenotypeManager(String phenotypePath) {
		manager = new PhenotypeOntologyManager(phenotypePath, false);
		manager.write();
	}

	/**
	 * Retrieves a phenotype for a given id from the cop ontology.
	 * @param id Identifier to search for. Can be suffix or full IRI.
	 * @return The found phenotype as Category object.
	 */
	public Category getPhenotype(String id) {
		if (StringUtils.isBlank(id)) return null;
		Category category = manager.getCategory(XMLUtils.getNCNameSuffix(id));
		return category != null ? category : manager.getPhenotype(XMLUtils.getNCNameSuffix(id));
	}

	/**
	 * Returns the top node of the cop.owl taxonomy. The node may contain child nodes.
	 * @param includePhenotypes If true the method returns the taxonomy with phenotypes,
	 *                          else only categories are included.
	 * @return Top node of the cop.owl taxonomy.
	 */
	public TreeNode getTaxonomy(Boolean includePhenotypes) {
		PhenotypeCategoryTreeNode node = manager.getPhenotypeCategoryTree(includePhenotypes);
		TreeNode treeNode = getTreeNode(node, includePhenotypes);
		treeNode.setOpened(true);

		return treeNode;
	}

	/**
	 * Creates a phenotype category from provided category data.
	 * @param formData Category data
	 * @return The created category.
	 * @throws NullPointerException If a required parameter is missing.
	 */
	public Category createCategory(Phenotype formData) throws NullPointerException {
		if (StringUtils.isBlank(formData.getId()))
			throw new NullPointerException("ID of category is missing.");

		Category category = new Category(formData.getId());
		setPhenotypeBasicData(category, formData);

		if (StringUtils.isBlank(formData.getSuperCategory()))
			manager.addPhenotypeCategory(category);
		else manager.addPhenotypeCategory(category, formData.getSuperCategory());
		manager.write();

		return category;
	}

	/**
	 * Creates an abstract phenotype from provided phenotype data.
	 * @param formData Phenotype data
	 * @return The created abstract Phenotype.
	 * @throws NullPointerException If a required parameter is missing.
	 * @throws UnsupportedDataTypeException If the provided datatype of the phenotype is not supported.
	 */
	public AbstractPhenotype createAbstractPhenotype(Phenotype formData) throws NullPointerException, UnsupportedDataTypeException {
		if (StringUtils.isBlank(formData.getId()))
			throw new NullPointerException("ID of the abstract phenotype is missing.");
		if (StringUtils.isBlank(formData.getDatatype()))
			throw new NullPointerException("Datatype of the abstract phenotype is missing.");

		AbstractPhenotype phenotype;
		switch (formData.getDatatype()) {
			case "numeric":
				OWL2Datatype datatype = formData.getIsDecimal() != null && formData.getIsDecimal()
					? OWL2Datatype.XSD_DOUBLE : OWL2Datatype.XSD_INTEGER;
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractSinglePhenotype(formData.getId(), datatype)
					: new AbstractSinglePhenotype(formData.getId(), datatype, formData.getCategories().split(";"));
				if (StringUtils.isNoneBlank(formData.getUcum()))
					phenotype.asAbstractSinglePhenotype().setUnit(formData.getUcum());
				break;
			case "string":
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_STRING)
					: new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_STRING, formData.getCategories().split(";"));
				break;
			case "date":
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DATE_TIME)
					: new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DATE_TIME, formData.getCategories().split(";"));
				break;
			case "boolean":
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_BOOLEAN)
					: new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_BOOLEAN, formData.getCategories().split(";"));
				break;
			case "composite-boolean":
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractBooleanPhenotype(formData.getId())
					: new AbstractBooleanPhenotype(formData.getId(), formData.getCategories().split(";"));
				break;
			case "calculation":
				if (StringUtils.isBlank(formData.getFormula()))
					throw new NullPointerException("Formula for abstract calculated phenotype is missing.");
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractCalculationPhenotype(formData.getId(), manager.getFormula(formData.getFormula()))
					: new AbstractCalculationPhenotype(formData.getId(), manager.getFormula(formData.getFormula()), formData.getCategories().split(";"));
				if (StringUtils.isNoneBlank(formData.getUcum()))
					phenotype.asAbstractCalculationPhenotype().setUnit(formData.getUcum());
				break;
			default:
				throw new UnsupportedDataTypeException("Could not determine Datatype.");
		}

		setPhenotypeBasicData(phenotype, formData);
		addPhenotype(phenotype);

		manager.write();
		return phenotype;
	}

	/**
	 * Creates a restricted phenotype from provided phenotype data.
	 * @param formData Phenotype data
	 * @return The created restricted phenotype.
	 * @throws NullPointerException If a required parameter is missing.
	 * @throws UnsupportedDataTypeException If the provided datatype of the phenotype is not supported.
	 */
	public RestrictedPhenotype createRestrictedPhenotype(Phenotype formData) throws NullPointerException, UnsupportedDataTypeException {
		if (StringUtils.isBlank(formData.getId()))
			throw new NullPointerException("ID or super phenotype is missing.");
		if (StringUtils.isBlank(formData.getSuperPhenotype()))
			throw new NullPointerException("Super phenotype is missing.");

		Category superPhenotype = manager.getPhenotype(formData.getSuperPhenotype());
		RestrictedPhenotype phenotype;

		if (superPhenotype == null) {
			throw new UnsupportedDataTypeException("Super phenotype does not exist");
		} else if (superPhenotype.isAbstractBooleanPhenotype()) {
			if (StringUtils.isBlank(formData.getExpression()))
				throw new NullPointerException(
					"Boolean expression for restricted boolean phenotype is missing.");

			phenotype = new RestrictedBooleanPhenotype(
				formData.getId(), superPhenotype.getName(),
				manager.getManchesterSyntaxExpression(formData.getExpression())
			);
			phenotype.asRestrictedBooleanPhenotype().setScore(formData.getScore());
		} else if (superPhenotype.isAbstractCalculationPhenotype()) {
			phenotype = new RestrictedCalculationPhenotype(
				formData.getId(), formData.getSuperPhenotype(),
				getRestrictedPhenotypeRange(OWL2Datatype.XSD_DOUBLE, formData)
			);
		} else if (superPhenotype.isAbstractSinglePhenotype()) {
			phenotype = new RestrictedSinglePhenotype(
				formData.getId(), formData.getSuperPhenotype(),
				getRestrictedPhenotypeRange(superPhenotype.asAbstractSinglePhenotype().getDatatype(), formData)
			);
		} else {
			throw new UnsupportedDataTypeException(
				"Could not determine datatype of super phenotype.");
		}

		setPhenotypeBasicData(phenotype, formData);
		addPhenotype(phenotype);

		manager.write();
		return phenotype;
	}

	private ReasonerReport classifyIndividual(List<Property> properties) throws IllegalArgumentException {
		ComplexPhenotypeInstance complex = new ComplexPhenotypeInstance();

		for (Property property : properties) {
			if (StringUtils.isBlank(property.getName())) continue;
			Category phenotype = manager.getPhenotype(property.getName());
			if (phenotype == null) continue;

			String value = property.getValue();
			String name  = property.getName();

			SinglePhenotypeInstance instance;
			if (phenotype.isRestrictedPhenotype()) {
				throw new IllegalArgumentException("Reasoning with restricted phenotypes is not yet supported.");
				// instance = new SinglePhenotypeInstance(name, null); // TODO: request implementation
			} else if (phenotype.isAbstractBooleanPhenotype()) {
				instance = new SinglePhenotypeInstance(name, Boolean.valueOf(value));
			} else if (phenotype.isAbstractCalculationPhenotype()) {
				try { instance = new SinglePhenotypeInstance(name, Double.valueOf(value)); }
				catch (NumberFormatException e) {
					throw new IllegalArgumentException("Could not parse Double from String '" + value + "'." + e.getMessage());
				}
			} else if (phenotype.isAbstractSinglePhenotype()) {
				if (OWL2Datatype.XSD_INTEGER.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try { instance = new SinglePhenotypeInstance(name, Integer.valueOf(value)); }
					catch (NumberFormatException e) {
						throw new IllegalArgumentException("Could not parse Integer from String '" + value + "'.");
					}
				} else if (OWL2Datatype.XSD_DOUBLE.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try { instance = new SinglePhenotypeInstance(name, Double.valueOf(value)); }
					catch (NumberFormatException e) {
						throw new IllegalArgumentException("Could not parse Double from String '" + value + "'." + e.getMessage());
					}
				} else if (OWL2Datatype.XSD_DATE_TIME.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try { instance = new SinglePhenotypeInstance(name, parseStringToDate(value)); }
					catch (ParseException e) {
						throw new IllegalArgumentException("Could not parse Date from String '" + value + "'. " + e.getMessage());
					}
				} else if (OWL2Datatype.XSD_BOOLEAN.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					instance = new SinglePhenotypeInstance(name, Boolean.valueOf(value));
				} else {
					instance = new SinglePhenotypeInstance(name, value);
				}
			} else continue;

			complex.addSinglePhenotypeInstance(instance);
		}

		return manager.derivePhenotypes(complex, 0).getFinalReport();
	}

	public List<String> classifyIndividualAsList(List<Property> properties) throws IllegalArgumentException {
		return classifyIndividual(properties).getPhenotypes().stream().map(Category::getName).collect(Collectors.toList());
	}

	public byte[] classifyIndividualAsImage(List<Property> properties) throws IOException, IllegalArgumentException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(
			manager.writeGraphToPNG(manager.createReasonerReportGraph(classifyIndividual(properties)), true),
			"png", out
		);
		return out.toByteArray();
	}

	/**
	 * Creates a phenotype decision tree in the requested format.
	 * @param phenotypeId The phenotype's identifier.
	 * @param format String representation of the requested format.
	 * @return String representation of the result.
	 * @throws IllegalArgumentException If the provided format is not one of 'png' or 'graphml'.
	 * @throws IOException If BufferedImage could not be written to ByteArrayOutputStream.
	 */
	public Object getPhenotypeDecisionTree(String phenotypeId, String format) throws IllegalArgumentException, IOException {
		Graph graph = manager.createAbstractPhenotypeGraph(phenotypeId);

		if ("png".equalsIgnoreCase(format)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(manager.writeGraphToPNG(graph, true), "png", out);
			return out.toByteArray();
		} else if ("graphml".equalsIgnoreCase(format)) {
			return manager.writeGraphToGraphML(graph);
		} else {
			throw new IllegalArgumentException("Provided form '" + format + "' is not supported.");
		}
	}





	private TreeNode getTreeNode(PhenotypeCategoryTreeNode node, Boolean includePhenotypes) {
		Category category = node.getCategory();

		String tooltip  = category.hasLabels() ? getTextLang(category.getLabels()) : "";
		String text     = node.getName().replaceAll("_", " ");
		OWL2Datatype datatype = getDatatype(category);


		TreeNode treeNode = new TreeNode(node.getName(), text, tooltip);
		treeNode.setType(owl2DatatypeToString(datatype, category));

		if (category.isRestrictedPhenotype()) {
			treeNode.setRestrictedPhenotype();
		} else if (category.isAbstractPhenotype()) {
			treeNode.setAbstractPhenotype();
		}

		if (category.isAbstractSinglePhenotype() || category.isRestrictedSinglePhenotype()) {
			treeNode.setSinglePhenotype();
			if (OWL2Datatype.XSD_DOUBLE.equals(datatype) || OWL2Datatype.XSD_INTEGER.equals(datatype)) {
				treeNode.setNumericPhenotype();
			} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
				treeNode.setStringPhenotype();
			} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype)) {
				treeNode.setDatePhenotype();
			} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
				treeNode.setBooleanPhenotype();
			}
		} else if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
			treeNode.setCompositeBooleanPhenotype();
		} else if (category.isAbstractCalculationPhenotype() || category.isRestrictedCalculationPhenotype()) {
			treeNode.setCalculationPhenotype();
		}

		node.getChildren().stream().sorted(Comparator.comparing(PhenotypeCategoryTreeNode::getName)).forEach(child -> {
			if (includePhenotypes || !child.getCategory().isPhenotype())
				treeNode.addChild(getTreeNode(child, includePhenotypes));
		});
		return treeNode;
	}

	private String owl2DatatypeToString(OWL2Datatype datatype, Category category) {
		if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
			return "composite-boolean";
		} else if (category.isAbstractCalculationPhenotype() || category.isRestrictedCalculationPhenotype()) {
			return "calculation";
		} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
			return "string";
		} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype)) {
			return "date";
		} else if (OWL2Datatype.XSD_INTEGER.equals(datatype) || OWL2Datatype.XSD_DOUBLE.equals(datatype)) {
			return "numeric";
		} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
			return "boolean";
		}
		return null;
	}

	private OWL2Datatype getDatatype(Category category) {
		if (category.isAbstractSinglePhenotype()) {
			return category.asAbstractSinglePhenotype().getDatatype();
		} else if (category.isRestrictedSinglePhenotype()) {
			return category.asRestrictedSinglePhenotype().getDatatype();
		} else if (category.isAbstractCalculationPhenotype() || category.isRestrictedCalculationPhenotype()) {
			return OWL2Datatype.XSD_DOUBLE;
		} else if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
			return OWL2Datatype.XSD_BOOLEAN;
		}
		return null;
	}

	/**
	 * Adds basic information to the provided phenotype based on formData.
	 * Basic information includes only fields, which are available for all types of phenotypes.
	 * If the phenotype is abstract, categories are added too.
	 * @param phenotype An abstract or restricted phenotype.
	 * @param formData Data which was provided via form or JSON post request.
	 */
	private void setPhenotypeBasicData(Category phenotype, Phenotype formData) {
		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeRelations(phenotype, formData.getRelations());
	}

	private void setPhenotypeLabels(Category phenotype, List<String> labels, List<String> languages) {
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			if (StringUtils.isBlank(label)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addLabel(label, languages.get(i));
			else phenotype.addLabel(label);
		}
	}

	private void setPhenotypeDefinitions(Category phenotype, List<String> definitions, List<String> languages) {
		for (int i = 0; i < definitions.size(); i++) {
			String definition = definitions.get(i);
			if (StringUtils.isBlank(definition)) continue;
			if (languages.size() > i && StringUtils.isNoneBlank(languages.get(i)))
				phenotype.addDefinition(definition, languages.get(i));
			else phenotype.addDefinition(definition);
		}
	}

	private void setPhenotypeRelations(Category phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}

	private PhenotypeRange getRestrictedPhenotypeRange(OWL2Datatype datatype, Phenotype formData) throws NullPointerException {
		PhenotypeRange range = Optional.ofNullable(getRestrictedPhenotypeRange(
			datatype,
			formData.getRangeMin(), formData.getRangeMinOperator(),
			formData.getRangeMax(), formData.getRangeMaxOperator()
		)).orElse(getRestrictedPhenotypeRange(datatype, formData.getEnumValues()));

		if (range == null)
			throw new NullPointerException("No Restriction for restricted phenotype provided.");

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
	private PhenotypeRange getRestrictedPhenotypeRange(OWL2Datatype datatype, String min, String minOperator, String max, String maxOperator) {
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
				try { values.add(parseStringToDate(min)); } catch (Exception e) { values.add(null); }
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				try { values.add(parseStringToDate(max)); } catch (Exception e) { values.add(null); }
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
	private PhenotypeRange getRestrictedPhenotypeRange(OWL2Datatype datatype, List<String> enumValues) {
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
					values.add(parseStringToDate(v));
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

	/**
	 * Transforms a string into a java Date object.
	 * See {@link #DATE_PATTERNS} for allowed patterns.
	 * @param string String representation of a date.
	 * @return The parsed Date object.
	 * @throws ParseException If the string could not be parsed to Date.
	 */
	private Date parseStringToDate(String string) throws ParseException {
		Date date = null;

		for (String pattern : DATE_PATTERNS) {
			DateFormat format = new SimpleDateFormat(pattern);
			try {
				date = format.parse(string);
			} catch (ParseException ignored) { }
		}
		if (date == null) throw new ParseException("Could not parse string '" + string + "' to Date.", 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	private String getTextLang(Set<TextLang> set) {
		return String.join("\n", set.stream().map(textLang -> textLang.getLang() + ": " + textLang.getText()).collect(Collectors.toList()));
	}

	/**
	 * This function checks if the provided Phenotype is one of
	 * RestrictedSinglePhenotype, RestrictedBooleanPhenotype, RestrictedCalculationPhenotype,
	 * AbstractSinglePhenotype, AbstractBooleanPhenotype, AbstractCalculationPhenotype
	 * and uses the appropriate function to add the phenotype to the manager.
	 * @param phenotype A phenotype which will be added to the manager.
	 */
	private void addPhenotype(Category phenotype) {
		if (phenotype.isRestrictedCalculationPhenotype()) {
			manager.addRestrictedCalculationPhenotype(phenotype.asRestrictedCalculationPhenotype());
		} else if (phenotype.isRestrictedBooleanPhenotype()) {
			manager.addRestrictedBooleanPhenotype(phenotype.asRestrictedBooleanPhenotype());
		} else if (phenotype.isRestrictedSinglePhenotype()) {
			manager.addRestrictedSinglePhenotype(phenotype.asRestrictedSinglePhenotype());
		} else if (phenotype.isAbstractBooleanPhenotype()) {
			manager.addAbstractBooleanPhenotype(phenotype.asAbstractBooleanPhenotype());
		} else if (phenotype.isAbstractCalculationPhenotype()) {
			 manager.addAbstractCalculationPhenotype(phenotype.asAbstractCalculationPhenotype());
		} else if (phenotype.isAbstractSinglePhenotype()) {
			manager.addAbstractSinglePhenotype(phenotype.asAbstractSinglePhenotype());
		}
	}




	public class TreeNode {
		public String text;
		public String icon;
		public State state = new State();
		public List<TreeNode> children = new ArrayList<>();
		public AttributeList a_attr    = new AttributeList();

		TreeNode(String id, String text, String title) {
			this.text = text;
			a_attr.id    = id;
			a_attr.title = title;
		}

		public void setOpened(Boolean opened) {
			state.opened = opened;
		}

		public void setSelected(Boolean selected) {
			state.selected = selected;
		}

		public void setType(String type) { a_attr.type = type; }
		void setRestrictedPhenotype() {
			a_attr.phenotype = true;
			a_attr.restrictedPhenotype = true;
			icon += " text-warning";
		}
		void setAbstractPhenotype() {
			a_attr.phenotype = true;
			a_attr.abstractPhenotype = true;
			icon += " text-primary";
		}
		void setSinglePhenotype() {
			a_attr.phenotype = true;
			a_attr.singlePhenotype = true;
		}
		void setNumericPhenotype() { icon += " fa fa-calculator"; }
		void setStringPhenotype() { icon += " fa fa-font"; }
		void setDatePhenotype() { icon += " fa fa-calendar"; }
		void setBooleanPhenotype() { icon += " fa fa-check-circle-o"; }
		void setCompositeBooleanPhenotype() { icon += " fa fa-check-circle"; }
		void setCalculationPhenotype() { icon += " fa fa-calculator"; }

		void addChild(TreeNode child) { children.add(child); }
	}

	public class AttributeList {
		public String type;
		public String id;
		public String title;
		public Boolean phenotype = false;
		public Boolean restrictedPhenotype = false;
		public Boolean abstractPhenotype   = false;
		public Boolean singlePhenotype     = false;
	}

	public class State {
		public Boolean opened   = false;
		public Boolean selected = false;
	}
}
