package de.onto_med.ontology_service.resources;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import de.onto_med.ontology_service.data_models.Individual;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;

import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.*;
import org.lha.phenoman.model.reasoner_result.RestrictedBooleanPhenotypeResult;
import org.lha.phenoman.model.top_level.AbstractPhenotype;
import org.lha.phenoman.model.top_level.RestrictedPhenotype;
import org.semanticweb.binaryowl.owlobject.serializer.OWLDataCardinalityRestrictionSerializer;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

@Path("/phenotype")
public class PhenotypeResource extends Resource {
	private String phenotypePath;
	
	private PhenotypeOntologyManager manager;
	
	public PhenotypeResource(String rootPath, String phenotypePath) {
		super(rootPath);
		this.phenotypePath = phenotypePath;
		manager = new PhenotypeOntologyManager(phenotypePath, false);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeView() {
		return Response.ok(new RestApiView("PhenotypeView.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/{iri}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPhenotype(@PathParam("iri") String iri) {
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(phenotypePath, false);
		return Response.ok(manager.getPhenotype(XMLUtils.getNCNameSuffix(iri))).build();
	}
	
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response getPhenotypeTaxonomy(@Context HttpHeaders headers, @QueryParam("type") String type) {
		PhenotypeOntologyManager manager = new PhenotypeOntologyManager(phenotypePath, false);
		
		if (acceptsMediaType(headers, MediaType.APPLICATION_JSON_TYPE)) {
			return Response.ok(manager.getPhenotypeCategoryTree(true)).build();
		} else {
			return Response.ok(new RestApiView("AllPhenotypes.ftl", rootPath)).build();
		}
	}
	
	@GET
	@Path("/decision-tree")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getDecisionTree(@QueryParam("phenotype") String phenotype, @QueryParam("language") String language) {
		if (StringUtils.isBlank(phenotype)) throw new WebApplicationException("Query parameter 'phenotype' missing.");
		
		// TODO: generate GraphML representation of the phenotypes decision tree
		// StringUtils.defaultString(language, "en");
		
		return Response.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename='" + phenotype + "_decisiontree.GraphML'").build();
	}
	
	@GET
	@Path("/decision-tree-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getDecisionTreeForm() {
		return Response.ok(new RestApiView("DecisionTreeForm.ftl", rootPath)).build();
	}

//	@GET
//	@Path("/numeric-phenotype-form")
//	@Produces(MediaType.TEXT_HTML)
//	public Response getNumericPhenotypeForm() {
//		return Response.ok(new PhenotypeFormView("NumericPhenotypeForm.ftl", rootPath)).build();
//	}

	@GET
	@Path("/singlephenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getSinglePhenotypeForm() { // TODO: add @BeanParam Phenotype phenotype
		return Response.ok(new PhenotypeFormView("SinglePhenotypeForm.ftl", rootPath)).build();
	}
	
	@GET
	@Path("/compositephenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getCompositePhenotypeForm() { // TODO: add @BeanParam Phenotype phenotype
		return Response.ok(new PhenotypeFormView("CompositePhenotypeForm.ftl", rootPath)).build();
	}
	
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createPhenotype(@Context HttpHeaders headers, @BeanParam Phenotype formData) {
		// TODO: redirect to form after creation succeded or failed and show message
		RestApiView view = new RestApiView(formData.getType().equals("single") ? "SinglePhenotypeForm.ftl" : "CompositePhenotypeForm.ftl", rootPath);
		
		/* form data validation */
		if (StringUtils.isBlank(formData.getId()) || StringUtils.isBlank(formData.getDatatype())) {
			view.addMessage("danger", "ID and/or data type is missing.");
			return Response.ok(view).build();
		}
		
		switch (formData.getDatatype()) {
			case "integer":
				createIntegerPhenotype(formData);
				break;
			case "double":
				createDoublePhenotype(formData);
				break;
			case "string":
				createStringPhenotype(formData);
				break;
			case "date":
				createDatePhenotype(formData);
				break;
			case "expression":
				if (StringUtils.isBlank(formData.getFormula())) {
					view.addMessage("danger", "Phenotype with Datatype 'Boolean Expression' requires an expression.");
					return Response.ok(view).build();
				}
				createBooleanPhenotype(formData);
				break;
			case "formula":
				if (StringUtils.isBlank(formData.getFormula())) {
					view.addMessage("danger", "Phenotype with Datatype 'Formula' requires a formula.");
					return Response.ok(view).build();
				}
				createCalculatedPhenotype(formData);
				break;
			default:
				view.addMessage("danger", "Could not determine Datatype.");
				return Response.ok(view).build();
		}
		
		manager.write();
		view.addMessage("success", "Phenotype '" + formData.getId() + "' created.");
		return Response.ok(view).build();
	}
	
	@POST
	@Path("/reason")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response classifyIndividuals(List<Individual> individuals) {
		for (Individual individual : individuals) {
			// TODO: implement reasoning in COP.owl
			individual.setClassification(new ArrayList<>());
		}
		return Response.ok(individuals).build();
	}
	
	private void createIntegerPhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_INTEGER);
		
		setPhenotypeBasicData(phenotype, formData);
		if (StringUtils.isNoneBlank(formData.getUcum())) phenotype.setUnit(formData.getUcum());
		manager.addAbstractSinglePhenotype(phenotype);

		setRestrictedPhenotypeRanges(
			phenotype, formData.getRangeMins(), formData.getRangeMinOperators(),
			formData.getRangeMaxs(), formData.getRangeMaxOperators(), formData.getRangeLabels()
		);
		setRestrictedPhenotypeEnums(phenotype, formData.getEnumValues(), formData.getEnumLabels());
	}

	private void createDoublePhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DOUBLE);
		
		setPhenotypeBasicData(phenotype, formData);
		if (StringUtils.isNoneBlank(formData.getUcum())) phenotype.setUnit(formData.getUcum());
		manager.addAbstractSinglePhenotype(phenotype);

		setRestrictedPhenotypeRanges(
			phenotype, formData.getRangeMins(), formData.getRangeMinOperators(),
			formData.getRangeMaxs(), formData.getRangeMaxOperators(), formData.getRangeLabels()
		);
		setRestrictedPhenotypeEnums(phenotype, formData.getEnumValues(), formData.getEnumLabels());
	}

	private void createDatePhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_DATE_TIME);

		setPhenotypeBasicData(phenotype, formData);
		manager.addAbstractSinglePhenotype(phenotype);

		setRestrictedPhenotypeRanges(
			phenotype, formData.getRangeMins(), formData.getRangeMinOperators(),
			formData.getRangeMaxs(), formData.getRangeMaxOperators(), formData.getRangeLabels()
		);
		setRestrictedPhenotypeEnums(phenotype, formData.getEnumValues(), formData.getEnumLabels());
	}
	
	private void createStringPhenotype(Phenotype formData) {
		AbstractSinglePhenotype phenotype = new AbstractSinglePhenotype(formData.getId(), OWL2Datatype.XSD_STRING);

		setPhenotypeBasicData(phenotype, formData);
		manager.addAbstractSinglePhenotype(phenotype);

		setRestrictedPhenotypeEnums(phenotype, formData.getEnumValues(), formData.getEnumLabels());
	}
	
	private void createCalculatedPhenotype(Phenotype formData) {
		AbstractCalculationPhenotype phenotype = new AbstractCalculationPhenotype(formData.getId(), formData.getFormula());

		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeCategories(phenotype, formData);
		setPhenotypeRelations(phenotype, formData.getRelations());
		if (StringUtils.isNoneBlank(formData.getUcum())) phenotype.setUnit(formData.getUcum());

		manager.addAbstractCalculationPhenotype(phenotype);

		setRestrictedPhenotypeRanges(
			phenotype, formData.getRangeMins(), formData.getRangeMinOperators(),
			formData.getRangeMaxs(), formData.getRangeMaxOperators(), formData.getRangeLabels()
		);
		setRestrictedPhenotypeEnums(phenotype, formData.getEnumValues(), formData.getEnumLabels());
	}
	
	private void createBooleanPhenotype(Phenotype formData) {
		AbstractBooleanPhenotype phenotype = new AbstractBooleanPhenotype(formData.getId());

		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeCategories(phenotype, formData);
		setPhenotypeRelations(phenotype, formData.getRelations());

		manager.addAbstractBooleanPhenotype(phenotype);

		setRestrictedPhenotypeBoolean(phenotype, formData.getBooleanTrueLabel(), formData.getBooleanFalseLabel());
	}

	private void setPhenotypeBasicData(AbstractPhenotype phenotype, Phenotype formData) {
		setPhenotypeLabels(phenotype, formData.getLabels(), formData.getLabelLanguages());
		setPhenotypeDefinitions(phenotype, formData.getDefinitions(), formData.getDefinitionLanguages());
		setPhenotypeCategories(phenotype, formData);
		setPhenotypeRelations(phenotype, formData.getRelations());
	}

	private void setPhenotypeLabels(AbstractPhenotype phenotype, List<String> labels, List<String> languages) {
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			if (StringUtils.isBlank(label)) continue;
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(language)) phenotype.addLabel(label, language);
			else phenotype.addLabel(label);
		}
	}
	
	private void setPhenotypeDefinitions(AbstractPhenotype phenotype, List<String> definitions, List<String> languages) {
		for (int i = 0; i < definitions.size(); i++) {
			String definition = definitions.get(i);
			if (StringUtils.isBlank(definition)) continue;
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(definition)) phenotype.addDefinition(definition, language);
			else phenotype.addLabel(definition);
		}
	}
	
	private void setPhenotypeCategories(AbstractPhenotype phenotype, Phenotype formData) { // TODO: do not use formData
		String category    = formData.getCategory();
		String newCategory = formData.getNewCategory(); 
		if (StringUtils.isNoneBlank(category)) {
			if (!category.equals("new_category"))
				phenotype.setPhenotypeCategories(category);
			else if (StringUtils.isNoneBlank(newCategory)) {
				manager.addPhenotypeCategory(newCategory);
				phenotype.setPhenotypeCategories(newCategory);
			}
		}
	}
	
	private void setPhenotypeRelations(AbstractPhenotype phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}

	/**
	 * Creates boolean restrictions for the given phenotype for True and/or False.
	 * @param phenotype The phenotype where te restricted phenotypes will be attached to.
	 * @param booleanTrueLabel Label for True.
	 * @param booleanFalseLabel Label for False.
	 */
	private void setRestrictedPhenotypeBoolean(AbstractBooleanPhenotype phenotype, String booleanTrueLabel, String booleanFalseLabel) {
		HashMap<String, String> map = new HashMap<>();
		map.put(booleanTrueLabel, "true");
		map.put(booleanFalseLabel, "false");

		for (String label : map.keySet()) {
			if (StringUtils.isBlank(label)) continue;
			manager.addRestrictedBooleanPhenotype(new RestrictedBooleanPhenotype(label, phenotype.getName(), map.get(label)));
		}
	}

	/**
	 * Creates multiple restricted phenotypes and adds them to the provided phenotype.
	 * @param phenotype The phenotype where the restricted phenotypes will be added to.
	 * @param rangeMins List of restricting range minimas.
	 * @param rangeMinOperators List of operators for the bottom borders of the ranges.
	 * @param rangeMaxs List of restricting range maximas.
	 * @param rangeMaxOperators List of operators for the top borders of the ranges.
	 * @param rangeLabels Labels for the restricted ranges.
	 */
	private void setRestrictedPhenotypeRanges(
		AbstractPhenotype phenotype, List<String> rangeMins, List<String> rangeMinOperators,
		List<String> rangeMaxs, List<String> rangeMaxOperators, List<String> rangeLabels
	) {
		for (int i = 0; i < Math.max(rangeMins.size(), rangeMaxs.size()); i++) {
			String min = getElementAt(rangeMins, i);
			String max = getElementAt(rangeMaxs, i);
			String minOperator = getElementAt(rangeMinOperators, i);
			String maxOperator = getElementAt(rangeMaxOperators, i);
			String label = rangeLabels.get(i);

			if (StringUtils.isBlank(min) && StringUtils.isBlank(max)
				|| StringUtils.isBlank(minOperator) && StringUtils.isBlank(maxOperator)
			) continue;

			RestrictedSinglePhenotype restrictedPhenotype = StringUtils.isBlank(label)
				? new RestrictedSinglePhenotype(phenotype.getName())
				: new RestrictedSinglePhenotype(label, phenotype.getName());
			restrictedPhenotype.setDatatype(OWL2Datatype.XSD_INTEGER);

			setRestrictedPhenotypeRange(restrictedPhenotype, min, max, minOperator, maxOperator);

			manager.addRestrictedSinglePhenotype(restrictedPhenotype);
		}
	}

	/**
	 * Creates a single restricted phenotype by range for the given phenotype, if min and minOperator or max and maxOperator are valid.
	 * @param phenotype The phenotype where the created restricted phenotype will be attached to.
	 * @param min Minimum of the range restriction.
	 * @param max Maximum of the range restriction.
	 * @param minOperator Operator for bottom border.
	 * @param maxOperator Operator for top border.
	 */
	private void setRestrictedPhenotypeRange(RestrictedPhenotype phenotype, String min, String max, String minOperator, String maxOperator) {
		List<OWLFacet> facets = new ArrayList<>();
		OWL2Datatype datatype = phenotype.getDatatype();

		if (datatype.equals(OWL2Datatype.XSD_INTEGER)) {
			List<Integer> values = new ArrayList<>();
			
			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Integer.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Integer.valueOf(max));
			}
			((RestrictedSinglePhenotype) phenotype).setRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Integer[values.size()]));
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
			((RestrictedSinglePhenotype) phenotype).setRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Double[values.size()]));
		} else if (datatype.equals(OWL2Datatype.XSD_DATE_TIME)) {
			List<Date> values = new ArrayList<>();

			if (StringUtils.isNoneBlank(min) && StringUtils.isNoneBlank(minOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(minOperator));
				values.add(Date.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Date.valueOf(max));
			}
			((RestrictedSinglePhenotype) phenotype).setRange(facets.toArray(new OWLFacet[facets.size()]), values.toArray(new Date[values.size()]));
		} else throw new WebApplicationException("Invalid phenotype type supplied to setRestrictedRange() method.");
	}

	/**
	 * Creates potentially multiple restricted phenotypes depending on the values and labels lists
	 * and adds them to the provided phenotype.
	 * @param phenotype The phenotype object where the created restricted phenotypes will be attached to.
	 * @param enumValues A list of enumeration values. (May contain only one value)
	 * @param enumLabels A list of enumeration labels.
	 */
	private void setRestrictedPhenotypeEnums(AbstractPhenotype phenotype, List<String> enumValues, List<String> enumLabels) {
		for (int i = 0; i < Math.max(enumValues.size(), enumLabels.size()); i++) {
			String values = getElementAt(enumValues, i);
			String label  = getElementAt(enumLabels, i);

			if (StringUtils.isBlank(values)) continue;

			for (String value : values.split(";")) {
				RestrictedSinglePhenotype restrictedPhenotype = StringUtils.isBlank(label)
						? new RestrictedSinglePhenotype(phenotype.getName())
						: new RestrictedSinglePhenotype(label, phenotype.getName());

				OWL2Datatype datatype = phenotype.getDatatype();
				try {
					if (datatype.equals(OWL2Datatype.XSD_INTEGER)) {
						restrictedPhenotype.setValue(Integer.valueOf(value));
					} else if (datatype.equals(OWL2Datatype.XSD_DOUBLE))
						restrictedPhenotype.setValue(Double.valueOf(value));
					else if (datatype.equals(OWL2Datatype.XSD_DATE_TIME))
						restrictedPhenotype.setValue(Date.valueOf(value));
					else if (datatype.equals(OWL2Datatype.XSD_STRING))
						restrictedPhenotype.setValue(value);
					else continue;
				} catch (Exception e) { continue; }

				manager.addRestrictedSinglePhenotype(restrictedPhenotype);
			}
		}
	}

	/**
	 * Returns an element from a list with the specified index, or null if the index is not present in the list.
	 * @param list A list of objects where the index is applied to.
	 * @param index Index of the requested list element.
	 * @param <T> Class of the objects in the list.
	 * @return Element in the list at provided index or null if index is invalid.
	 */
	private <T> T getElementAt(List<T> list, int index) {
		return list.size() > index ? list.get(index) : null;
	}
}
