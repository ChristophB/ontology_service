package de.onto_med.ontology_service.manager;

import de.imise.graph_api.graph.Graph;
import de.onto_med.ontology_service.data_model.Phenotype;
import de.onto_med.ontology_service.data_model.Property;
import de.onto_med.ontology_service.factory.AbstractPhenotypeFactory;
import de.onto_med.ontology_service.factory.PhenotypeCategoryFactory;
import de.onto_med.ontology_service.factory.PhenotypeFactory;
import de.onto_med.ontology_service.factory.RestrictedPhenotypeFactory;
import de.onto_med.ontology_service.util.Parser;
import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.exception.WrongPhenotypeTypeException;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.category_tree.PhenotypeCategoryTreeNode;
import org.lha.phenoman.model.instance.ComplexPhenotypeInstance;
import org.lha.phenoman.model.instance.SinglePhenotypeInstance;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.lha.phenoman.model.reasoner_result.ReasonerReport;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.UnsupportedDataTypeException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class serves as communicator between the web application and the PhenoMan.
 *
 * @author Christoph Beger
 */
public class PhenotypeManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypeManager.class);

	public static final String BASE_URL = "http://lha.org/";

	/**
	 * The PhenoMan manager instance of a phenotype ontology.
	 */
	private PhenotypeOntologyManager manager;
	private String                   phenotypePath;

	/**
	 * This constructor opens an existing phenotype ontology or creates a new one.
	 * Existing files are not overwritten but appended to.
	 * The parameter @code{phenotypePath} specifies the location of the existing
	 * ontology or of the ontology to be created.
	 *
	 * @param phenotypePath Path to the phenotype ontology OWL file.
	 */
	public PhenotypeManager(String phenotypePath) {
		this.phenotypePath = phenotypePath;
		manager = new PhenotypeOntologyManager(phenotypePath, false);
		manager.write();
	}

	public static String buildIri(String suffix) {
		return BASE_URL + suffix + "#";
	}

	/**
	 * Retrieves a phenotype for a given id from the cop ontology.
	 *
	 * @param id Identifier to search for. Can be suffix or full IRI.
	 * @return The found phenotype as Category object.
	 */
	public Category getPhenotype(String id) {
		if (StringUtils.isBlank(id)) return null;
		try {
			String suffix = PhenotypeFactory.getLocalName(id);
			Category category = manager.getPhenotype(suffix);

			return category != null ? category : manager.getCategory(suffix);
		} catch (NoSuchElementException e) {
			LOGGER.info(e.getMessage());
		}
		return null;
	}

	/**
	 * Returns the top node of the cop.owl taxonomy. The node may contain child nodes.
	 *
	 * @param includePhenotypes If true the method returns the taxonomy with phenotypes,
	 *                          else only categories are included.
	 * @return Top node of the cop.owl taxonomy.
	 */
	public TreeNode getTaxonomy(Boolean includePhenotypes) {
		PhenotypeCategoryTreeNode node     = manager.getPhenotypeCategoryTree(includePhenotypes);
		TreeNode                  treeNode = getTreeNode(node, includePhenotypes);
		treeNode.setOpened(true);

		return treeNode;
	}

	/**
	 * Creates a phenotype category from provided category data.
	 *
	 * @param formData Category data
	 * @return The created category.
	 * @throws NullPointerException If a required parameter is missing.
	 */
	public Category createCategory(Phenotype formData) throws NullPointerException {
		Category category = new PhenotypeCategoryFactory(manager).createPhenotypeCategory(formData);

		if (StringUtils.isBlank(formData.getSuperCategory()))
			manager.addPhenotypeCategory(category);
		else manager.addPhenotypeCategory(category, formData.getSuperCategory());
		manager.write();

		return category;
	}

	/**
	 * Creates an abstract phenotype from provided phenotype data.
	 *
	 * @param formData Phenotype data
	 * @return The created abstract Phenotype.
	 * @throws NullPointerException         If a required parameter is missing.
	 * @throws UnsupportedDataTypeException If the provided datatype of the phenotype is not supported.
	 */
	public AbstractPhenotype createAbstractPhenotype(Phenotype formData) throws NullPointerException, UnsupportedDataTypeException, WrongPhenotypeTypeException {
		if (StringUtils.isBlank(formData.getDatatype()))
			throw new NullPointerException("Datatype of the abstract phenotype is missing.");

		AbstractPhenotype phenotype = new AbstractPhenotypeFactory(manager).createAbstractPhenotype(formData);
		addPhenotype(phenotype);

		manager.write();
		return phenotype;
	}

	/**
	 * Creates a restricted phenotype from provided phenotype data.
	 *
	 * @param formData Phenotype data
	 * @return The created restricted phenotype.
	 * @throws NullPointerException         If a required parameter is missing.
	 * @throws UnsupportedDataTypeException If the provided data type of the phenotype is not supported.
	 */
	public RestrictedPhenotype createRestrictedPhenotype(Phenotype formData) throws NullPointerException, UnsupportedDataTypeException, WrongPhenotypeTypeException {
		if (StringUtils.isBlank(formData.getSuperPhenotype()))
			throw new NullPointerException("Super phenotype is missing.");

		RestrictedPhenotype phenotype = new RestrictedPhenotypeFactory(manager).createRestrictedPhenotype(formData);
		addPhenotype(phenotype);

		manager.write();
		return phenotype;
	}

	/**
	 * This method returns Phenotype objects as map, which are associated with the provided abstract phenotype.
	 * The map contains the names of restricted phenotypes and the title.
	 * @param abstractPhenotype The abstract Phenotype.
	 * @return A map of restricted Phenotype names and titles
	 */
	public Map<String, String> getRestrictions(String abstractPhenotype) {
		return manager.getRestrictedPhenotypes(abstractPhenotype).stream()
			.collect(Collectors.toMap(Category::getName, c -> c.getMainTitle().getTitleText()));
	}

	/**
	 * Returns a list of all phenotypes, which are referencing this phenotype, thus require it for reasoning.
	 *
	 * @param iri The local name or IRI of the phenotype to search for.
	 * @return List of dependent phenotypes as Category.
	 */
	public List<Category> getDependentPhenotypes(String iri) {
		return manager.getDependentPhenotypes(PhenotypeFactory.getLocalName(iri));
	}

	/**
	 * Returns a list of all abstract single phenotypes, which are required for the calculation of the provided phenotype.
	 *
	 * @param iri The local name or IRI of the phenotype to be calculated.
	 * @return Set of required abstract single phenotypes for the calculation.
	 */
	public List<Phenotype> getParts(String iri) {
		List<Phenotype> parts = new ArrayList<>();

		manager.getParts(PhenotypeFactory.getLocalName(iri)).forEach(p -> {
			Phenotype part = new Phenotype() {{
				setIsRestricted(false);
				setIsPhenotype(true);
				setDatatype(p.getDatatypeText());
				setUcum(p.getUnit());
				setDescriptionMap(p.getDescriptions());
				setSelectOptions(getRestrictions(p.getName()));
				setName(p.getName());
			}};
			parts.add(part);
		});

		return parts;
	}

	/**
	 * Deletes all phenotypes, which are represented in the set with their ID.
	 *
	 * @param ids A Set of phenotype IDs
	 */
	public void deletePhenotypes(Set<String> ids) {
		manager.removePhenotypes(ids);
		manager.write();
	}

	/**
	 * This method created a temporary individual with the provided list of properties.
	 * Thereafter, types of the individual are retrieved with a reasoner and returned as @code{ReasonerReport}.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return A ReasonerReport which contains all found types of the individual.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	public ReasonerReport classifyIndividual(List<Property> properties) throws IllegalArgumentException {
		ComplexPhenotypeInstance complex = new ComplexPhenotypeInstance();

		for (Property property : properties) {
			if (StringUtils.isBlank(property.getName())) continue;
			Category phenotype = manager.getPhenotype(property.getName());
			if (phenotype == null) continue;

			String value = property.getValue();
			String name  = property.getName();
			if (StringUtils.isBlank(value)) value = null;

			SinglePhenotypeInstance instance;
			if (phenotype.isRestrictedPhenotype()) {
				instance = new SinglePhenotypeInstance(manager.getRestrictedSinglePhenotype(name));
			} else if (value == null) {
				continue;
			} else if (phenotype.isAbstractBooleanPhenotype()) {
				instance = new SinglePhenotypeInstance(name, Boolean.valueOf(value));
			} else if (phenotype.isAbstractCalculationPhenotype()) {
				try {
					instance = new SinglePhenotypeInstance(name, Double.valueOf(value));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Could not parse Double from String '" + value + "'." + e.getMessage());
				}
			} else if (phenotype.isAbstractSinglePhenotype()) {
				if (OWL2Datatype.XSD_INTEGER.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try {
						instance = new SinglePhenotypeInstance(name, Integer.valueOf(value));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Could not parse Integer from String '" + value + "'.");
					}
				} else if (OWL2Datatype.XSD_DOUBLE.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try {
						instance = new SinglePhenotypeInstance(name, Double.valueOf(value));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Could not parse Double from String '" + value + "'." + e.getMessage());
					}
				} else if (OWL2Datatype.XSD_DATE_TIME.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
					try {
						instance = new SinglePhenotypeInstance(name, Parser.parseStringToDate(value));
					} catch (ParseException e) {
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

	/**
	 * Returns the result of @code{classifyIndividual} as HTML formatted string.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return String representation of the reasoning result.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	public String classifyIndividualAsString(List<Property> properties) throws IllegalArgumentException {
		return classifyIndividual(properties).getPhenotypes().stream().map(Category::getName).collect(Collectors.toList()).toString();
	}

	/**
	 * Returns the result of @code{classifyIndividual} as list.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return A list of class names.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	public List<String> classifyIndividualAsList(List<Property> properties) throws IllegalArgumentException {
		return classifyIndividual(properties).getPhenotypes().stream().map(Category::getName).collect(Collectors.toList());
	}

	/**
	 * Returns the result of @code{classifyIndividual} as image.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return Byte representation of the resulting image.
	 * @throws IOException              If the file image file could not be created or written to.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
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
	 *
	 * @param phenotypeId The phenotype's identifier.
	 * @param format      String representation of the requested format.
	 * @return String representation of the result.
	 * @throws IllegalArgumentException If the provided format is not one of 'png' or 'graphml'.
	 * @throws IOException              If BufferedImage could not be written to ByteArrayOutputStream.
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

	/**
	 * Returns all category and phenotype IDs of the ontology as list.
	 * @return List of phenotype IDs.
	 */
	public List<String> getList() {
		return taxonomyAsList(manager.getPhenotypeCategoryTree(true));
	}

	/**
	 * Returns the full RDF document for this ontology as string.
	 *
	 * @return string containing the full RDF document.
	 */
	public String getFullRdfDocument() throws IOException {
		return new String(Files.readAllBytes(Paths.get(phenotypePath)));
	}


	private List<String> taxonomyAsList(PhenotypeCategoryTreeNode node) {
		List<String> result = new ArrayList<>();
		result.add(node.getName());
		node.getChildren().forEach(c -> result.addAll(taxonomyAsList(c)));
		return result;
	}

	private TreeNode getTreeNode(PhenotypeCategoryTreeNode node, Boolean includePhenotypes) {
		Category     category = node.getCategory();
		String       text     = category != null ? category.getMainTitleText() : node.getName();
		OWL2Datatype datatype = getDatatype(category);

		TreeNode treeNode = new TreeNode(node.getName(), text);
		treeNode.setType(owl2DatatypeToString(datatype, category));

		if (category != null) {
			StringBuilder tooltip = new StringBuilder();
			for (String lang : category.getTitles().keySet()) {
				tooltip.append(String.format(
					"%s: %s (%s)" + System.lineSeparator(),
					lang, category.getTitle(lang).getTitleText(),
					StringUtils.defaultString(category.getTitle(lang).getAlias(), "none")
				));
			}
			treeNode.setTitle(tooltip.toString());

			if (category.isRestrictedPhenotype()) {
				treeNode.setRestrictedPhenotype();
			} else if (category.isAbstractPhenotype()) {
				treeNode.setAbstractPhenotype();
			} else {
				treeNode.setCategory();
			}

			if (category.isAbstractSinglePhenotype() || category.isRestrictedSinglePhenotype()) {
				treeNode.setSinglePhenotype();
				if (OWL2Datatype.XSD_DOUBLE.equals(datatype) || OWL2Datatype.XSD_INTEGER.equals(datatype)) {
					treeNode.setNumericPhenotype();
				} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
					treeNode.setStringPhenotype();
				} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype) || OWL2Datatype.XSD_LONG.equals(datatype)) {
					treeNode.setDatePhenotype();
				} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
					treeNode.setBooleanPhenotype();
				}
			} else if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
				treeNode.setCompositeBooleanPhenotype();
			} else if (category.isAbstractCalculationPhenotype() || category.isRestrictedCalculationPhenotype()) {
				treeNode.setCalculationPhenotype();
			}
		} else treeNode.setCategory();

		node.getChildren().stream().sorted(Comparator.comparing(PhenotypeCategoryTreeNode::getName)).forEach(child -> {
			if (includePhenotypes || !child.getCategory().isPhenotype())
				treeNode.addChild(getTreeNode(child, includePhenotypes));
		});

		return treeNode;
	}

	/**
	 * Converts an OWL2Datatype to a string, depending on the provided category/phenotype.
	 *
	 * @param datatype The OWL2Datatype.
	 * @param category The category or phenotype object.
	 * @return A string for representation of the OWL2Datatype.
	 */
	private String owl2DatatypeToString(OWL2Datatype datatype, Category category) {
		if (datatype == null || category == null) {
			return null;
		} if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
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

	/**
	 * Returns the OWL2Datatype of a category or phenotype.
	 *
	 * @param category The category or phenotype object.
	 * @return The OWL2Datatype of the category.
	 */
	private OWL2Datatype getDatatype(Category category) {
		if (category == null) {
			return null;
		} if (category.isAbstractSinglePhenotype()) {
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
	 * This function checks if the provided Phenotype is one of
	 * RestrictedSinglePhenotype, RestrictedBooleanPhenotype, RestrictedCalculationPhenotype,
	 * AbstractSinglePhenotype, AbstractBooleanPhenotype, AbstractCalculationPhenotype
	 * and uses the appropriate function to add the phenotype to the manager.
	 *
	 * @param phenotype A phenotype which will be added to the manager.
	 */
	private void addPhenotype(Category phenotype) throws WrongPhenotypeTypeException {
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
		public String icon = "";
		public State          state    = new State();
		public List<TreeNode> children = new ArrayList<>();
		public AttributeList  a_attr   = new AttributeList();

		TreeNode(String id, String text, String title) {
			this(id, text);
			a_attr.title = title;
		}

		TreeNode(String id, String text) {
			a_attr.id = id;
			this.text = text;
		}

		public void setOpened(Boolean opened) {
			state.opened = opened;
		}

		public void setSelected(Boolean selected) {
			state.selected = selected;
		}

		public void setType(String type) {
			a_attr.type = type;
		}

		public void setTitle(String title) {
			a_attr.title = title;
		}

		void setCategory() {
			a_attr.isPhenotype = false;
			a_attr.isRestricted = false;
			icon += " fa fa-folder-open-o text-secondary";
		}

		void setRestrictedPhenotype() {
			a_attr.isPhenotype = true;
			a_attr.isRestricted = true;
			icon += " text-warning";
		}

		void setAbstractPhenotype() {
			a_attr.isPhenotype = true;
			a_attr.isRestricted = false;
			icon += " text-primary";
		}

		void setSinglePhenotype() {
			a_attr.isPhenotype = true;
			a_attr.isSinglePhenotype = true;
		}

		void setNumericPhenotype() {
			icon += " fa fa-calculator";
		}

		void setStringPhenotype() {
			icon += " fa fa-font";
		}

		void setDatePhenotype() {
			icon += " fa fa-calendar";
		}

		void setBooleanPhenotype() {
			icon += " fa fa-check-circle-o";
		}

		void setCompositeBooleanPhenotype() {
			icon += " fa fa-check-circle";
		}

		void setCalculationPhenotype() {
			icon += " fa fa-calculator";
		}

		void addChild(TreeNode child) {
			children.add(child);
		}

		public void addCategory(String category) {
			a_attr.categories.add(category);
		}
	}

	public class AttributeList { // TODO: add superPhenotype
		public String       type;
		public String       id;
		public String       title;
		public List<String> categories;
		public Boolean isPhenotype       = false;
		public Boolean isRestricted      = false;
		public Boolean isSinglePhenotype = false;
	}

	public class State {
		public Boolean opened   = false;
		public Boolean selected = false;
	}
}
