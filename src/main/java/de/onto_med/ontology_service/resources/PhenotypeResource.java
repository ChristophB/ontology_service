package de.onto_med.ontology_service.resources;

import de.onto_med.ontology_service.data_models.Individual;
import de.onto_med.ontology_service.data_models.Phenotype;
import de.onto_med.ontology_service.views.PhenotypeFormView;
import de.onto_med.ontology_service.views.RestApiView;

import org.apache.commons.lang3.StringUtils;
import org.lha.phenoman.man.PhenotypeOntologyManager;
import org.lha.phenoman.model.category_tree.PhenotypeCategoryTreeNode;
import org.lha.phenoman.model.phenotype.*;
import org.lha.phenoman.model.phenotype.top_level.AbstractPhenotype;
import org.lha.phenoman.model.phenotype.top_level.Category;
import org.lha.phenoman.model.phenotype.top_level.PhenotypeRange;
import org.lha.phenoman.model.phenotype.top_level.RestrictedPhenotype;
import org.semanticweb.owlapi.io.XMLUtils;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLFacet;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeCategoryTreeNode getCategories() {
		return manager.getPhenotypeCategoryTree(false);
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public PhenotypeCategoryTreeNode getPhenotypes() {
		return manager.getPhenotypeCategoryTree(true);
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

	@GET
	@Path("/phenotype-form")
	@Produces(MediaType.TEXT_HTML)
	public Response getPhenotypeForm(@QueryParam("type") String type, @QueryParam("id") String id) {
		Category phenotype = manager.getPhenotype(id);
		return Response.ok(new PhenotypeFormView(rootPath, new Phenotype(phenotype))).build();
	}

	@POST
	@Path("/create-abstract-phenotype")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createAbstractPhenotype(@BeanParam Phenotype formData) {
		PhenotypeFormView view = new PhenotypeFormView(rootPath, formData);

		AbstractPhenotype phenotype;
		switch (formData.getDatatype()) {
			case "numeric":
				OWL2Datatype datatype = formData.getIsDecimal() ? OWL2Datatype.XSD_DOUBLE : OWL2Datatype.XSD_INTEGER;
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
					? new AbstractBooleanPhenotype(formData.getId())
					: new AbstractBooleanPhenotype(formData.getId(), formData.getCategories().split(";"));
				break;
			case "calculated":
				if (StringUtils.isBlank(formData.getFormula())) {
					view.addMessage("danger", "Formula for abstract calculated phenotype is missing.");
					return Response.ok(view).build();
				}
				phenotype = StringUtils.isBlank(formData.getCategories())
					? new AbstractCalculationPhenotype(formData.getId(), formData.getFormula())
					: new AbstractCalculationPhenotype(formData.getId(), formData.getFormula(), formData.getCategories().split(";"));
				if (StringUtils.isNoneBlank(formData.getUcum()))
					phenotype.asAbstractCalculationPhenotype().setUnit(formData.getUcum());
				break;
			default:
				view.addMessage("danger", "Could not determine Datatype.");
				return Response.ok(view).build();
		}

		setPhenotypeBasicData(phenotype, formData);
		addPhenotype(phenotype);

		manager.write();
		view.addMessage("success", "Abstract phenotype '" + phenotype.getName() + "' created.");
		return Response.ok(view).build();
	}

	@POST
	@Path("/create-restricted-phenotype")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createRestrictedPhenotype(@Context HttpHeaders headers, @BeanParam Phenotype formData) {
		RestApiView view = new PhenotypeFormView(rootPath);

		if (StringUtils.isBlank(formData.getId()) || StringUtils.isBlank(formData.getSuperPhenotype())) {
			view.addMessage("danger", "ID or super phenotype is missing.");
			return Response.ok(view).build();
		}

		OWL2Datatype datatype = manager.getPhenotype(formData.getSuperPhenotype()).asAbstractSinglePhenotype().getDatatype();
		RestrictedPhenotype phenotype;
		switch (formData.getDatatype()) {
			case "numeric":
			case "date":
			case "string":
				phenotype = new RestrictedSinglePhenotype(
					formData.getId(), formData.getSuperPhenotype(),
					Optional.ofNullable(getRestrictedPhenotypeRange(
						datatype,
						formData.getRangeMin(), formData.getRangeMinOperator(),
						formData.getRangeMax(), formData.getRangeMinOperator()
					)).orElse(getRestrictedPhenotypeRange(datatype, formData.getEnumValues()))
				);
				break;
			case "expression":
				if (StringUtils.isBlank(formData.getExpression())) {
					view.addMessage("danger", "Boolean expression for restricted boolean phenotype is missing.");
					return Response.ok(view).build();
				}
				phenotype = new RestrictedBooleanPhenotype(
					formData.getId(), formData.getSuperPhenotype(),
					manager.getManchesterSyntaxExpression(formData.getExpression())
				);
				((RestrictedBooleanPhenotype) phenotype).setScore(formData.getScore());
				break;
			case "formula":
				phenotype = new RestrictedCalculationPhenotype(
					formData.getId(), formData.getSuperPhenotype(),
					Optional.ofNullable(getRestrictedPhenotypeRange(
						OWL2Datatype.XSD_DOUBLE, formData.getRangeMin(), formData.getRangeMinOperator(),
						formData.getRangeMax(), formData.getRangeMinOperator()
					)).orElse(getRestrictedPhenotypeRange(datatype, formData.getEnumValues()))
				);
				((RestrictedCalculationPhenotype) phenotype).setScore(formData.getScore());
				break;
			default:
				view.addMessage("danger", "Could not determine Datatype.");
				return Response.ok(view).build();
		}

		setPhenotypeBasicData(phenotype, formData);
		addPhenotype(phenotype);

		manager.write();
		view.addMessage("success", "Phenotype '" + formData.getId() + "' created.");
		return Response.ok(view).build();
	}

	@POST
	@Path("/create-phenotype-category")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	public Response createPhenotypeCategory(@BeanParam Phenotype formData) {
		PhenotypeFormView view = new PhenotypeFormView(rootPath, formData);
		// TODO: implement creation of categories
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
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(language)) phenotype.addLabel(label, language);
			else phenotype.addLabel(label);
		}
	}
	
	private void setPhenotypeDefinitions(Category phenotype, List<String> definitions, List<String> languages) {
		for (int i = 0; i < definitions.size(); i++) {
			String definition = definitions.get(i);
			if (StringUtils.isBlank(definition)) continue;
			String language = languages.get(i);
			if (StringUtils.isNoneBlank(definition)) phenotype.addDefinition(definition, language);
			else phenotype.addLabel(definition);
		}
	}
	
	private void setPhenotypeRelations(Category phenotype, List<String> relations) {
		for (String relation : relations)
			if (StringUtils.isNoneBlank(relation))
				phenotype.addRelatedConcept(relation);
	}

	/**
	 * Creates a single restricted phenotype by range for the given phenotype, if min and minOperator or max and maxOperator are valid.
	 * @param datatype The OWL2Datatype, which will be used to generate a PhenotypeRange.
	 * @param min Minimum of the range restriction.
	 * @param max Maximum of the range restriction.
	 * @param minOperator Operator for bottom border.
	 * @param maxOperator Operator for top border.
	 */
	private PhenotypeRange getRestrictedPhenotypeRange(OWL2Datatype datatype, String min, String max, String minOperator, String maxOperator) {
		List<OWLFacet> facets = new ArrayList<>();

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
				values.add(Date.valueOf(min));
			}
			if (StringUtils.isNoneBlank(max) && StringUtils.isNoneBlank(maxOperator)) {
				facets.add(OWLFacet.getFacetBySymbolicName(maxOperator));
				values.add(Date.valueOf(max));
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
		if (enumValues == null) return null;

		for (String value : enumValues) {
			if (StringUtils.isBlank(value)) continue;

			try {
				if (OWL2Datatype.XSD_INTEGER.equals(datatype))
					return new PhenotypeRange(Integer.valueOf(value));
				else if (OWL2Datatype.XSD_DOUBLE.equals(datatype))
					return new PhenotypeRange(Double.valueOf(value));
				else if (OWL2Datatype.XSD_DATE_TIME.equals(datatype))
					return new PhenotypeRange(Date.valueOf(value));
				else if (OWL2Datatype.XSD_STRING.equals(datatype))
					return new PhenotypeRange(value);
				else if (OWL2Datatype.XSD_BOOLEAN.equals(datatype))
					return new PhenotypeRange(Boolean.valueOf(value));
			} catch (Exception ignored) { }
		}
		return null;
	}

	/**
	 * This function checks if the provided Phenotype is one of
	 * RestrictedSinglePhenotype, RestrictedBooleanPhenotype, RestrictedCalculationPhenotype,
	 * AbstractSinglePhenotype, AbstractBooleanPhenotype, AbstractCalculationPhenotype
	 * and uses the appropriate function to add the phenotype to the manager.
	 * @param phenotype A phenotype which will be added to the manager.
	 */
	private void addPhenotype(Category phenotype) {
		if (phenotype.isAbstractSinglePhenotype()) {
			manager.addAbstractSinglePhenotype(phenotype.asAbstractSinglePhenotype());
		} else if (phenotype.isAbstractBooleanPhenotype()) {
			manager.addAbstractBooleanPhenotype(phenotype.asAbstractBooleanPhenotype());
		} else if (phenotype.isAbstractCalculationPhenotype()) {
			manager.addAbstractCalculationPhenotype(phenotype.asAbstractCalculationPhenotype());
		} else if (phenotype.isRestrictedCalculationPhenotype()) {
			manager.addRestrictedCalculationPhenotype(phenotype.asRestrictedCalculationPhenotype());
		} else if (phenotype.isRestrictedSinglePhenotype()) {
			manager.addRestrictedSinglePhenotype(phenotype.asRestrictedSinglePhenotype());
		} else if (phenotype.isRestrictedBooleanPhenotype()) {
			manager.addRestrictedBooleanPhenotype(phenotype.asRestrictedBooleanPhenotype());
		}
	}

}
