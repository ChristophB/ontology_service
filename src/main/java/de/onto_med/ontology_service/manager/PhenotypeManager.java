package de.onto_med.ontology_service.manager;

import ca.uhn.fhir.rest.client.exceptions.FhirClientConnectionException;
import de.imise.graph_api.graph.Graph;
import de.onto_med.ontology_service.data_model.FhirProperties;
import de.onto_med.ontology_service.data_model.PhenotypeFormData;
import de.onto_med.ontology_service.data_model.Property;
import de.onto_med.ontology_service.factory.AbstractPhenotypeFactory;
import de.onto_med.ontology_service.factory.PhenotypeCategoryFactory;
import de.onto_med.ontology_service.factory.PhenotypeFactory;
import de.onto_med.ontology_service.factory.RestrictedPhenotypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.fhir.ucum.UcumException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smith.phenoman.exception.WrongPhenotypeTypeException;
import org.smith.phenoman.model.category_tree.EntityTreeNode;
import org.smith.phenoman.model.instance.CompositePhenotypeInstance;
import org.smith.phenoman.model.instance.SinglePhenotypeInstance;
import org.smith.phenoman.model.instance.value.BooleanValue;
import org.smith.phenoman.model.instance.value.DateValue;
import org.smith.phenoman.model.instance.value.DecimalValue;
import org.smith.phenoman.model.instance.value.StringValue;
import org.smith.phenoman.model.phenotype.top_level.*;
import org.smith.phenoman.model.reasoner_result.ReasonerReport;

import javax.activation.UnsupportedDataTypeException;
import javax.imageio.ImageIO;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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

	private static final String BASE_URL = "http://lha.org/";

	/**
	 * The PhenoMan manager instance of a phenotype ontology.
	 */
	private org.smith.phenoman.man.PhenotypeManager manager;
	private String                                phenotypePath;

	/**
	 * This constructor opens an existing phenotype ontology or creates a new one.
	 * Existing files are not overwritten but appended to.
	 * The parameter @code{phenotypePath} specifies the location of the existing
	 * ontology or of the ontology to be created.
	 *
	 * @param phenotypePath Path to the phenotype ontology OWL file.
	 */
	public PhenotypeManager(String phenotypePath) throws IllegalAccessException, InstantiationException {
		this.phenotypePath = phenotypePath;
		manager = new org.smith.phenoman.man.PhenotypeManager(phenotypePath, false);
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
	public Entity getEntity(String id) {
		if (StringUtils.isBlank(id)) return null;
		try {
			String   suffix   = PhenotypeFactory.getLocalName(id);
			Category category = manager.getCategory(suffix);

			return category != null ? category : manager.getPhenotype(suffix);
		} catch (NoSuchElementException e) {
			LOGGER.info(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unused")
	public Phenotype getPhenotype(String id) {
		if (StringUtils.isBlank(id)) return null;
		try {
			String suffix = PhenotypeFactory.getLocalName(id);
			return manager.getPhenotype(suffix);
		} catch (NoSuchElementException e) {
			LOGGER.info(e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unused")
	public Category getCategory(String id) {
		if (StringUtils.isBlank(id)) return null;
		try {
			String suffix = PhenotypeFactory.getLocalName(id);
			return manager.getCategory(suffix);
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
		EntityTreeNode node     = manager.getEntityTree(includePhenotypes);
		TreeNode       treeNode = getTreeNode(node, includePhenotypes);
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
	public Category createCategory(PhenotypeFormData formData) throws NullPointerException {
		Category category = new PhenotypeCategoryFactory().createPhenotypeCategory(formData);

		manager.addCategory(category);
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
	public AbstractPhenotype createAbstractPhenotype(PhenotypeFormData formData) throws NullPointerException, UnsupportedDataTypeException, WrongPhenotypeTypeException {
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
	public RestrictedPhenotype createRestrictedPhenotype(PhenotypeFormData formData) throws NullPointerException, UnsupportedDataTypeException, WrongPhenotypeTypeException, ParseException {
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
	 *
	 * @param abstractPhenotype The abstract Phenotype.
	 * @return A map of restricted Phenotype names and titles
	 */
	public Map<String, String> getRestrictions(String abstractPhenotype) {
		return manager.getRestrictedPhenotypes(abstractPhenotype).stream()
			.collect(Collectors.toMap(Phenotype::getName, c -> c.getMainTitle().getTitleText()));
	}

	/**
	 * Returns a list of all phenotypes, which are referencing this phenotype, thus require it for reasoning.
	 *
	 * @param iri The local name or IRI of the phenotype to search for.
	 * @return List of dependent phenotypes as Category.
	 */
	public List<Phenotype> getDependentPhenotypes(String iri) {
		return manager.getDependentPhenotypes(PhenotypeFactory.getLocalName(iri));
	}

	/**
	 * Returns a list of all abstract single phenotypes, which are required for the calculation of the provided phenotype.
	 *
	 * @param iri The local name or IRI of the phenotype to be calculated.
	 * @return Set of required abstract single phenotypes for the calculation.
	 */
	public List<PhenotypeFormData> getParts(String iri) {
		List<PhenotypeFormData> parts = new ArrayList<>();

		manager.getParts(PhenotypeFactory.getLocalName(iri)).forEach(p -> {
			PhenotypeFormData part = new PhenotypeFormData() {{
				setIsRestricted(false);
				setIsPhenotype(true);
				setDatatype(p.getDatatypeText());
				setUcum(p.getUnit());
				setDescriptionMap(p.getDescriptions());
				setSelectOptions(getRestrictions(p.getName()));
				setIdentifier(p.getName());
				setMainTitle(p.getMainTitleText());
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
		manager.removeEntities(ids);
		manager.write();
	}

	/**
	 * This method creates a temporary individual with the provided list of properties.
	 * Thereafter, types of the individual are retrieved with a reasoner and returned as @code{ReasonerReport}.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return A ReasonerReport which contains all found types of the individual.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	private ReasonerReport classifyIndividual(List<Property> properties) throws IllegalArgumentException {
		CompositePhenotypeInstance complex = new CompositePhenotypeInstance();

		for (Property property : properties) {
			if (StringUtils.isBlank(property.getName())) continue;
			Phenotype phenotype = manager.getPhenotype(property.getName());
			if (phenotype == null) continue;

			String value = property.getValue();
			String name  = property.getName();
			if (StringUtils.isBlank(value)) value = null;

			try {
				if (phenotype.isRestrictedPhenotype()) {
					if (phenotype.isRestrictedSinglePhenotype()) {
						complex.addSinglePhenotypeInstance(new SinglePhenotypeInstance(
							manager.getAbstractSinglePhenotype(phenotype.asRestrictedSinglePhenotype().getAbstractPhenotypeName()),
							manager.getRestrictedSinglePhenotype(name)));
					}
				} else if (value == null) { // skip
				} else if (phenotype.isAbstractSinglePhenotype()) {
					if (OWL2Datatype.XSD_DECIMAL.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
						complex.addSinglePhenotypeInstance(new SinglePhenotypeInstance(
							phenotype.asAbstractSinglePhenotype(),
							new DecimalValue(value, property.getObservationDate())));
					} else if (OWL2Datatype.XSD_DATE_TIME.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
						complex.addSinglePhenotypeInstance(new SinglePhenotypeInstance(
							phenotype.asAbstractSinglePhenotype(),
							new DateValue(value, property.getObservationDate())));
					} else if (OWL2Datatype.XSD_BOOLEAN.equals(phenotype.asAbstractSinglePhenotype().getDatatype())) {
						complex.addSinglePhenotypeInstance(new SinglePhenotypeInstance(
							phenotype.asAbstractSinglePhenotype(),
							new BooleanValue(Boolean.parseBoolean(value), property.getObservationDate())));
					} else {
						complex.addSinglePhenotypeInstance(new SinglePhenotypeInstance(
							phenotype.asAbstractSinglePhenotype(),
							new StringValue(value, property.getObservationDate())));
					}
				}
			} catch (ParseException e){
				throw new IllegalArgumentException("Could not parse Date from String '" + value + "'. " + e.getMessage());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Could not parse Number from String '" + value + "'.");
			} catch (FileNotFoundException | UcumException e) {
				e.printStackTrace();
			}
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
	@SuppressWarnings("unused")
	public String classifyIndividualAsString(List<Property> properties) throws IllegalArgumentException {
		return classifyIndividual(properties).getPhenotypes().stream().map(Phenotype::getMainTitleText).collect(Collectors.toList()).toString();
	}

	/**
	 * Returns the result of @code{classifyIndividual} as list.
	 *
	 * @param properties A list of properties, which will be used to create the individual.
	 * @return A list of class names.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	public List<String> classifyIndividualAsList(List<Property> properties) throws IllegalArgumentException {
		return classifyIndividual(properties).getPhenotypes().stream().map(Phenotype::getMainTitleText).collect(Collectors.toList());
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
	 * This method returns a list of reasoner reports for all patients fond by the specified inclusion criteria.
	 *
	 * @param properties A list of abstract and restricted phenotypes, which will be used to filter the patients.
	 * @return A List of ReasonerReports.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 * @throws FhirClientConnectionException If the connection to the FHIR server failed.
	 */
	private List<ReasonerReport> queryFhir(FhirProperties properties) throws IllegalArgumentException, FhirClientConnectionException {
//		if (StringUtils.isBlank(properties.getServerUrl()))
//			throw new IllegalArgumentException("No URL to FHIR server provided, but it is required.");
//
//		FHIRQuery query = manager.getFHIRQuery(properties.getServerUrl());
//
//		if (StringUtils.isNoneBlank(properties.getGender()))
//			query.setGender(properties.getGender());
//
//		if (StringUtils.isNoneBlank(properties.getMinAge(), properties.getMaxAge()))
//			query.setAgeRange(new DecimalRangeLimited().setMinInclusive(properties.getMinAge()).setMaxInclusive(properties.getMaxAge()));
//
//		if (StringUtils.isNoneBlank(properties.getMinDate(), properties.getMaxDate())) {
//			try {
//				query.setDateRange(new DateRangeLimited().setMinInclusive(properties.getMinDate()).setMaxInclusive(properties.getMaxDate()));
//			} catch (ParseException e) {
//				LOGGER.warn(e.getMessage());
//				throw new IllegalArgumentException("Could not parse Date.");
//			}
//		}
//
//		List<String> parameters = new ArrayList<>();
//		for (Property property : properties.getProperties()) {
//			if (StringUtils.isBlank(property.getName())) continue;
//			Phenotype phenotype = manager.getPhenotype(property.getName());
//			if (phenotype == null) continue;
//			parameters.add(property.getName());
//		}
//
//		query.setParameters(parameters.toArray(new String[0]));
//		List<Bundle> bundles = query.execute();
//
//		List<ReasonerReport> reports = new ArrayList<>();
//		try {
//			for (Bundle bundle : bundles) {
//				reports.add(manager.derivePhenotypes(bundle, PhenotypeReasoner.HERMIT).getFinalReport());
//			}
//		} catch (FileNotFoundException | UcumException e) {
//			LOGGER.warn(e.getMessage());
//			throw new FhirClientConnectionException("Could not retrieve data from FHIR server.");
//		}
//		return reports;
		throw new WebApplicationException("Not implemented");
	}

	/**
	 * Returns the result of @code{queryFhir} as HTML formatted string.
	 *
	 * @param properties A list of abstract and restricted phenotypes, which will be used to filter the patients.
	 * @return String representation of the reasoning result.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	@SuppressWarnings("unused")
	public String queryFhirAsString(FhirProperties properties) throws IllegalArgumentException {
		return queryFhirAsList(properties).toString();
	}

	/**
	 * Returns the result of @code{queryFhir} as list.
	 *
	 * @param properties A list of abstract and restricted phenotypes, which will be used to filter the patients.
	 * @return A list of class names.
	 * @throws IllegalArgumentException If a property value could not be parsed.
	 */
	public List<String> queryFhirAsList(FhirProperties properties) throws IllegalArgumentException {
		List<String> results = new ArrayList<>();

		for (ReasonerReport report : queryFhir(properties)) {
			results.addAll(report.getPhenotypes().stream().map(Phenotype::getMainTitleText).collect(Collectors.toList()));
		}

		return results;
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
		Graph graph;

		try {
			graph = manager.createAbstractPhenotypeGraph(phenotypeId);
		} catch (Exception e) {
			throw new IllegalArgumentException("Decision tree could not be generated! " + e.getMessage());
		}

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
	 *
	 * @return List of phenotype IDs.
	 */
	public List<String> getList() {
		return taxonomyAsList(manager.getEntityTree(true));
	}

	/**
	 * Returns the full RDF document for this ontology as string.
	 *
	 * @return string containing the full RDF document.
	 */
	public String getFullRdfDocument() throws IOException {
		return new String(Files.readAllBytes(Paths.get(phenotypePath)));
	}

	/**
	 * Returns a Microsoft Excel document containing the whole ontology.
	 * @return Microsoft Excel file
	 */
	public File getMicrosoftExcel() throws IOException {
		File xls = File.createTempFile("phenoman-", "-xls");
		manager.writePhenotypesToXLS(xls);
		return xls;
	}

	public void importArtDecorDataSet(String categoryId, String dataSetId) throws WrongPhenotypeTypeException, URISyntaxException, JAXBException, IOException {
		LOGGER.info(String.format("Importing Art-Decor data set %s into ontology...", dataSetId));
		manager.addArtDecorDataSet(dataSetId);
		LOGGER.info("Art-Decor data set import finished.");
	}


	private List<String> taxonomyAsList(EntityTreeNode node) {
		List<String> result = new ArrayList<>();
		result.add(node.getName());
		node.getChildren().forEach(c -> result.addAll(taxonomyAsList(c)));
		return result;
	}

	private TreeNode getTreeNode(EntityTreeNode node, Boolean includePhenotypes) {
		Entity       entity   = node.getEntity();
		String       text     = entity != null ? entity.getMainTitleText() : node.getName();
		OWL2Datatype datatype = getDatatype(entity);

		TreeNode treeNode = new TreeNode(node.getName(), text);
		treeNode.setType(owl2DatatypeToString(datatype, entity));

		if (entity != null) {
			StringBuilder tooltip = new StringBuilder();
			for (String lang : entity.getTitles().keySet()) {
				tooltip.append(String.format(
					"%s: %s (%s)" + System.lineSeparator(),
					lang, entity.getTitle(lang).getTitleText(),
					StringUtils.defaultString(entity.getTitle(lang).getAlias(), "none")
				));
			}
			treeNode.setTitle(tooltip.toString());

			if (entity.isRestrictedPhenotype()) {
				treeNode.setRestrictedPhenotype();
			} else if (entity.isAbstractPhenotype()) {
				treeNode.setAbstractPhenotype();
			} else {
				treeNode.setCategory();
			}

			if (entity.isAbstractSinglePhenotype() || entity.isRestrictedSinglePhenotype()) {
				treeNode.setSinglePhenotype();
				if (OWL2Datatype.XSD_DECIMAL.equals(datatype)) {
					treeNode.setNumericPhenotype();
				} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
					treeNode.setStringPhenotype();
				} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype) || OWL2Datatype.XSD_LONG.equals(datatype)) {
					treeNode.setDatePhenotype();
				} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
					treeNode.setBooleanPhenotype();
				}
			} else if (entity.isAbstractBooleanPhenotype() || entity.isRestrictedBooleanPhenotype()) {
				treeNode.setCompositeBooleanPhenotype();
			} else if (entity.isAbstractCalculationPhenotype() || entity.isRestrictedCalculationPhenotype()) {
				treeNode.setCalculationPhenotype();
			}
		} else treeNode.setCategory();

		node.getChildren().stream().sorted(Comparator.comparing(EntityTreeNode::getName)).forEach(child -> {
			if (includePhenotypes || !child.getEntity().isPhenotype())
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
	private String owl2DatatypeToString(OWL2Datatype datatype, Entity category) {
		if (datatype == null || category == null) {
			return null;
		}
		if (category.isAbstractBooleanPhenotype() || category.isRestrictedBooleanPhenotype()) {
			return "composite-boolean";
		} else if (category.isAbstractCalculationPhenotype() || category.isRestrictedCalculationPhenotype()) {
			return "calculation";
		} else if (OWL2Datatype.XSD_STRING.equals(datatype)) {
			return "string";
		} else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype)) {
			return "date";
		} else if (OWL2Datatype.XSD_DECIMAL.equals(datatype)) {
			return "numeric";
		} else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype)) {
			return "boolean";
		}
		return null;
	}

	/**
	 * Returns the OWL2Datatype of a category or phenotype.
	 *
	 * @param entity The category or phenotype object.
	 * @return The OWL2Datatype of the category.
	 */
	private OWL2Datatype getDatatype(Entity entity) {
		if (entity == null) {
			return null;
		}
		if (entity.isAbstractSinglePhenotype()) {
			return entity.asAbstractSinglePhenotype().getDatatype();
		} else if (entity.isRestrictedSinglePhenotype()) {
			return entity.asRestrictedSinglePhenotype().getDatatype();
		} else if (entity.isAbstractCalculationPhenotype() || entity.isRestrictedCalculationPhenotype()) {
			return OWL2Datatype.XSD_DECIMAL;
		} else if (entity.isAbstractBooleanPhenotype() || entity.isRestrictedBooleanPhenotype()) {
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
	private void addPhenotype(Phenotype phenotype) throws WrongPhenotypeTypeException {
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
		public String         text;
		public String         icon     = "";
		public State          state    = new State();
		public List<TreeNode> children = new ArrayList<>();
		public AttributeList  a_attr   = new AttributeList();

		@SuppressWarnings("unused")
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
			icon += " far fa-folder-open text-secondary";
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
			icon += " fas fa-calculator";
		}

		void setStringPhenotype() {
			icon += " fas fa-font";
		}

		void setDatePhenotype() {
			icon += " far fa-calendar-alt";
		}

		void setBooleanPhenotype() {
			icon += " far fa-check-circle";
		}

		void setCompositeBooleanPhenotype() {
			icon += " fas fa-check-circle";
		}

		void setCalculationPhenotype() {
			icon += " fas fa-calculator";
		}

		void addChild(TreeNode child) {
			children.add(child);
		}
	}

	public class AttributeList {
		public String       type;
		public String       id;
		public String       title;
		public List<String> categories;
		@SuppressWarnings("WeakerAccess")
		public Boolean      isPhenotype       = false;
		@SuppressWarnings("WeakerAccess")
		public Boolean      isRestricted      = false;
		@SuppressWarnings("WeakerAccess")
		public Boolean      isSinglePhenotype = false;
	}

	public class State {
		public Boolean opened   = false;
		public Boolean selected = false;
	}
}
